package com.example.backend.controller;

import com.example.backend.model.ThreatType;
import com.example.backend.repository.ThreatTypeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/threat-types")
@Tag(name = "Threat Types", description = "Available threat types dictionary management")
public class ThreatTypeController {

    private final ThreatTypeRepository repository;

    public ThreatTypeController(ThreatTypeRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @Operation(summary = "Get all threat types", description = "Retrieves a list of all available threat types dictionary values.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved threat types list")
    public List<ThreatType> getAll() {
        return repository.findAll();
    }
}