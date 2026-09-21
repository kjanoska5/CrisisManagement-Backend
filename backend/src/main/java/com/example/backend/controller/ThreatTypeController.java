package com.example.backend.controller;

import com.example.backend.model.ThreatType;
import com.example.backend.repository.ThreatTypeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// realizacja issue 7
@RestController
@RequestMapping("/api/threat-types")
public class ThreatTypeController {

    private final ThreatTypeRepository repository;

    public ThreatTypeController(ThreatTypeRepository repository) {
        this.repository = repository;
    }

    // pobieranie listy zagrozen
    @GetMapping
    public List<ThreatType> getAll() {
        return repository.findAll();
    }
}