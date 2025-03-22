package com.pointsystem.model;

public enum ActivityType {
    // User - Content interaction
    POST(1, 50, PointType.EXPIRING, 0, "Post content"),
    VIDEO_POST(2, 100, PointType.EXPIRING, 0, "Create a video"),
    LIVE_STREAM(3, 150, PointType.EXPIRING, 0, "Live streaming session"),
    LIKE(4, 0, PointType.NONE, 0, "Like/React to content"),
    COMMENT(5, 10, PointType.EXPIRING, 5, "Comment on content"),
    SHARE(6, 25, PointType.EXPIRING, 0, "Share/Repost content"),
    SAVE_BOOKMARK(7, 0, PointType.NONE, 0, "Save/Bookmark content"),
    VIDEO_WATCH(8, 5, PointType.EXPIRING, 50, "Watch video (per minute)"),
    
    // User - User interaction
    FOLLOW(9, 0, PointType.NONE, 0, "Follow a user"),
    DIRECT_MESSAGE(10, 0, PointType.NONE, 0, "Send direct message"),
    TAG_USER(11, 10, PointType.EXPIRING, 3, "Tag user in post"),
    COMMENT_REPLY(12, 10, PointType.EXPIRING, 0, "Reply to comment"),
    BEING_TAGGED(13, 10, PointType.EXPIRING, 0, "Being tagged in post"),
    
    // Community based interactions
    JOIN_GROUP(14, 50, PointType.EXPIRING, 0, "Join a group/community"),
    POLL_PARTICIPATION(15, 10, PointType.EXPIRING, 0, "Participate in poll"),
    CHALLENGE_PARTICIPATION(16, 100, PointType.EXPIRING, 0, "Participate in challenge"),
    EVENT_RSVP(17, 50, PointType.EXPIRING, 0, "RSVP to event"),
    
    // Commerce based interaction
    PURCHASE(18, 1, PointType.PERMANENT, 0, "Buy product (per $1)"),
    IN_APP_PURCHASE(19, 1, PointType.PERMANENT, 0, "Make in-app purchase (per $1)"),
    CREATOR_TIP(20, 5, PointType.PERMANENT, 0, "Tip creator (per $1)"),
    CREATOR_EARNINGS(21, 10, PointType.PERMANENT, 0, "Earn from content (per $1)"),
    
    // System activities
    POINTS_DECAY(22, 0, PointType.NONE, 0, "Points decay"),
    REWARD_EARNED(23, 0, PointType.PERMANENT, 0, "Reward earned");

    private final int typeId;
    private final int basePoints;
    private final PointType pointType;
    private final int maxPerItem;
    private final String description;

    ActivityType(int typeId, int basePoints, PointType pointType, int maxPerItem, String description) {
        this.typeId = typeId;
        this.basePoints = basePoints;
        this.pointType = pointType;
        this.maxPerItem = maxPerItem;
        this.description = description;
    }

    public int getTypeId() {
        return typeId;
    }

    public int getBasePoints() {
        return basePoints;
    }
    
    public PointType getPointType() {
        return pointType;
    }
    
    public int getMaxPerItem() {
        return maxPerItem;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static ActivityType getTypeById(int typeId) {
        for (ActivityType type : ActivityType.values()) {
            if (type.getTypeId() == typeId) {
                return type;
            }
        }
        return POST; // Default type
    }
} 