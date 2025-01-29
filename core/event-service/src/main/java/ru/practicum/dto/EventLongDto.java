package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import ru.practicum.model.Location;
import ru.practicum.model.enums.EventState;

import java.time.LocalDateTime;

@Value
@Builder
public class EventLongDto {
    Long id;
    Long initiator;
    String title;
    String annotation;
    String description;
    CategoryDto category;
    Integer participantLimit;
    Integer confirmedRequests;
    Boolean paid;
    Location location;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedOn;
    Boolean requestModeration;
    EventState state;
    Long views;
}