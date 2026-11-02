package com.example.backend.service;

import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalAuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LocalAuthService localAuthService;

    @Test
    void register_shouldThrowConflict_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest(
                "test@example.com",
                "password123",
                "Jan",
                "Kowalski",
                "CITIZEN"
        );

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            localAuthService.register(request);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Email is already taken", exception.getReason());

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void register_shouldCreateUser_whenDataIsValid() {
        RegisterRequest request = new RegisterRequest(
                "new@example.com",
                "password123",
                "Jan",
                "Kowalski",
                "CITIZEN"
        );

        UUID userId = UUID.randomUUID();
        User savedUser = new User();
        savedUser.setId(userId);
        savedUser.setEmail("new@example.com");
        savedUser.setRole(Role.CITIZEN);

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        AuthResponse response = localAuthService.register(request);

        assertNotNull(response);
        assertEquals(userId, response.userId());
        assertEquals(Role.CITIZEN, response.role());
        verify(userRepository, times(1)).save(any(User.class));
    }
}