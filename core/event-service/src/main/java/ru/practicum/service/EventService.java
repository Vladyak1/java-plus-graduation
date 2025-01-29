package ru.practicum.service;

import ru.practicum.model.Category;
import ru.practicum.dto.*;
import ru.practicum.model.Event;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface EventService {

    List<EventShortDto> getAllEventOfUser(Long userId, Integer from, Integer size);

    EventLongDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventOfUserById(Long userId, Long eventId);

    EventLongDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getRequestEventByUser(Long userId, Long eventId);

    EventRequestStatusUpdateResult changeRequestEventStatus(Long userId, Long eventId,
                                                            EventRequestStatusUpdateRequest request);

    List<EventLongDto> getAllEventsByAdmin(EventAdminParams eventAdminParams);

    EventLongDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getPublicEvents(EventPublicParams param);

    Event getEventById(Long eventId);

    List<Event> getAllEventsByListId(List<Long> eventsId);

    Optional<Event> findByCategory(Category category);

    EventFullDto getEventDtoByIdWithHit(Long id, HttpServletRequest httpServletRequest);
}
