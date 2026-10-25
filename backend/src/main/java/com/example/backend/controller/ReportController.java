package com.example.backend.controller;

import com.example.backend.dto.NewReportRequest;
import com.example.backend.dto.UpdateReportRequest;
import com.example.backend.dto.ReportResponse;
import com.example.backend.model.ReportSource;
import com.example.backend.model.ReportStatus;
import com.example.backend.model.ThreatType;
import com.example.backend.model.User;
import com.example.backend.model.Report;
import com.example.backend.repository.ReportRepository;
import com.example.backend.repository.ThreatTypeRepository;
import com.example.backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports", description = "Incident and threat reports management")
public class ReportController {

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new report", description = "Registers a new report based on mobile app data (GPS coordinates, type, comment).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Report successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., missing UUID or unknown threat type)")
    })
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

        if (request.getSource() != null) {
            try {
                ReportSource source = ReportSource.valueOf(request.getSource().toUpperCase());
                report.setSource(source);
                if (source == ReportSource.SERVICE) {
                    report.setStatus(ReportStatus.ACTIVE);
                }
            } catch (IllegalArgumentException e) {
            }
        }

        Report saved = reportRepository.save(report);

        return new ReportResponse(saved.getId(), saved.getCreatedAt());
    }

    @GetMapping
    @Operation(summary = "Get all reports", description = "Returns a list of all reports with their status and coordinates for map rendering.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of reports")
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
    @Operation(summary = "Update report status", description = "Endpoint for manually updating the report status (e.g., from a dispatcher's computer).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status successfully updated"),
            @ApiResponse(responseCode = "404", description = "Report not found for the given UUID")
    })
    public ResponseEntity<MapReportDto> updateReportStatus(
            @Parameter(description = "UUID of the report to update") @PathVariable UUID id,
            @Parameter(description = "New status of the report (e.g., VERIFIED, IN_PROGRESS, RESOLVED, REJECTED)") @RequestParam ReportStatus status) {

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

    @PutMapping("/{id}")
    @Operation(summary = "Update report details", description = "Endpoint for editing an existing report (e.g., location, threat type, or comment).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or threat type not found"),
            @ApiResponse(responseCode = "404", description = "Report not found for the given UUID")
    })
    public ResponseEntity<MapReportDto> updateReport(
            @Parameter(description = "UUID of the report to update") @PathVariable UUID id,
            @Valid @RequestBody UpdateReportRequest request) {

        ThreatType type = threatTypeRepository.findById(request.getThreatTypeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "threat type not found"));

        return reportRepository.findById(id)
                .map(report -> {
                    Point location = geometryFactory.createPoint(
                            new Coordinate(request.getLng(), request.getLat())
                    );
                    report.setThreatType(type);
                    report.setLocation(location);
                    report.setComment(request.getComment());

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

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete report", description = "Endpoint for removing a report (e.g., if created by mistake).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Report successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Report not found for the given UUID")
    })
    public ResponseEntity<Void> deleteReport(
            @Parameter(description = "UUID of the report to delete") @PathVariable UUID id) {

        if (!reportRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        reportRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}