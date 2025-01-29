package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.enums.EventState;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, CustomizedEventStorage {

    List<Event> findByInitiatorOrderByIdDesc(Long userId, PageRequest pageRequest);

    Optional<Event> findByIdAndInitiator(Long eventId, Long userId);

    Optional<Event> findByIdAndState(Long id, EventState state);

    Optional<Event> findByCategory(Category category);

}
