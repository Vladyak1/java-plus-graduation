package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.AdminUserDto;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.UserDtoReceived;
import ru.practicum.dto.UserShortDto;
import ru.practicum.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    User toUser(UserDtoReceived userDto);

    UserShortDto toUserShortDto(User user);

    AdminUserDto toAdminUserDto(User user);

    List<AdminUserDto> toListAdminUserDto(List<User> users);
}