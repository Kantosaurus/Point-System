package com.pointsystem.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class User implements Comparable<User> {
    private String userId;
    private String username;
    private int totalPoints;
    private MembershipTier tier;
    private LocalDateTime lastLoginDate;
    private LocalDateTime lastPointsDecayDate;
    private int currentLoginStreak;
    private List<Activity> activityHistory;
    private int followersCount;
    private int followingCount;
    private Map<AchievementBadge, Boolean> earnedBadges;
    private Map<String, Integer> activityCounts;
    private Set<String> participatingChallenges;
    private LocalDateTime lastSurpriseDropCheck;
    private Connection dbConnection;
    private List<String> following;
    private List<String> followers;
    private Map<String, Integer> commentCountByPost;
    private Map<String, Integer> tagCountByPost;
    private Map<String, Integer> watchTimeByVideo;

    public User(String userId, String username, int totalPoints, MembershipTier tier, 
                LocalDateTime lastLoginDate, LocalDateTime lastPointsDecayDate, 
                int currentLoginStreak, int followersCount, int followingCount, Connection dbConnection) {
        this.userId = userId;
        this.username = username;
        this.totalPoints = totalPoints;
        this.tier = tier;
        this.lastLoginDate = lastLoginDate;
        this.lastPointsDecayDate = lastPointsDecayDate;
        this.currentLoginStreak = currentLoginStreak;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.activityHistory = new ArrayList<>();
        this.earnedBadges = new HashMap<>();
        this.activityCounts = new HashMap<>();
        this.participatingChallenges = new HashSet<>();
        this.lastSurpriseDropCheck = LocalDateTime.now();
        this.dbConnection = dbConnection;
        this.following = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.commentCountByPost = new HashMap<>();
        this.tagCountByPost = new HashMap<>();
        this.watchTimeByVideo = new HashMap<>();
        
        // Initialize activity counts
        activityCounts.put("posts", 0);
        activityCounts.put("videoPosts", 0);
        activityCounts.put("comments", 0);
        activityCounts.put("likes", 0);
        activityCounts.put("trendingPosts", 0);
        
        // Load earned badges from database
        loadEarnedBadges();
        
        // Load participating challenges
        loadParticipatingChallenges();
    }

    public User(String userId, String username) {
        this.userId = userId;
        this.username = username;
        this.totalPoints = 0;
        this.tier = MembershipTier.BRONZE;
        this.lastLoginDate = null;
        this.lastPointsDecayDate = LocalDateTime.now();
        this.currentLoginStreak = 0;
        this.followersCount = 0;
        this.followingCount = 0;
        this.activityHistory = new ArrayList<>();
        this.earnedBadges = new HashMap<>();
        this.activityCounts = new HashMap<>();
        this.participatingChallenges = new HashSet<>();
        this.lastSurpriseDropCheck = LocalDateTime.now();
        this.following = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.commentCountByPost = new HashMap<>();
        this.tagCountByPost = new HashMap<>();
        this.watchTimeByVideo = new HashMap<>();
        
        // Initialize activity counts
        activityCounts.put("posts", 0);
        activityCounts.put("videoPosts", 0);
        activityCounts.put("comments", 0);
        activityCounts.put("likes", 0);
        activityCounts.put("trendingPosts", 0);
        
        // Initialize all badges as not earned
        for (AchievementBadge badge : AchievementBadge.values()) {
            earnedBadges.put(badge, false);
        }
    }

    private void loadEarnedBadges() {
        try {
            String sql = "SELECT badge_id FROM user_badges WHERE user_id = ?";
            PreparedStatement stmt = dbConnection.prepareStatement(sql);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            // Initialize all badges as not earned
            for (AchievementBadge badge : AchievementBadge.values()) {
                earnedBadges.put(badge, false);
            }
            
            // Mark earned badges
            while (rs.next()) {
                int badgeId = rs.getInt("badge_id");
                AchievementBadge badge = AchievementBadge.getBadgeById(badgeId);
                if (badge != null) {
                    earnedBadges.put(badge, true);
                }
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error loading earned badges: " + e.getMessage());
        }
    }
    
    private void loadParticipatingChallenges() {
        try {
            String sql = "SELECT challenge_id FROM challenge_participants WHERE user_id = ?";
            PreparedStatement stmt = dbConnection.prepareStatement(sql);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                participatingChallenges.add(rs.getString("challenge_id"));
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error loading participating challenges: " + e.getMessage());
        }
    }

    @Override
    public int compareTo(User other) {
        // For PriorityQueue to sort users by points (descending)
        return Integer.compare(other.totalPoints, this.totalPoints);
    }

    public void checkAndApplyPointsDecay() {
        if (lastPointsDecayDate != null) {
            long daysSinceLastDecay = ChronoUnit.DAYS.between(lastPointsDecayDate, LocalDateTime.now());
            if (daysSinceLastDecay >= 30) { // Apply decay every 30 days
                totalPoints = (int) (totalPoints * 0.95); // 5% decay
                lastPointsDecayDate = LocalDateTime.now();
            }
        }
    }

    public void applyPointsDecay() {
        checkAndApplyPointsDecay();
    }

    public MembershipTier getTier() {
        return tier;
    }

    public String getUserId() {
        return userId;
    }

    public void addPoints(int points, PointType type) {
        this.totalPoints += points;
        // Update activity history
        activityHistory.add(new Activity(ActivityType.valueOf(type.name()), points, "Points added"));
    }

    public void recordActivity(ActivityType type, String details) {
        activityHistory.add(new Activity(type, 0, details));
    }

    public void joinChallenge(String challengeId) {
        if (!participatingChallenges.contains(challengeId)) {
            participatingChallenges.add(challengeId);
            try {
                String sql = "INSERT INTO challenge_participants (user_id, challenge_id) VALUES (?, ?)";
                PreparedStatement stmt = dbConnection.prepareStatement(sql);
                stmt.setString(1, userId);
                stmt.setString(2, challengeId);
                stmt.executeUpdate();
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Error joining challenge: " + e.getMessage());
            }
        }
    }

    public Set<String> getParticipatingChallenges() {
        return participatingChallenges;
    }

    public void addReward(String reward) {
        // Store reward in database
        try {
            String sql = "INSERT INTO user_rewards (user_id, reward) VALUES (?, ?)";
            PreparedStatement stmt = dbConnection.prepareStatement(sql);
            stmt.setString(1, userId);
            stmt.setString(2, reward);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error adding reward: " + e.getMessage());
        }
    }

    public void login() {
        LocalDateTime now = LocalDateTime.now();
        if (lastLoginDate != null) {
            LocalDateTime lastLogin = lastLoginDate.toLocalDate().atStartOfDay();
            LocalDateTime today = now.toLocalDate().atStartOfDay();
            
            if (lastLogin.equals(today.minusDays(1))) {
                currentLoginStreak++;
            } else if (!lastLogin.equals(today)) {
                currentLoginStreak = 1;
            }
        } else {
            currentLoginStreak = 1;
        }
        
        lastLoginDate = now;
        checkAndApplyPointsDecay();
    }

    public Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userId", userId);
        stats.put("username", username);
        stats.put("totalPoints", totalPoints);
        stats.put("tier", tier);
        stats.put("loginStreak", currentLoginStreak);
        stats.put("followersCount", followersCount);
        stats.put("followingCount", followingCount);
        stats.put("activityCounts", activityCounts);
        stats.put("earnedBadges", earnedBadges);
        stats.put("participatingChallenges", participatingChallenges);
        return stats;
    }

    public String getUserStatsAsString() {
        Map<String, Object> stats = getUserStats();
        StringBuilder sb = new StringBuilder();
        sb.append("User Stats:\n");
        sb.append("ID: ").append(stats.get("userId")).append("\n");
        sb.append("Username: ").append(stats.get("username")).append("\n");
        sb.append("Points: ").append(stats.get("totalPoints")).append("\n");
        sb.append("Tier: ").append(stats.get("tier")).append("\n");
        sb.append("Login Streak: ").append(stats.get("loginStreak")).append("\n");
        sb.append("Followers: ").append(stats.get("followersCount")).append("\n");
        sb.append("Following: ").append(stats.get("followingCount")).append("\n");
        return sb.toString();
    }

    // Surprise drop methods
    public boolean checkSurpriseDrop() {
        LocalDateTime now = LocalDateTime.now();
        if (ChronoUnit.HOURS.between(lastSurpriseDropCheck, now) >= 24) {
            lastSurpriseDropCheck = now;
            return Math.random() < 0.1; // 10% chance of surprise drop
        }
        return false;
    }

    // Following/Followers management
    public void followUser(String userId) {
        if (!following.contains(userId)) {
            following.add(userId);
            followingCount++;
        }
    }

    public void unfollowUser(String userId) {
        if (following.remove(userId)) {
            followingCount--;
        }
    }

    public void addFollower(String userId) {
        if (!followers.contains(userId)) {
            followers.add(userId);
            followersCount++;
        }
    }

    public void removeFollower(String userId) {
        if (followers.remove(userId)) {
            followersCount--;
        }
    }

    public List<String> getFollowing() {
        return new ArrayList<>(following);
    }

    public List<String> getFollowers() {
        return new ArrayList<>(followers);
    }

    // Post engagement tracking
    public void incrementCommentCount(String postId) {
        commentCountByPost.merge(postId, 1, Integer::sum);
    }

    public void incrementTagCount(String postId) {
        tagCountByPost.merge(postId, 1, Integer::sum);
    }

    public void recordVideoWatchTime(String videoId, int watchTimeInSeconds) {
        watchTimeByVideo.merge(videoId, watchTimeInSeconds, Integer::sum);
    }

    public int getCommentCountForPost(String postId) {
        return commentCountByPost.getOrDefault(postId, 0);
    }

    public int getTagCountForPost(String postId) {
        return tagCountByPost.getOrDefault(postId, 0);
    }

    public int getWatchTimeForVideo(String videoId) {
        return watchTimeByVideo.getOrDefault(videoId, 0);
    }

    // ... [Rest of the User class methods remain the same]
    // Note: The rest of the methods from the original file should be copied here
} 