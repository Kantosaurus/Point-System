package com.pointsystem.model;

public enum AchievementBadge {
    FIRST_POST(1, "First Post", "Created your first post", 50),
    CONTENT_CREATOR(2, "Content Creator", "Created 50 posts", 100),
    VIDEO_STAR(3, "Video Star", "Created 20 video posts", 150),
    SOCIAL_BUTTERFLY(4, "Social Butterfly", "Reached 100 followers", 200),
    ENGAGEMENT_KING(5, "Engagement King", "Received 500 likes", 250),
    LOYAL_USER(6, "Loyal User", "Logged in for 30 consecutive days", 300),
    TRENDING_MAKER(7, "Trending Maker", "Had 5 posts trending", 400),
    SUPER_COMMENTER(8, "Super Commenter", "Left 200 comments", 150);

    private final int badgeId;
    private final String name;
    private final String description;
    private final int bonusPoints;

    AchievementBadge(int badgeId, String name, String description, int bonusPoints) {
        this.badgeId = badgeId;
        this.name = name;
        this.description = description;
        this.bonusPoints = bonusPoints;
    }

    public int getBadgeId() {
        return badgeId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getBonusPoints() {
        return bonusPoints;
    }
    
    public static AchievementBadge getBadgeById(int badgeId) {
        for (AchievementBadge badge : AchievementBadge.values()) {
            if (badge.getBadgeId() == badgeId) {
                return badge;
            }
        }
        return null;
    }
} 