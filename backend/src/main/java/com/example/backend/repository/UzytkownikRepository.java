package com.example.backend.repository;

import com.example.backend.model.Uzytkownik;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UzytkownikRepository extends JpaRepository<Uzytkownik, UUID> {
}
