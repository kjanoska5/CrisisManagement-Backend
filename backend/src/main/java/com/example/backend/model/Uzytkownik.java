package com.example.backend.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * TYMCZASOWA, minimalna wersja encji użytkownika.
 */
@Entity
@Table(name = "uzytkownicy")
public class Uzytkownik {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "haslo_hash")
    private String hasloHash;

    @Enumerated(EnumType.STRING)
    private Rola rola;

    private String imie;
    private String nazwisko;

    @Column(name = "data_utworzenia", nullable = false, updatable = false)
    private Instant dataUtworzenia = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHasloHash() { return hasloHash; }
    public void setHasloHash(String hasloHash) { this.hasloHash = hasloHash; }

    public Rola getRola() { return rola; }
    public void setRola(Rola rola) { this.rola = rola; }

    public String getImie() { return imie; }
    public void setImie(String imie) { this.imie = imie; }

    public String getNazwisko() { return nazwisko; }
    public void setNazwisko(String nazwisko) { this.nazwisko = nazwisko; }

    public Instant getDataUtworzenia() { return dataUtworzenia; }
    public void setDataUtworzenia(Instant dataUtworzenia) { this.dataUtworzenia = dataUtworzenia; }
}
