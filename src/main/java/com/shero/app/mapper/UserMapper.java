package com.shero.app.mapper;

import com.shero.app.dto.response.UserResponse;
import com.shero.app.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);
}
