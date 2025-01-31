package ru.practicum.core.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "like-service")
public interface LikeServiceClient {

    @GetMapping("/internal/likes/events/{eventId}")
    Long getCountByEventId(@PathVariable Long eventId);

    @PutMapping("/users/{userId}/events/{eventId}/likes")
    Long addEventLike(@PathVariable long userId, @PathVariable long eventId);

    @DeleteMapping("/users/{userId}/events//{eventId}/likes")
    Long deleteEventLike(@PathVariable long userId, @PathVariable long eventId);

    @GetMapping("/internal/likes/events")
    Map<Long, Long> getAllEventsLikesByIds(@RequestParam List<Long> eventIdList);

    @GetMapping("/internal/likes/locations/top")
    Map<Long, Long> getTopLikedLocationsIds(
            @RequestParam(required = false, defaultValue = "10") Integer count);

    @GetMapping("/internal/likes/events/top")
    Map<Long, Long> getTopLikedEventsIds(@RequestParam(required = false, defaultValue = "10") Integer count);

    @GetMapping("/internal/likes/locations/{locationId}")
    Long getCountByLocationId(@PathVariable Long locationId);

}
