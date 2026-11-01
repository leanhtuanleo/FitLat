package com.fitlat.backend.mapper;

import com.fitlat.backend.dto.UserResponse;
import com.fitlat.backend.entity.User;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}