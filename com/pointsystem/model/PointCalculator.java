package com.pointsystem.model;

import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

public class PointCalculator {
    private Map<String, Map<ActivityType, Integer>> activityCountsByUser;
    private Map<String, Map<String, Integer>> commentCountsByPost;
    private Map<String, Map<String, Integer>> tagCountsByPost;
    private Map<String, Map<String, Integer>> watchTimeByVideo;

    public PointCalculator() {
        this.activityCountsByUser = new HashMap<>();
        this.commentCountsByPost = new HashMap<>();
        this.tagCountsByPost = new HashMap<>();
        this.watchTimeByVideo = new HashMap<>();
    }

    public int calculatePoints(String userId, String itemId, ActivityType activityType, double amount) {
        // Initialize user activity counts if not exists
        activityCountsByUser.putIfAbsent(userId, new HashMap<>());
        Map<String, Integer> userCounts = activityCountsByUser.get(userId);

        switch (activityType) {
            case POST:
                return 50; // Expiring points
                
            case VIDEO_POST:
                return 100; // Expiring points
                
            case LIVE_STREAM:
                return 150; // Expiring points
                
            case LIKE:
                return 0; // No points to prevent abuse
                
            case COMMENT:
                // Max 5 comments per post
                commentCountsByPost.putIfAbsent(itemId, new HashMap<>());
                Map<String, Integer> commentCounts = commentCountsByPost.get(itemId);
                int currentComments = commentCounts.getOrDefault(userId, 0);
                if (currentComments < 5) {
                    commentCounts.put(userId, currentComments + 1);
                    return 10; // Expiring points
                }
                return 0;
                
            case SHARE:
                return 25; // Expiring points
                
            case SAVE_BOOKMARK:
                return 0; // No points
                
            case VIDEO_WATCH:
                // 5 points per minute, max 50 points per video
                watchTimeByVideo.putIfAbsent(itemId, new HashMap<>());
                Map<String, Integer> watchTime = watchTimeByVideo.get(itemId);
                int currentPoints = watchTime.getOrDefault(userId, 0);
                int newPoints = Math.min(50, (int)(amount * 5)); // amount is minutes watched
                watchTime.put(userId, newPoints);
                return Math.max(0, newPoints - currentPoints);
                
            case FOLLOW:
                return 0; // No points to prevent follow/unfollow abuse
                
            case DIRECT_MESSAGE:
                return 0; // No points for private conversations
                
            case TAG_USER:
                // Max 3 tags per post
                tagCountsByPost.putIfAbsent(itemId, new HashMap<>());
                Map<String, Integer> tagCounts = tagCountsByPost.get(itemId);
                int currentTags = tagCounts.getOrDefault(userId, 0);
                if (currentTags < 3) {
                    tagCounts.put(userId, currentTags + 1);
                    return 10; // Expiring points
                }
                return 0;
                
            case COMMENT_REPLY:
                return 10; // Expiring points
                
            case BEING_TAGGED:
                return 10; // Expiring points
                
            case JOIN_GROUP:
                return 50; // Expiring points
                
            case POLL_PARTICIPATION:
                return 10; // Expiring points
                
            case CHALLENGE_PARTICIPATION:
                return 100; // Expiring points
                
            case EVENT_RSVP:
                return 50; // Expiring points
                
            case PURCHASE:
            case IN_APP_PURCHASE:
                return (int)amount; // 1 permanent point per $1 spent
                
            case CREATOR_TIP:
                return (int)(amount * 5); // 5 permanent points per $1 tipped
                
            case CREATOR_EARNINGS:
                return (int)(amount * 10); // 10 permanent points per $1 earned
                
            default:
                return 0;
        }
    }

    public void resetDailyCounts() {
        activityCountsByUser.clear();
        commentCountsByPost.clear();
        tagCountsByPost.clear();
        watchTimeByVideo.clear();
    }
} 