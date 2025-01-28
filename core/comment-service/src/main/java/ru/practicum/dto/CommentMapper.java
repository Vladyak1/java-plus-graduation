package ru.practicum.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.Comment;

@Mapper(componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "create", ignore = true)
    Comment toComment(CommentDto commentDto);

    @Mapping(target = "create", dateFormat = "yyyy-MM-dd HH:mm:ss")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "create", dateFormat = "yyyy-MM-dd HH:mm:ss")
    CommentDtoPublic toCommentDtoPublic(Comment comment);
}
