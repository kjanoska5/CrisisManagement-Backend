package com.example.backend.controller;

import com.example.backend.model.TypZagrozenia;
import com.example.backend.repository.TypZagrozeniaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Realizuje kryterium akceptacji issue #7 (1.1)
 */
@RestController
@RequestMapping("/api/typy-zagrozen")
public class TypZagrozeniaController {

    private final TypZagrozeniaRepository repository;

    public TypZagrozeniaController(TypZagrozeniaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<TypZagrozenia> lista() {
        return repository.findAll();
    }
}
