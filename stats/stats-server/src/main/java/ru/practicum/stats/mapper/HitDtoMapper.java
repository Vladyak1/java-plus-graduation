package ru.practicum.stats.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.EndpointHit;
import ru.practicum.stats.entity.Hit;

import java.time.LocalDateTime;

import static ru.practicum.stats.utils.Constants.formatter;

@UtilityClass
public class HitDtoMapper {

    public static EndpointHit toHitDto(Hit hit) {
        String dateTime = hit.getTimestamp().format(formatter);

        return new EndpointHit(
                hit.getId(),
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                dateTime
        );
    }

    public static Hit dtoToHit(EndpointHit endpointHit) {

        LocalDateTime localDateTime = LocalDateTime.parse(endpointHit.getTimestamp(), formatter);
        Hit hit = new Hit();
        hit.setId(endpointHit.getId());
        hit.setApp(endpointHit.getApp());
        hit.setUri(endpointHit.getUri());
        hit.setIp(endpointHit.getIp());
        hit.setTimestamp(localDateTime);
        return hit;
    }
}

