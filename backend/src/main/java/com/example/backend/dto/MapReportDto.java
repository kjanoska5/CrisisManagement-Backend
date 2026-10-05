package com.example.backend.dto;

import com.example.backend.model.ReportStatus;
import java.util.UUID;

public record MapReportDto(
        UUID id,
        double lat,
        double lng,
        ReportStatus status,
        UUID threatTypeId,
        String comment
) {}