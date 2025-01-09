package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.dto.EndpointHit;
import ru.practicum.model.ServiceHit;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServiceHitMapper {
    @Mapping(target = "timestamp", source = "created")
    EndpointHit toDto(ServiceHit serviceHit);

    @Mapping(target = "created", source = "timestamp")
    @Mapping(target = "id", ignore = true)
    ServiceHit toEntity(EndpointHit endpointHit);
}
