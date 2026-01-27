package com.ollirum.foodiesapi.services.impl;

import com.ollirum.foodiesapi.DTO.request.UserRequestDTO;
import com.ollirum.foodiesapi.DTO.response.UserResponseDTO;
import com.ollirum.foodiesapi.entities.User;
import com.ollirum.foodiesapi.mappers.UserMapper;
import com.ollirum.foodiesapi.repositories.UserRepository;
import com.ollirum.foodiesapi.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserResponseDTO registerUser(UserRequestDTO inputDTO) {
        User newUser = UserMapper.toEntity(inputDTO);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser = userRepository.save(newUser);
        return UserMapper.toResponseDTO(newUser);
    }
}