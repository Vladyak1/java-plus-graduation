package ru.practicum.stats.service;

import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;

import java.util.List;

public interface StatsService {
    EndpointHit saveHit(EndpointHit endpointHit);

    List<ViewStats> getHits(String start, String end, List<String> uris, Boolean unique);
}
