package ru.practicum.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.ParticipationRequest;

@Mapper(componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING)
public interface RequestsMapper {

    RequestsMapper REQUESTS_MAPPER = Mappers.getMapper(RequestsMapper.class);

    @Mapping(target = "event", source = "eventId")
    @Mapping(target = "requester", source = "requesterId")
    ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest);

    @Mapping(target = "eventId", source = "event")
    @Mapping(target = "requesterId", source = "requester")
    ParticipationRequest toParticipationRequest(ParticipationRequestDto participationRequestDto);
}