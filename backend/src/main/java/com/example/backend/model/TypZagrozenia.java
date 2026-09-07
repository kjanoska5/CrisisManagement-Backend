package com.example.backend.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "typy_zagrozen")
public class TypZagrozenia {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String nazwa;

    private String ikona;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNazwa() { return nazwa; }
    public void setNazwa(String nazwa) { this.nazwa = nazwa; }

    public String getIkona() { return ikona; }
    public void setIkona(String ikona) { this.ikona = ikona; }
}
