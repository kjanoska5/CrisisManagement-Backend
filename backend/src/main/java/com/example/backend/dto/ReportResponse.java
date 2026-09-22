package com.example.backend.dto;

import java.time.Instant;
import java.util.UUID;

// odpowiedz zwracana po utworzeniu zgloszenia
public class ReportResponse {

    private final UUID id;
    private final Instant createdAt;

    public ReportResponse(UUID id, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public Instant getCreatedAt() { return createdAt; }
}