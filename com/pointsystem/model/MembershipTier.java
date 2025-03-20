package com.pointsystem.model;

public enum MembershipTier {
    BRONZE(1, 0, 499, 1.0, 0.05, "Basic badges, 1 free story highlight/month"),
    SILVER(2, 500, 4999, 1.2, 0.05, "Exclusive filters, 2x points on weekends"),
    GOLD(3, 5000, 9999, 1.5, 0.04, "Analytics dashboard, priority customer support"),
    PLATINUM(4, 10000, Integer.MAX_VALUE, 2.0, 0.02, "Monetization (ads revenue share), custom emojis, verified checkmark");

    private final int tierId;
    private final int minPoints;
    private final int maxPoints;
    private final double pointMultiplier;
    private final double weeklyDecayRate;
    private final String perks;

    MembershipTier(int tierId, int minPoints, int maxPoints, double pointMultiplier, double weeklyDecayRate, String perks) {
        this.tierId = tierId;
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
        this.pointMultiplier = pointMultiplier;
        this.weeklyDecayRate = weeklyDecayRate;
        this.perks = perks;
    }

    public int getTierId() {
        return tierId;
    }

    public int getMinPoints() {
        return minPoints;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public double getPointMultiplier() {
        return pointMultiplier;
    }
    
    public double getWeeklyDecayRate() {
        return weeklyDecayRate;
    }
    
    public String getPerks() {
        return perks;
    }

    public static MembershipTier getTierByPoints(int points) {
        for (MembershipTier tier : MembershipTier.values()) {
            if (points >= tier.getMinPoints() && points <= tier.getMaxPoints()) {
                return tier;
            }
        }
        return BRONZE; // Default tier
    }
    
    public static MembershipTier getTierById(int tierId) {
        for (MembershipTier tier : MembershipTier.values()) {
            if (tier.getTierId() == tierId) {
                return tier;
            }
        }
        return BRONZE; // Default tier
    }
} 