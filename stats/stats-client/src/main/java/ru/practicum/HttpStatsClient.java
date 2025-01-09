package ru.practicum;

import ru.practicum.dto.ViewStats;

import java.util.List;
import java.util.Optional;

public interface HttpStatsClient {
    List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique);

    <R> Optional<R> getStats(StatsParameters<R> params);

    <T, R> Optional<R> sendHit(T hitDto, Class<R> responseType);
}