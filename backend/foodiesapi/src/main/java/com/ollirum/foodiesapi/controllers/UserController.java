package com.ollirum.foodiesapi.controllers;

import com.ollirum.foodiesapi.DTO.request.UserRequestDTO;
import com.ollirum.foodiesapi.DTO.response.UserResponseDTO;
import com.ollirum.foodiesapi.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@CrossOrigin("*")
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRequestDTO inputDTO) {
        UserResponseDTO responseDTO = userService.registerUser(inputDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
}