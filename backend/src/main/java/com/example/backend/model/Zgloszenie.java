package com.example.backend.model;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;

import java.time.Instant;
import java.util.UUID;

/**
 * Spełnia encję ZGLOSZENIA ze schematu (docs/database-schema.md).
 */
@Entity
@Table(name = "zgloszenia")
public class Zgloszenie {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "uzytkownik_id")
    private Uzytkownik uzytkownik;

    @ManyToOne(optional = false)
    @JoinColumn(name = "typ_zagrozenia_id")
    private TypZagrozenia typZagrozenia;

    @Column(columnDefinition = "geography(Point,4326)", nullable = false)
    private Point lokalizacja;

    @Column(name = "zdjecie_url")
    private String zdjecieUrl;

    @Column(columnDefinition = "text")
    private String komentarz;

    @Enumerated(EnumType.STRING)
    private ZrodloZgloszenia zrodlo = ZrodloZgloszenia.ONLINE;

    @Column(name = "data_utworzenia", nullable = false, updatable = false)
    private Instant dataUtworzenia = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Uzytkownik getUzytkownik() { return uzytkownik; }
    public void setUzytkownik(Uzytkownik uzytkownik) { this.uzytkownik = uzytkownik; }

    public TypZagrozenia getTypZagrozenia() { return typZagrozenia; }
    public void setTypZagrozenia(TypZagrozenia typZagrozenia) { this.typZagrozenia = typZagrozenia; }

    public Point getLokalizacja() { return lokalizacja; }
    public void setLokalizacja(Point lokalizacja) { this.lokalizacja = lokalizacja; }

    public String getZdjecieUrl() { return zdjecieUrl; }
    public void setZdjecieUrl(String zdjecieUrl) { this.zdjecieUrl = zdjecieUrl; }

    public String getKomentarz() { return komentarz; }
    public void setKomentarz(String komentarz) { this.komentarz = komentarz; }

    public ZrodloZgloszenia getZrodlo() { return zrodlo; }
    public void setZrodlo(ZrodloZgloszenia zrodlo) { this.zrodlo = zrodlo; }

    public Instant getDataUtworzenia() { return dataUtworzenia; }
    public void setDataUtworzenia(Instant dataUtworzenia) { this.dataUtworzenia = dataUtworzenia; }
}
