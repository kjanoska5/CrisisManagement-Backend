package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Dane przychodzące z aplikacji Android przy zgłaszaniu zdarzenia (issue 1.1).
 *
 * uzytkownikId jest na razie wysyłane wprost z apki (bo nie ma jeszcze
 * prawdziwego logowania z tokenem) — do zmiany, gdy powstanie autoryzacja.
 */
public class NoweZgloszenieRequest {

    @NotNull
    private UUID uzytkownikId;

    @NotNull
    private UUID typZagrozeniaId;

    @NotNull
    private Double lat;

    @NotNull
    private Double lng;

    private String zdjecieUrl;

    private String komentarz;

    public UUID getUzytkownikId() { return uzytkownikId; }
    public void setUzytkownikId(UUID uzytkownikId) { this.uzytkownikId = uzytkownikId; }

    public UUID getTypZagrozeniaId() { return typZagrozeniaId; }
    public void setTypZagrozeniaId(UUID typZagrozeniaId) { this.typZagrozeniaId = typZagrozeniaId; }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }

    public String getZdjecieUrl() { return zdjecieUrl; }
    public void setZdjecieUrl(String zdjecieUrl) { this.zdjecieUrl = zdjecieUrl; }

    public String getKomentarz() { return komentarz; }
    public void setKomentarz(String komentarz) { this.komentarz = komentarz; }
}
