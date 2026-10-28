package com.example.backend.dto;
import com.example.backend.model.Role;
import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
        @NotBlank String idToken,
        Role role
) {}