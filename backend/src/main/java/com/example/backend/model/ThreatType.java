package com.example.backend.model;

import jakarta.persistence.*;

import java.util.UUID;

// typ zagrozenia
@Entity
@Table(name = "threat_types")
public class ThreatType {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String icon;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}