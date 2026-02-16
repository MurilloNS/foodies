package com.ollirum.foodiesapi.services.impl;

import com.ollirum.foodiesapi.entities.User;
import com.ollirum.foodiesapi.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {
    @InjectMocks
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Test
    void shouldLoadUserByUsernameSuccessfully() {
        // Given
        String email = "test@gmail.com";
        String password = "123456";

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Action
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(email);

        // Verification
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        String email = "notfounf@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Verification
        assertThrows(UsernameNotFoundException.class, () -> userDetailsServiceImpl.loadUserByUsername(email));
    }
}