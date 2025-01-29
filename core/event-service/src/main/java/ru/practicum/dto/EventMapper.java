package ru.practicum.dto;

import client.UserServiceClient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.Event;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public abstract class EventMapper {

    @Autowired
    protected UserServiceClient userServiceClient;

    @Mapping(source = "category", target = "category.id")
    @Mapping(source = "location.lat", target = "lat")
    @Mapping(source = "location.lon", target = "lon")
    public abstract Event toEvent(NewEventDto newEventDto);

    @Mapping(source = "category", target = "category")
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(target = "views", expression = "java(mapViews(event.getViews()))")
    public abstract EventShortDto toEventShortDto(Event event);

    @Mapping(source = "event.category", target = "category")
    @Mapping(source = "event.initiator", target = "initiator")
    @Mapping(target = "views", source = "views")
    public abstract EventShortDto toShortDto(Event event, Long views);

    @Mapping(source = "event.category", target = "category")
    @Mapping(source = "event.initiator", target = "initiator")
    @Mapping(source = "event.lat", target = "location.lat")
    @Mapping(source = "event.lon", target = "location.lon")
    @Mapping(target = "views", source = "views")
    public abstract EventLongDto toLongDto(Event event, Long views);

    @Mapping(source = "category", target = "category")
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(source = "lat", target = "location.lat")
    @Mapping(source = "lon", target = "location.lon")
    @Mapping(target = "views", constant = "0L")
    public abstract EventLongDto toLongDto(Event event);

    @Mapping(source = "category", target = "category")
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(source = "lat", target = "location.lat")
    @Mapping(source = "lon", target = "location.lon")
    @Mapping(target = "views", constant = "0L")
    public abstract EventFullDto toEventFullDto(Event event);

    protected Long mapViews(List<Long> views) {
        if (views == null || views.isEmpty()) {
            return 0L;
        }
        return views.get(0);
    }

    protected UserShortDto mapInitiator(Long initiatorId) {
        if (initiatorId == null) {
            return null;
        }
        var adminUserDto = userServiceClient.findUserById(initiatorId);
        return new UserShortDto(adminUserDto.getId(), adminUserDto.getName());
    }

    protected UserDto mapToUserDto(Long initiatorId) {
        if (initiatorId == null) {
            return null;
        }
        var adminUserDto = userServiceClient.findUserById(initiatorId);
        return UserDto.builder()
                .id(adminUserDto.getId())
                .name(adminUserDto.getName())
                .build();
    }
}
