package com.example.backend.repository;

import com.example.backend.model.TypZagrozenia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TypZagrozeniaRepository extends JpaRepository<TypZagrozenia, UUID> {
}
