package com.ollirum.foodiesapi.services;

import com.ollirum.foodiesapi.DTO.request.UserRequestDTO;
import com.ollirum.foodiesapi.DTO.response.UserResponseDTO;

public interface UserService {
    UserResponseDTO registerUser(UserRequestDTO requestDTO);
    String findByUserId();
}