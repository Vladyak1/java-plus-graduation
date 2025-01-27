package ru.practicum.service.impl;


import client.UserServiceClient;
import com.querydsl.core.types.dsl.BooleanExpression;

import ru.practicum.dto.user.AdminUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.enums.EventState;
import client.EventServiceClient;
import exception.ConflictException;
import exception.NotFoundException;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.enums.RequestStatus;
import ru.practicum.model.mapper.RequestsMapper;
import ru.practicum.repository.RequestsRepository;
import ru.practicum.service.RequestsService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestsServiceImpl implements RequestsService {

    private final RequestsRepository requestsRepository;
    private final EventServiceClient eventService;
    private final UserServiceClient userService;

    @Override
    public List<ParticipationRequestDto> getRequest(Long userId) {
        return requestsRepository.findByRequesterId(userId).stream()
                .map(RequestsMapper.REQUESTS_MAPPER::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto setRequest(Long eventId, Long userId) {

        EventFullDto event = eventService.getEventById(eventId);
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("You can't send request to your own event");
        }
        if (!Objects.equals(event.getState(), EventState.PUBLISHED.toString())) {
            throw new ConflictException("Event is not published");
        }
        int confirmedRequests = requestsRepository.findByEventIdAndStatus(eventId, RequestStatus.CONFIRMED).size();
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= confirmedRequests) {
            throw new ConflictException("Participant limit reached");
        }
        AdminUserDto user = userService.findUserById(userId);
        RequestStatus status = RequestStatus.PENDING;
        if (Boolean.TRUE.equals(!event.getRequestModeration()) || event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
        }
        ParticipationRequest participationRequest = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .status(status)
                .eventId(event.getId())
                .requesterId(user.getId())
                .build();

        Optional<ParticipationRequest> check = requestsRepository.findByEventIdAndRequesterId(eventId, userId);
        if (check.isPresent()) throw new ConflictException("You already have request to event");

        participationRequest = requestsRepository.save(participationRequest);
        return RequestsMapper.REQUESTS_MAPPER.toParticipationRequestDto(participationRequest);
    }

    @Override
    public ParticipationRequestDto updateRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestsRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(() ->
                new NotFoundException("Request not found"));
        request.setStatus(RequestStatus.CANCELED);
        return RequestsMapper.REQUESTS_MAPPER.toParticipationRequestDto(requestsRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getAllByEventId(Long eventId) {
        return requestsRepository.findAllByEventId(eventId).stream()
                .map(RequestsMapper.REQUESTS_MAPPER::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public List<ParticipationRequestDto> getAllByRequestIdIn(List<Long> requestsId) {
        return requestsRepository.findAllByIdIn(requestsId).stream()
                .map(RequestsMapper.REQUESTS_MAPPER::toParticipationRequestDto)
                .collect(Collectors.toList());
    }


    public Iterable<ParticipationRequest> findAll(BooleanExpression conditions) {
        return requestsRepository.findAll((Pageable) conditions);
    }

    public List<ParticipationRequest> saveAll(List<ParticipationRequest> requests) {
        return requestsRepository.saveAll(requests);
    }

}