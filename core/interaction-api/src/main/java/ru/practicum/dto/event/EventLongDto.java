package ru.practicum.dto.event;

import ru.practicum.dto.category.CategoryDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.dto.event.enums.EventState;
import lombok.Builder;
import lombok.Value;

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
    LocationDto location;
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