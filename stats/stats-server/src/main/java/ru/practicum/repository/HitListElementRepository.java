package ru.practicum.repository;

import ru.practicum.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface HitListElementRepository {
    List<ViewStats> getHitListElementDtos(LocalDateTime start,
                                          LocalDateTime end,
                                          String[] uris,
                                          boolean unique);
}
