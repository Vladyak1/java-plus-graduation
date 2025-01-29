package ru.practicum.repository;

import ru.practicum.dto.EventAdminParams;
import ru.practicum.dto.EventPublicParams;
import ru.practicum.model.Event;

import java.util.List;

public interface CustomizedEventStorage {
    List<Event> searchEventsForAdmin(EventAdminParams param);

    List<Event> searchPublicEvents(EventPublicParams param);
}