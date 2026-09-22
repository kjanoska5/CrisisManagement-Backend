package com.example.backend.repository;

import com.example.backend.model.ThreatType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ThreatTypeRepository extends JpaRepository<ThreatType, UUID> {
}
