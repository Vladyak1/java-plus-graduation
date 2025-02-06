package ru.yandex.practicum.stats.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.stats.analyzer.entity.EventSimilarity;

import java.util.List;
import java.util.Set;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {

    List<EventSimilarity> findAllByEventAIdOrEventBId(long eventA, long eventB);

    List<EventSimilarity> findAllByEventAIdInOrEventBIdIn(Set<Long> eventIds, Set<Long> eventIds1);
}
