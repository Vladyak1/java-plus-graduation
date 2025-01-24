package event.repository;

import event.dto.EventAdminParams;
import event.dto.EventPublicParams;
import event.model.Event;

import java.util.List;

public interface CustomizedEventStorage {
    List<Event> searchEventsForAdmin(EventAdminParams param);

    List<Event> searchPublicEvents(EventPublicParams param);
}