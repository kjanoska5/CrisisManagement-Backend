package com.example.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

// dane z apki android przy zgloszeniu
public class NewReportRequest {

    @NotNull
    private UUID userId;

    @NotNull
    private UUID threatTypeId;

    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double lat;

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double lng;

    private String photoUrl;

    private String comment;

    private String source;

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getThreatTypeId() { return threatTypeId; }
    public void setThreatTypeId(UUID threatTypeId) { this.threatTypeId = threatTypeId; }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}