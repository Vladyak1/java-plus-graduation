package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.constants.DataTransferConvention;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
public class StatController {
    private final StatService statService;

    @PostMapping("/hit")
    public ResponseEntity<EndpointHit> registerHit(@RequestBody @Valid EndpointHit endpointHit) {
        log.info("Registering hit: {}", endpointHit);
        return new ResponseEntity<>(statService.registerHit(endpointHit), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStats>> getStats(
            @RequestParam("start") @DateTimeFormat(pattern = DataTransferConvention.DATE_TIME_PATTERN) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(pattern = DataTransferConvention.DATE_TIME_PATTERN) LocalDateTime end,
            @RequestParam(value = "uris", required = false) String[] uris,
            @RequestParam(value = "unique", defaultValue = "false")
            Boolean unique) {
        if (start.isAfter(end)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(statService.getHits(start, end, uris, unique), HttpStatus.OK);
    }
}
