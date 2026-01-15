package com.ollirum.foodiesapi.mappers;

import com.ollirum.foodiesapi.DTO.request.UserRequestDTO;
import com.ollirum.foodiesapi.DTO.response.UserResponseDTO;
import com.ollirum.foodiesapi.entities.User;

public class UserMapper {
    private UserMapper() {}

    public static User toEntity(UserRequestDTO dto) {
        if (dto == null) return null;

        return User.builder()
                .email(dto.email())
                .password(dto.password())
                .name(dto.name())
                .build();
    }

    public static UserResponseDTO toResponseDTO(User user) {
        if (user == null) return null;

        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}