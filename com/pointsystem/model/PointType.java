package com.pointsystem.model;

public enum PointType {
    PERMANENT(0, 0), // Never expires
    EXPIRING(30, 0), // Expires after 30 days
    NONE(0, 0);      // No points awarded
    
    private final int expirationDays;
    private final int maxPerItem; // For limiting points (e.g., max 5 comments per post)
    
    PointType(int expirationDays, int maxPerItem) {
        this.expirationDays = expirationDays;
        this.maxPerItem = maxPerItem;
    }
    
    public int getExpirationDays() {
        return expirationDays;
    }
    
    public int getMaxPerItem() {
        return maxPerItem;
    }
} 