package com.example.backend.model;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

// encja reprezentujaca zgloszenie w bazie
@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "threat_type_id")
    private ThreatType threatType;

    // zapisywane jako punkt z postgis
    @Column(columnDefinition = "geography(Point,4326)", nullable = false)
    private Point location;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(columnDefinition = "text")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'ONLINE'")
    private ReportSource source = ReportSource.ONLINE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.VERIFYING;

    public UUID getId() { return id; }

    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public ThreatType getThreatType() { return threatType; }
    public void setThreatType(ThreatType threatType) { this.threatType = threatType; }

    public Point getLocation() { return location; }
    public void setLocation(Point location) { this.location = location; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public ReportSource getSource() { return source; }
    public void setSource(ReportSource source) { this.source = source; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }
}