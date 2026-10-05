package com.example.backend.controller;

import com.example.backend.dto.NewReportRequest;
import com.example.backend.dto.ReportResponse;
import com.example.backend.model.ReportStatus;
import com.example.backend.model.ThreatType;
import com.example.backend.model.User;
import com.example.backend.model.Report;
import com.example.backend.repository.ReportRepository;
import com.example.backend.repository.ThreatTypeRepository;
import com.example.backend.repository.UserRepository;
import jakarta.validation.Valid;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;
import java.util.List;
import com.example.backend.dto.MapReportDto;
import java.util.stream.Collectors;

// obsluga zgloszen api
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    // uklad wspolrzednych wgs84
    private static final int WGS84_SRID = 4326;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), WGS84_SRID);

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ThreatTypeRepository threatTypeRepository;

    public ReportController(
            ReportRepository reportRepository,
            UserRepository userRepository,
            ThreatTypeRepository threatTypeRepository
    ) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.threatTypeRepository = threatTypeRepository;
    }

    // tworzenie nowego zgloszenia
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportResponse create(@Valid @RequestBody NewReportRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "user not found"));

        ThreatType type = threatTypeRepository.findById(request.getThreatTypeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "threat type not found"));

        Point location = geometryFactory.createPoint(
                new Coordinate(request.getLng(), request.getLat())
        );

        Report report = new Report();
        report.setUser(user);
        report.setThreatType(type);
        report.setLocation(location);
        report.setPhotoUrl(request.getPhotoUrl());
        report.setComment(request.getComment());

        Report saved = reportRepository.save(report);

        return new ReportResponse(saved.getId(), saved.getCreatedAt());
    }

    @GetMapping
    public ResponseEntity<List<MapReportDto>> getAllReports() {
        List<MapReportDto> reports = reportRepository.findAll().stream()
                .map(report -> {
                    double lat = report.getLocation().getY();
                    double lng = report.getLocation().getX();

                    return new MapReportDto(
                            report.getId(),
                            lat,
                            lng,
                            report.getStatus(),
                            report.getThreatType().getId(),
                            report.getComment()
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(reports);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MapReportDto> updateReportStatus(
            @PathVariable UUID id,
            @RequestParam ReportStatus status) {

        return reportRepository.findById(id)
                .map(report -> {
                    report.setStatus(status);
                    Report updatedReport = reportRepository.save(report);

                    MapReportDto responseDto = new MapReportDto(
                            updatedReport.getId(),
                            updatedReport.getLocation().getY(),
                            updatedReport.getLocation().getX(),
                            updatedReport.getStatus(),
                            updatedReport.getThreatType().getId(),
                            updatedReport.getComment()
                    );

                    return ResponseEntity.ok(responseDto);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}