package event.service.impl;

import dto.user.AdminUserDto;
import event.dto.*;
import event.model.Event;
import event.model.Location;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import category.model.Category;
import category.service.CategoryService;
import event.model.enums.EventSort;
import event.model.enums.EventState;
import event.model.enums.StateActionForAdmin;
import event.model.enums.StateActionForUser;
import event.repository.EventRepository;
import event.service.EventService;
import exception.DataConflictRequest;
import exception.InvalidRequestException;
import exception.NotFoundException;
import dto.request.EventRequestStatusUpdateRequest;
import dto.request.EventRequestStatusUpdateResult;
import dto.request.ParticipationRequestDto;
import dto.request.enums.RequestStatus;
import client.RequestServiceClient;
import stat.service.MainStatsService;
import client.UserServiceClient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@NoArgsConstructor(force = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserServiceClient userService;
    private final RequestServiceClient requestService;
    private final CategoryService categoryService;
    private final EventMapper eventMapper;
    private final MainStatsService mainStatsService;

    @Autowired
    @Lazy
    public EventServiceImpl(EventRepository eventRepository, UserServiceClient userService,
                            RequestServiceClient requestService,
                            CategoryService categoryService, EventMapper eventMapper,
                            MainStatsService mainStatsService) {
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.requestService = requestService;
        this.categoryService = categoryService;
        this.eventMapper = eventMapper;
        this.mainStatsService = mainStatsService;
    }

    public List<EventShortDto> getAllEventOfUser(Long userId, Integer from, Integer size) {
        List<EventShortDto> eventsOfUser;
        userService.findUserById(userId);
        List<Event> events = eventRepository.findByInitiatorIdOrderByIdDesc(userId, PageRequest.of(from / size, size));
        eventsOfUser = events.stream().map(eventMapper::toEventShortDto).collect(Collectors.toList());
        log.info("Получение всех событий пользователя с ID = {}", userId);
        return eventsOfUser;
    }

    @Transactional
    public EventLongDto createEvent(Long userId, NewEventDto newEventDto) {
        AdminUserDto initiator = userService.findUserById(userId);
        Category category = categoryService.getCategoryByIdNotMapping(newEventDto.getCategory());
        Event event = eventMapper.toEvent(newEventDto);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InvalidRequestException("The date and time for which the event is scheduled cannot be earlier " +
                    "than two hours from the current moment");
        }
        event.setInitiator(initiator);
        event.setCategory(category);
        event.setConfirmedRequests(0L);
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        event.setPublishedOn(LocalDateTime.now());
        event = eventRepository.save(event);
        EventLongDto eventFullDto = eventMapper.toLongDto(event);
        log.info("Событию присвоен ID = {}, и оно успешно добавлено", event.getId());
        return eventFullDto;
    }

    public EventFullDto getEventOfUserById(Long userId, Long eventId) {
        userService.findUserById(userId);
        Optional<Event> optEventSaved = eventRepository.findByIdAndInitiatorId(eventId, userId);
        EventFullDto eventFullDto;
        if (optEventSaved.isPresent()) {
            eventFullDto = eventMapper.toEventFullDto(optEventSaved.get());
        } else {
            throw new NotFoundException("The required object was not found.");
        }
        //Получаем и добавляем просмотры
        Map<Long, Long> views = mainStatsService.getView(List.of(eventFullDto).stream()
                .map(EventFullDto::getId)
                .collect(Collectors.toList()), true);

        eventFullDto.setViews(views.get(eventFullDto.getId()));
        //

        log.info("Выполнен поиск события с ID = {}", eventId);
        return eventFullDto;
    }

    @Transactional
    public EventLongDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEvent) {
        userService.findUserById(userId);
        Optional<Event> optEventSaved = eventRepository.findByIdAndInitiatorId(eventId, userId);
        Event eventSaved;
        if (optEventSaved.isPresent()) {
            eventSaved = optEventSaved.get();
        } else {
            throw new NotFoundException("Event with ID = " + eventId + " was not found");
        }

        if (eventSaved.getState().equals(EventState.PUBLISHED)) {
            throw new DataConflictRequest("It is not possible to make changes to an already published event.");
        }

        if (updateEvent.getEventDate() != null) {
            if (LocalDateTime.parse(updateEvent.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    .isBefore(LocalDateTime.now().plusHours(2))) {
                throw new InvalidRequestException("The start date of the event to be modified must be no earlier " +
                        "than two hours from the date of publication.");
            } else {
                eventSaved.setEventDate(LocalDateTime.parse(updateEvent.getEventDate(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
        }

        if (updateEvent.getStateAction() != null) {
            updateStateOfEventByUser(updateEvent.getStateAction(), eventSaved);
        }

        if (updateEvent.getAnnotation() != null) {
            eventSaved.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {
            Category category = categoryService.getCategoryByIdNotMapping(updateEvent.getCategory());
            eventSaved.setCategory(category);
        }
        checkParams(eventSaved, updateEvent.getDescription(), updateEvent.getLocation(),
                updateEvent.getParticipantLimit(), eventSaved.getParticipantLimit(),
                updateEvent.getPaid(), updateEvent.getRequestModeration(),
                updateEvent.getTitle());

        Event eventUpdate = eventRepository.save(eventSaved);

        EventLongDto eventFullDto = eventMapper.toLongDto(eventUpdate);
        log.info("Событие ID = {} пользователя ID = {} успешно обновлено", eventId, userId);
        return eventFullDto;
    }

    // Метод для UpdateEventUserRequest
    private void checkParams(Event eventSaved, String description, Location location, Integer participantLimit,
                             Integer participantLimit2, Boolean paid, Boolean requestModeration, String title
                             ) {
        if (description != null) {
            eventSaved.setDescription(description);
        }
        if (location != null) {
            eventSaved.setLat(location.getLat());
            eventSaved.setLon(location.getLon());
        }
        if (participantLimit != null) {
            eventSaved.setParticipantLimit(participantLimit2);
        }
        if (paid != null) {
            eventSaved.setPaid(paid);
        }
        if (requestModeration != null) {
            eventSaved.setRequestModeration(requestModeration);
        }
        if (title != null) {
            eventSaved.setTitle(title);
        }
    }

    public List<ParticipationRequestDto> getRequestEventByUser(Long userId, Long eventId) {
        userService.findUserById(userId);
        getEventById(eventId);
        List<ParticipationRequestDto> requests = requestService.getAllByEventId(eventId);
        log.info("Получен список заявок на участие в событии с ID = {} пользователя с ID = {}", eventId, userId);
        return requests;
    }

    @Transactional
    public EventRequestStatusUpdateResult changeRequestEventStatus(Long userId, Long eventId,
                                                                   EventRequestStatusUpdateRequest requestUpdate) {
        userService.findUserById(userId);
        Event event = getEventById(eventId);
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new RuntimeException("Пользователь с ID = " + userId + " не является инициатором события с ID = " + eventId);
        }

        List<ParticipationRequestDto> requests = requestService.getAllByRequestIdIn(requestUpdate.getRequestIds()); // получаем список запросов на одобрение
        RequestStatus newStatus = RequestStatus.valueOf(requestUpdate.getStatus()); // получаем значение нового статуса
        Integer countOfRequest = requestUpdate.getRequestIds().size(); // находим количество новых заявок на одобрение

        for (ParticipationRequestDto request : requests) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new DataConflictRequest("Изменить статус можно только у ожидающей подтверждения заявки на " +
                        "участие");
            }
        }

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        switch (newStatus) {
            case CONFIRMED:
                if ((event.getParticipantLimit() == 0) ||
                        ((event.getConfirmedRequests() + countOfRequest.longValue()) < event.getParticipantLimit()) ||
                        (!event.getRequestModeration())) {
                    requests.forEach(request -> request.setStatus(RequestStatus.CONFIRMED.toString()));
                    event.setConfirmedRequests(event.getConfirmedRequests() + countOfRequest);
                    confirmedRequests.addAll(requests);
                } else if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
                    throw new DataConflictRequest("The limit on applications for this event has been reached");
                } else {
                    for (ParticipationRequestDto request : requests) {
                        if (event.getConfirmedRequests() < event.getParticipantLimit()) {
                            request.setStatus(RequestStatus.CONFIRMED.toString());
                            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                            confirmedRequests.add(request);
                        } else {
                            request.setStatus(RequestStatus.REJECTED.toString());
                            rejectedRequests.add(request);
                        }
                    }
                }
                break;
            case REJECTED:
                for (ParticipationRequestDto request : requests) {
                    request.setStatus(RequestStatus.REJECTED.toString());
                    rejectedRequests.add(request);
                }
                break;
        }
        eventRepository.save(event);
        requestService.saveAll(requests);
        log.info("Статусы заявок успешно обновлены");
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    @Override
    public List<EventLongDto> getAllEventsByAdmin(EventAdminParams param) {
        log.info("Запрос от администратора на получение событий");

        List<Event> events = eventRepository.searchEventsForAdmin(param);
        Map<Long, Long> view = getView(events, false);
        return events.stream()
                .map(e -> eventMapper.toLongDto(e, view.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Transactional
    public EventLongDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEvent) {
        Optional<Event> optEventSaved = eventRepository.findById(eventId);
        Event eventSaved;
        if (optEventSaved.isPresent()) {
            eventSaved = optEventSaved.get();
        } else {
            throw new NotFoundException("Event with ID = " + eventId + " was not found");
        }
        if (updateEvent.getEventDate() != null) {
            updateEventData(updateEvent.getEventDate(), eventSaved);
        }
        if (updateEvent.getStateAction() != null) {
            updateStateOfEventByAdmin(updateEvent.getStateAction(), eventSaved);
        }
        if (updateEvent.getAnnotation() != null) {
            eventSaved.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {
            assert categoryService != null;
            Category category = categoryService.getCategoryByIdNotMapping(updateEvent.getCategory());
            eventSaved.setCategory(category);
        }
        checkParams(eventSaved, updateEvent.getDescription(), updateEvent.getLocation(), updateEvent.getParticipantLimit(), updateEvent.getParticipantLimit(), updateEvent.getPaid(), updateEvent.getRequestModeration(), updateEvent.getTitle());

        eventSaved = eventRepository.save(eventSaved);

        assert eventMapper != null;
        EventLongDto eventFullDto = eventMapper.toLongDto(eventSaved);

        log.info("Событие ID = {} успешно обновлено от имени администратора", eventId);
        return eventFullDto;
    }


    @Override
    public List<EventShortDto> getPublicEvents(EventPublicParams param) {
        log.info("Запрос получить опубликованные события");

        if (param.getRangeStart().isAfter(param.getRangeEnd())) {
            log.error("NotValid. При поиске опубликованных событий rangeStart после rangeEnd.");
            throw new InvalidRequestException("The start of the range must be before the end of the range.");
        }

        assert eventRepository != null;
        List<Event> events = eventRepository.searchPublicEvents(param);

        Comparator<EventShortDto> comparator = Comparator.comparing(EventShortDto::getId);

        if (param.getSort() != null) {
            // Сравниваем строки с именами элементов перечисления EventSort
            if (param.getSort().equals(EventSort.EVENT_DATE.name())) {
                comparator = Comparator.comparing(EventShortDto::getEventDate);
            } else if (param.getSort().equals(EventSort.VIEWS.name())) {
                comparator = Comparator.comparing(EventShortDto::getViews, Comparator.reverseOrder());
            }
        }

        Map<Long, Long> view = getView(events, false);
        return events.stream()
                .map(e -> {
                    assert eventMapper != null;
                    return eventMapper.toShortDto(e, view.getOrDefault(e.getId(), 0L));
                })
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    private Map<Long, Long> getView(List<Event> events, boolean unique) {
        if (!events.isEmpty()) {
            List<Long> eventsId = events.stream()
                    .map(Event::getId)
                    .collect(Collectors.toList());
            assert mainStatsService != null;
            return mainStatsService.getView(eventsId, unique);
        } else return new HashMap<>();
    }

    public EventLongDto getEventDtoById(Long id, HttpServletRequest httpServletRequest) {

        assert eventRepository != null;
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event must be published"));

        assert eventMapper != null;
        EventLongDto eventLongDto = eventMapper.toLongDto(event);

        log.info("Событие ID = {} успешно обновлено от имени администратора", id);
        return eventLongDto;
    }

    public EventFullDto getEventDtoByIdWithHit(Long id, HttpServletRequest httpServletRequest) {

        assert eventRepository != null;
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event must be published"));

        List<Long> eventViews = event.getViews();
        if (eventViews == null) {
            eventViews = new ArrayList<>();
        }
        if (!eventViews.contains(id)) {
            eventViews.add(id);
            event.setViews(eventViews);
            eventRepository.save(event);
        }

        assert eventMapper != null;
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        eventFullDto.setViews(Long.valueOf(eventViews.size()));

        log.info("Событие ID = {} успешно обновлено от имени администратора", id);
        return eventFullDto;
    }


    // ----- Вспомогательная часть ----

    // Вспомогательная функция обновления статуса
    private void updateStateOfEventByUser(String stateAction, Event eventSaved) {
        StateActionForUser stateActionForUser;
        try {
            stateActionForUser = StateActionForUser.valueOf(stateAction);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid parameter stateAction");
        }
        switch (stateActionForUser) {
            case SEND_TO_REVIEW:
                eventSaved.setState(EventState.PENDING);
                break;
            case CANCEL_REVIEW:
                eventSaved.setState(EventState.CANCELED);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + stateAction);
        }
    }

    // Вспомогательная функция обновления статуса и время публикации
    private void updateStateOfEventByAdmin(String stateAction, Event eventSaved) {
        StateActionForAdmin stateActionForAdmin;
        try {
            stateActionForAdmin = StateActionForAdmin.valueOf(stateAction);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid parameter stateAction");
        }
        switch (stateActionForAdmin) {
            case REJECT_EVENT:
                if (eventSaved.getState().equals(EventState.PUBLISHED)) {
                    throw new DataConflictRequest("The event has already been published.");
                }
                eventSaved.setState(EventState.CANCELED);
                break;
            case PUBLISH_EVENT:
                if (!eventSaved.getState().equals(EventState.PENDING)) {
                    throw new DataConflictRequest("Cannot publish the event because it's not in the right state: PUBLISHED");
                }
                eventSaved.setState(EventState.PUBLISHED);
                eventSaved.setPublishedOn(LocalDateTime.now());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + stateAction);
        }
    }

    // Получение event по id

    public Event getEventById(Long eventId) {
        assert eventRepository != null;
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isPresent()) {
            return eventOptional.get();
        }
        throw new NotFoundException("Event with ID = " + eventId + " was not found");
    }


    public void addRequestToEvent(Event event) {
        assert eventRepository != null;
        eventRepository.save(event);
    }

//    // Собираем условие по которому будем выбирать события из базы данных для публичного запроса
//    private Predicate makeEventsQueryConditionsForPublic(EventPublicParams request) {
//        QEvent event = QEvent.event;
//        BooleanExpression condition = event.isNotNull(); // Базовое условие
//
//        if (request.getText() != null && !request.getText().isEmpty()) {
//            String searchText = "%" + request.getText().toLowerCase() + "%";
//            condition = condition.and(
//                    event.title.likeIgnoreCase(searchText)
//                            .or(event.annotation.likeIgnoreCase(searchText))
//                            .or(event.description.likeIgnoreCase(searchText))
//            );
//        }
//
//        // Добавление других условий фильтрации (категории, платность и т.д.)
//        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
//            condition = condition.and(event.category.id.in(request.getCategories()));
//        }
//
//        if (request.getPaid() != null) {
//            condition = condition.and(event.paid.eq(request.getPaid()));
//        }
//
//        if (request.getRangeStart() != null) {
//            condition = condition.and(event.eventDate.goe(request.getRangeStart()));
//        }
//
//        if (request.getRangeEnd() != null) {
//            condition = condition.and(event.eventDate.loe(request.getRangeEnd()));
//        }
//
//        return condition;
//    }

    // Компаратор для сортировки по дате события
    public static class EventSortByEventDate implements Comparator<EventShortDto> {

        @Override
        public int compare(EventShortDto o1, EventShortDto o2) {
            return o1.getEventDate().compareTo(o2.getEventDate());
        }

    }

    public List<Event> getAllEventsByListId(List<Long> eventsId) {
        return eventRepository.findAllById(eventsId);
    }

    public void updateEventData(String dataTime, Event eventSaved) {
        if (LocalDateTime.parse(dataTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .isBefore(LocalDateTime.now().plusHours(1))) {
            throw new InvalidRequestException("The start date of the event to be modified must be no earlier " +
                    "than one hour from the date of publication.");
        }
        eventSaved.setEventDate(LocalDateTime.parse(dataTime,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    public Optional<Event> findByCategory(Category category) {
        return eventRepository.findByCategory(category);
    }
}