package com.ollirum.foodiesapi.controllers;

import com.ollirum.foodiesapi.DTO.request.AuthRequestDTO;
import com.ollirum.foodiesapi.DTO.response.AuthResponseDTO;
import com.ollirum.foodiesapi.services.impl.UserDetailsServiceImpl;
import com.ollirum.foodiesapi.utils.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO inputDTO) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(inputDTO.email(), inputDTO.password()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(inputDTO.email());
        final String jwtToken = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponseDTO(inputDTO.email(), jwtToken));
    }
}