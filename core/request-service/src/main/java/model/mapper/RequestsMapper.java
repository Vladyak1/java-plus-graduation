package model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import dto.request.ParticipationRequestDto;
import model.ParticipationRequest;

@Mapper(componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING)
public interface RequestsMapper {

    RequestsMapper REQUESTS_MAPPER = Mappers.getMapper(RequestsMapper.class);

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest);

    @Mapping(target = "event.id", source = "event")
    @Mapping(target = "requester.id", source = "requester")
    ParticipationRequest toParticipationRequest(ParticipationRequestDto participationRequestDto);
}