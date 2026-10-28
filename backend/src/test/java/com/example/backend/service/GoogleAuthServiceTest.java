package com.example.backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.example.backend.dto.GoogleLoginRequest;
import com.example.backend.model.AuthProvider;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleAuthServiceTest {

    @Mock
    private GoogleIdTokenVerifier verifier;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoogleAuthService googleAuthService;

    @Test
    void shouldThrowConflictWhenLocalUserExistsWithSameEmail() throws Exception {
        String idTokenString = "fake_token";
        GoogleLoginRequest request = new GoogleLoginRequest(idTokenString, null);

        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setEmailVerified(true);
        payload.setSubject("sub123");
        payload.setEmail("test@example.com");

        GoogleIdToken mockToken = mock(GoogleIdToken.class);
        when(verifier.verify(idTokenString)).thenReturn(mockToken);
        when(mockToken.getPayload()).thenReturn(payload);

        User localUser = new User();
        localUser.setAuthProvider(AuthProvider.LOCAL);

        when(userRepository.findByGoogleSub("sub123")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(localUser));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                googleAuthService.authenticate(request)
        );
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }
}