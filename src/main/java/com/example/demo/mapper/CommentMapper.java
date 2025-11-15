package com.example.demo.mapper;

import com.example.demo.dto.*;
import com.example.demo.entity.Comment;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {
    
    @Named("basicCommentDTO")
    @Mapping(target = "user", qualifiedByName = "basicUserDTO")
    @Mapping(target = "task", ignore = true)
    CommentDTO toDTO(Comment comment);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "task", ignore = true)
    Comment toEntity(CommentCreateDTO commentCreateDTO);
    
    List<CommentDTO> toDTOList(List<Comment> comments);
}
