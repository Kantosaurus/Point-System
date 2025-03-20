package com.pointsystem.model;

import java.time.LocalDateTime;

public class Activity {
    private ActivityType type;
    private LocalDateTime timestamp;
    private int pointsEarned;
    private String details;

    public Activity(ActivityType type, LocalDateTime timestamp, int pointsEarned, String details) {
        this.type = type;
        this.timestamp = timestamp;
        this.pointsEarned = pointsEarned;
        this.details = details;
    }

    public Activity(ActivityType type, int pointsEarned, String details) {
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.pointsEarned = pointsEarned;
        this.details = details;
    }

    public ActivityType getType() {
        return type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public String getDetails() {
        return details;
    }
} 