package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.client.config.FeignConfig;
import ru.practicum.dto.EndpointHit;

@FeignClient(
        name = "stats-service",
        configuration = FeignConfig.class
)
public interface StatsClient {

    @PostMapping("/hit")
    ResponseEntity<Object> postHit(@RequestBody EndpointHit endpointHit);

    @GetMapping("/stats")
    ResponseEntity<Object> getViewStats(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) String[] uris,
            @RequestParam(defaultValue = "false") boolean unique
    );
}
