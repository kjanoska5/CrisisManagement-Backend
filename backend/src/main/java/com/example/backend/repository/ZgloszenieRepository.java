package com.example.backend.repository;

import com.example.backend.model.Zgloszenie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ZgloszenieRepository extends JpaRepository<Zgloszenie, UUID> {
}
