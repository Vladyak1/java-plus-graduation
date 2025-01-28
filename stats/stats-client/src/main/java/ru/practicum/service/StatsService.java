package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHit;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsService {
    private final StatsClient statsClient;

    public void postHit(EndpointHit endpointHit) {
        log.info("Sending hit: {}", endpointHit);
        statsClient.postHit(endpointHit);
    }

    public ResponseEntity<Object> getViewStats(String start, String end, String[] uris, boolean unique) {
        log.info("Getting stats for uris: {}", uris);
        return statsClient.getViewStats(start, end, uris, unique);
    }
}