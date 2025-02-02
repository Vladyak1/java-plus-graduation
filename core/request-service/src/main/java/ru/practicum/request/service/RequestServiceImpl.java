package ru.practicum.request.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.core.api.client.EventServiceClient;
import ru.practicum.core.api.client.UserServiceClient;
import ru.practicum.core.api.dto.event.EventFullDto;
import ru.practicum.core.api.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.core.api.dto.request.ParticipationRequestDto;
import ru.practicum.core.api.dto.user.UserDto;
import ru.practicum.core.api.enums.EventState;
import ru.practicum.core.api.enums.RequestStatus;
import ru.practicum.core.api.exception.AccessException;
import ru.practicum.core.api.exception.ConflictException;
import ru.practicum.core.api.exception.NotFoundException;
import ru.practicum.request.controller.PrivateUpdateRequestParams;
import ru.practicum.request.entity.Request;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.repository.RequestRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final UserServiceClient userServiceClient;
    private final EventServiceClient eventServiceClient;
    private final RequestMapper requestMapper;

    @Override
    public ParticipationRequestDto create(long userId, long eventId) {
        UserDto user = userServiceClient.getById(userId);
        EventFullDto event = eventServiceClient.getById(eventId);

        long confirmedRequests = requestRepository.countByStatusAndEventId(RequestStatus.CONFIRMED, eventId);

        if (userId == event.initiator().id()) {
            throw new ConflictException("Initiator can't request in own event");
        }
        if (!event.state().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Event is not published");
        }
        if (event.participantLimit() != 0 && event.participantLimit() == confirmedRequests) {
            throw new ConflictException("There is no empty place for this event");
        }

        Request creatingRequest = requestMapper.toRequest(user, event);

        if (!event.requestModeration() || event.participantLimit() == 0) {
            creatingRequest.setStatus(RequestStatus.CONFIRMED);
        }

        Request receivedRequest = requestRepository.save(creatingRequest);

        return requestMapper.toParticipationRequestDto(receivedRequest);
    }

    @Override
    public List<ParticipationRequestDto> getAllOwnRequests(long userId) {
        log.debug("method getAllOwnRequests with user {}", userId);
        userServiceClient.checkExistence(userId);
        log.debug("User with id {} exist", userId);
        List<Request> receivedRequests = requestRepository.getAllByRequesterId(userId);
        log.debug("receivedRequests if user with id {}: {}", userId, receivedRequests);
        return receivedRequests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    public ParticipationRequestDto cancel(long userId, long requestId) {
        userServiceClient.checkExistence(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id " + requestId + " not found"));
        request.setStatus(RequestStatus.CANCELED);
        Request canceledRequest = requestRepository.save(request);

        return requestMapper.toParticipationRequestDto(canceledRequest);
    }

    @Override
    public List<ParticipationRequestDto> getAllForOwnEvent(long userId, long eventId) {

        UserDto user = userServiceClient.getById(userId);
        EventFullDto event = eventServiceClient.getById(eventId);

        log.info("getAllForOwnEvent. Пользователь полученный из сервиса user: {} ", user);
        log.info("getAllForOwnEvent. Событие полученное из сервиса event: {} ", event);
        log.info("getAllForOwnEvent. Входящие параметры метода getAllForOwnEvent. UserId: {}, EventId: {}", userId, eventId);


        if (!Objects.equals(event.initiator().id(), user.id())) {
            throw new AccessException("User with id " + userId + " is not own event");
        }

        List<Request> receivedEventsList = requestRepository.getAllByEventId(eventId);

        return receivedEventsList.stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateStatus(PrivateUpdateRequestParams params) {
        UserDto user = userServiceClient.getById(params.userId());
        EventFullDto event = eventServiceClient.getById(params.eventId());

        log.info("updateStatus. Пользователь полученный из сервиса user: {} ", user);
        log.info("updateStatus. Событие полученное из сервиса event: {} ", event);
        log.info("updateStatus. Входящие параметры метода updateStatus. PrivateUpdateRequestParams: {}", params);

        if (!Objects.equals(event.initiator().id(), user.id())) {
            throw new AccessException("User with id " + params.userId() + " is not own event");
        }

        List<Request> requestListOfEvent = //Получение всех изменяемых реквестов события
                requestRepository.findAllByIdInAndEventId(
                        params.eventRequestStatusUpdateRequest().requestIds(), params.eventId());

        long confirmedRequestsCount = //Получение количества подтвержденных запросов события.
                requestRepository.countByStatusAndEventId(RequestStatus.CONFIRMED, params.eventId());

        for (Request request : requestListOfEvent) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Request status is not PENDING");
            }
            if (confirmedRequestsCount >= event.participantLimit()) {
                throw new ConflictException("Participant limit exceeded");
            }
        }

        // Если требуется модерация, обрабатываем все обновления разом
        if (event.requestModeration()) {
            RequestStatus newStatus = params.eventRequestStatusUpdateRequest().status();
            List<Long> requestIds = requestListOfEvent.stream()
                    .map(Request::getId)
                    .collect(Collectors.toList());
            // Пакетное обновление всех запросов с новым статусом
            if (!requestIds.isEmpty()) {
                requestRepository.updateStatusBatch(newStatus.toString(), requestIds);
                // Если будут превышены подтверждающие запросы и лимит
                if (newStatus == RequestStatus.CONFIRMED) {
                    long newConfirmedCount = confirmedRequestsCount + requestIds.size();
                    if (newConfirmedCount >= event.participantLimit()) {
                        //Отменяем оставшиеся незавершенные запросы
                        requestRepository.cancelNewRequestsStatus(event.id());
                    }
                }
            }
        }

        List<ParticipationRequestDto> confirmedRequestsDtoList =
                requestRepository.findAllByStatus(RequestStatus.CONFIRMED)
                        .stream()
                        .filter(request -> request.getEventId().equals(event.id()))
                        .map(requestMapper::toParticipationRequestDto)
                        .toList();
        List<Request> rejectedRequests = requestRepository.findAllByStatus(RequestStatus.REJECTED);
        for (Request request : rejectedRequests) {
            log.debug("{} id, status: {}", request.getId(), request.getStatus());
        }

        List<ParticipationRequestDto> rejectedRequestsDtoList =
                rejectedRequests
                        .stream()
                        .filter(request -> request.getEventId().equals(event.id()))
                        .map(requestMapper::toParticipationRequestDto)
                        .toList();

        return new EventRequestStatusUpdateResult(confirmedRequestsDtoList, rejectedRequestsDtoList);
    }

    @Override
    public long countByStatusAndEventId(RequestStatus status, long eventId) {
        return requestRepository.countByStatusAndEventId(status, eventId);
    }

    @Override
    public Map<Long, Long> countByStatusAndEventsIds(RequestStatus status, List<Long> eventsIds) {

        List<Map<String,Long>> list = requestRepository.countByStatusAndEventsIds(RequestStatus.CONFIRMED.toString(), eventsIds);
        Map<Long, Long> eventRequestsWithStatus = new HashMap<>();
        for (Map<String, Long> row : list) {
            Long eventId = row.get("EVENT_ID");
            Long statusCount = row.get("EVENT_COUNT");
            eventRequestsWithStatus.put(eventId, statusCount);
        }
        return eventRequestsWithStatus;
    }


}
