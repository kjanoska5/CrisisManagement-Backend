package com.example.backend.dto;
import java.util.UUID;
import com.example.backend.model.Role;

public record AuthResponse(
        UUID userId,
        Role role
) {}
