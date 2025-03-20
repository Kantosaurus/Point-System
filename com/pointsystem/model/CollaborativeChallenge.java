package com.pointsystem.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class CollaborativeChallenge {
    private String challengeId;
    private String name;
    private String description;
    private int targetPoints;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String reward;
    private int currentPoints;
    private Set<String> participants;
    private boolean active;

    public CollaborativeChallenge(String challengeId, String name, String description, 
                                 int targetPoints, LocalDateTime startTime, 
                                 LocalDateTime endTime, String reward) {
        this.challengeId = challengeId;
        this.name = name;
        this.description = description;
        this.targetPoints = targetPoints;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reward = reward;
        this.currentPoints = 0;
        this.participants = new HashSet<>();
        this.active = true;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getTargetPoints() {
        return targetPoints;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getReward() {
        return reward;
    }

    public int getCurrentPoints() {
        return currentPoints;
    }

    public void addPoints(int points) {
        this.currentPoints += points;
    }

    public boolean isCompleted() {
        return currentPoints >= targetPoints;
    }

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return active && now.isAfter(startTime) && now.isBefore(endTime);
    }

    public void addParticipant(String userId) {
        participants.add(userId);
    }

    public Set<String> getParticipants() {
        return participants;
    }

    public double getProgressPercentage() {
        return Math.min(100.0, (currentPoints * 100.0) / targetPoints);
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
} 