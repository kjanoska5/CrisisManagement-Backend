package com.example.backend.controller;

import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.GoogleLoginRequest;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.service.GoogleAuthService;
import com.example.backend.service.LocalAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for Google and Local authentication. NOTE: API does not issue JWT at this stage.")
public class AuthController {

    private final GoogleAuthService googleAuthService;
    private final LocalAuthService localAuthService;

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

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a LOCAL account. Roles are temporarily restricted to CITIZEN or SERVICES.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Email is already taken")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = localAuthService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Local user login", description = "Authenticate using email and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "400", description = "Invalid request format"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password (or attempt to login to a Google account)")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = localAuthService.login(request);
        return ResponseEntity.ok(response);
    }
}