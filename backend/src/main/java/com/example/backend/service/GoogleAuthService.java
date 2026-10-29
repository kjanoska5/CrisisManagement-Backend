package com.example.backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.GoogleLoginRequest;
import com.example.backend.model.AuthProvider;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final GoogleIdTokenVerifier verifier;
    private final UserRepository userRepository;

    @Transactional
    public AuthResponse authenticate(GoogleLoginRequest request) {
        try {
            GoogleIdToken idToken = verifier.verify(request.idToken());
            if (idToken == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Nieprawidłowy lub wygasły token Google.");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail nie został zweryfikowany przez Google.");
            }

            String googleSub = payload.getSubject();
            String email = payload.getEmail();

            Optional<User> existingUserBySub = userRepository.findByGoogleSub(googleSub);
            if (existingUserBySub.isPresent()) {
                User user = existingUserBySub.get();
                return new AuthResponse(user.getId(), user.getRole());
            }

            Optional<User> existingUserByEmail = userRepository.findByEmail(email);
            if (existingUserByEmail.isPresent()) {
                User user = existingUserByEmail.get();
                if (user.getAuthProvider() == AuthProvider.LOCAL) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Konto z tym adresem e-mail zostało już zarejestrowane tradycyjnie. Zaloguj się używając hasła.");
                }
            }

            User newUser = new User();
            newUser.setEmail(email);
            newUser.setGoogleSub(googleSub);
            newUser.setAuthProvider(AuthProvider.GOOGLE);

            Role role = (request.role() != null && request.role() == Role.CITIZEN) ? request.role() : Role.CITIZEN;
            newUser.setRole(role);

            userRepository.save(newUser);
            return new AuthResponse(newUser.getId(), newUser.getRole());

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Błąd weryfikacji tokenu: " + e.getMessage());
        }
    }
}