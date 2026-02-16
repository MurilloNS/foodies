package com.ollirum.foodiesapi.services.impl;

import com.ollirum.foodiesapi.DTO.request.UserRequestDTO;
import com.ollirum.foodiesapi.DTO.response.UserResponseDTO;
import com.ollirum.foodiesapi.entities.User;
import com.ollirum.foodiesapi.repositories.UserRepository;
import com.ollirum.foodiesapi.services.AuthenticationFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @Mock
    private Authentication authentication;

    @Test
    void shouldRegisterUserSuccessfully() {
        // Given
        UserRequestDTO requestDTO = new UserRequestDTO("Test", "test@gmail.com", "123456");

        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Action
        UserResponseDTO responseDTO = userService.registerUser(requestDTO);

        // Verification
        assertNotNull(responseDTO);
        verify(passwordEncoder).encode("123456");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldReturnUserIdWhenUserIsAuthenticated() {
        // Given
        String email = "test@gmail.com";
        String userId = "123";

        User user = new User();
        user.setId(userId);
        user.setEmail(email);

        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Action
        String result = userService.findByUserId();

        // Verification
        assertEquals(userId, result);
        verify(authenticationFacade).getAuthentication();
        verify(authentication).getName();
        verify(userRepository).findByEmail(email);
        assertNull(userId);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        String email = "notfound@gmail.com";

        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Verify
        assertThrows(UsernameNotFoundException.class, () -> userService.findByUserId());
    }
}