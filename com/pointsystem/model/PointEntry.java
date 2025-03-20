package com.pointsystem.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class PointEntry {
    private int amount;
    private PointType type;
    private LocalDateTime earnedAt;
    
    public PointEntry(int amount, PointType type, LocalDateTime earnedAt) {
        this.amount = amount;
        this.type = type;
        this.earnedAt = earnedAt;
    }
    
    public int getAmount() {
        return amount;
    }
    
    public PointType getType() {
        return type;
    }
    
    public LocalDateTime getEarnedAt() {
        return earnedAt;
    }
    
    public boolean isExpired() {
        if (type == PointType.PERMANENT) {
            return false;
        }
        
        LocalDateTime expirationDate = earnedAt.plusDays(type.getExpirationDays());
        return LocalDateTime.now().isAfter(expirationDate);
    }
    
    public int getDaysUntilExpiration() {
        if (type == PointType.PERMANENT) {
            return Integer.MAX_VALUE;
        }
        
        LocalDateTime expirationDate = earnedAt.plusDays(type.getExpirationDays());
        return (int) ChronoUnit.DAYS.between(LocalDateTime.now(), expirationDate);
    }
} 