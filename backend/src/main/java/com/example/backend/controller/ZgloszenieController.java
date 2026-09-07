package com.example.backend.controller;

import com.example.backend.dto.NoweZgloszenieRequest;
import com.example.backend.dto.ZgloszenieResponse;
import com.example.backend.model.TypZagrozenia;
import com.example.backend.model.Uzytkownik;
import com.example.backend.model.Zgloszenie;
import com.example.backend.repository.TypZagrozeniaRepository;
import com.example.backend.repository.UzytkownikRepository;
import com.example.backend.repository.ZgloszenieRepository;
import jakarta.validation.Valid;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Realizuje kryterium akceptacji issue #7 (1.1):
 * "Po wysłaniu zgłoszenie trafia do backendu".
 */
@RestController
@RequestMapping("/api/zgloszenia")
public class ZgloszenieController {

    // Układ współrzędnych WGS84 — standard dla GPS, zgodny z docs/database-schema.md
    private static final int WGS84_SRID = 4326;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), WGS84_SRID);

    private final ZgloszenieRepository zgloszenieRepository;
    private final UzytkownikRepository uzytkownikRepository;
    private final TypZagrozeniaRepository typZagrozeniaRepository;

    public ZgloszenieController(
            ZgloszenieRepository zgloszenieRepository,
            UzytkownikRepository uzytkownikRepository,
            TypZagrozeniaRepository typZagrozeniaRepository
    ) {
        this.zgloszenieRepository = zgloszenieRepository;
        this.uzytkownikRepository = uzytkownikRepository;
        this.typZagrozeniaRepository = typZagrozeniaRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ZgloszenieResponse utworz(@Valid @RequestBody NoweZgloszenieRequest request) {

        Uzytkownik uzytkownik = uzytkownikRepository.findById(request.getUzytkownikId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nie znaleziono użytkownika"));

        TypZagrozenia typ = typZagrozeniaRepository.findById(request.getTypZagrozeniaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nie znaleziono typu zagrożenia"));

        // JTS Coordinate przyjmuje (x, y) czyli (longitude, latitude) —
        // odwrotna kolejność niż intuicyjne "lat, lng".
        Point lokalizacja = geometryFactory.createPoint(
                new Coordinate(request.getLng(), request.getLat())
        );

        Zgloszenie zgloszenie = new Zgloszenie();
        zgloszenie.setUzytkownik(uzytkownik);
        zgloszenie.setTypZagrozenia(typ);
        zgloszenie.setLokalizacja(lokalizacja);
        zgloszenie.setZdjecieUrl(request.getZdjecieUrl());
        zgloszenie.setKomentarz(request.getKomentarz());

        Zgloszenie zapisane = zgloszenieRepository.save(zgloszenie);

        return new ZgloszenieResponse(zapisane.getId(), zapisane.getDataUtworzenia());
    }
}
