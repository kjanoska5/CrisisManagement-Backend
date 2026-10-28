package com.example.backend.controller;

import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.GoogleLoginRequest;
import com.example.backend.service.GoogleAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and Google OAuth2 login management")
public class AuthController {

    private final GoogleAuthService googleAuthService;

    @PostMapping("/google")
    @Operation(summary = "Google Login", description = "Verifies Google idToken. Logs in an existing user or creates a new account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token"),
            @ApiResponse(responseCode = "409", description = "Conflict: Local account with this email already exists")
    })
    public ResponseEntity<AuthResponse> googleLogin(
            @Parameter(description = "Google login request containing idToken and optional role") @Valid @RequestBody GoogleLoginRequest request) {
        return ResponseEntity.ok(googleAuthService.authenticate(request));
    }
}