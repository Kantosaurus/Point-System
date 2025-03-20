package com.pointsystem.model;

public enum ActivityType {
    POST(1, 50, PointType.PERMANENT),
    VIDEO_POST(2, 100, PointType.PERMANENT),
    STORY(3, 30, PointType.EXPIRING),
    LIKE(4, 5, PointType.EXPIRING),
    COMMENT(5, 10, PointType.EXPIRING),
    COMMENT_REPLY(6, 15, PointType.EXPIRING),
    SHARE(7, 20, PointType.EXPIRING),
    FOLLOW(8, 25, PointType.EXPIRING),
    DAILY_LOGIN(9, 50, PointType.EXPIRING),
    SURPRISE_DROP(10, 500, PointType.EXPIRING),
    POINTS_DECAY(11, 0, PointType.NONE),
    REWARD_EARNED(12, 0, PointType.PERMANENT);

    private final int typeId;
    private final int basePoints;
    private final PointType pointType;

    ActivityType(int typeId, int basePoints, PointType pointType) {
        this.typeId = typeId;
        this.basePoints = basePoints;
        this.pointType = pointType;
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
    
    public static ActivityType getTypeById(int typeId) {
        for (ActivityType type : ActivityType.values()) {
            if (type.getTypeId() == typeId) {
                return type;
            }
        }
        return DAILY_LOGIN; // Default type
    }
} 