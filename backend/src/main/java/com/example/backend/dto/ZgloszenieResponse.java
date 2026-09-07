package com.example.backend.dto;

import java.time.Instant;
import java.util.UUID;

public class ZgloszenieResponse {

    private final UUID id;
    private final Instant dataUtworzenia;

    public ZgloszenieResponse(UUID id, Instant dataUtworzenia) {
        this.id = id;
        this.dataUtworzenia = dataUtworzenia;
    }

    public UUID getId() { return id; }
    public Instant getDataUtworzenia() { return dataUtworzenia; }
}
