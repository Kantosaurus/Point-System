package com.pointsystem.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Social Media Point System
 * 
 * This system implements a comprehensive engagement-driven point system with four membership tiers:
 * - Bronze (0-999 points): Basic badges, 1 free story highlight/month
 * - Silver (1000-4999 points): Exclusive filters, 2x points on weekends
 * - Gold (5000-19999 points): Analytics dashboard, priority customer support
 * - Platinum (20000+ points): Monetization (ads revenue share), custom emojis, verified checkmark
 * 
 * The system uses a combination of data structures:
 * 1. User class - Stores user information and points
 * 2. HashMap - For efficient user lookup by ID
 * 3. PriorityQueue - For tracking top users (leaderboard functionality)
 * 4. ArrayList - For storing user activity history
 * 
 * Algorithmic Boosts:
 * - Trending posts earn +200% bonus points for 24h (encourages chasing virality)
 * - "Power User Hours": Double points during peak times (6-9 PM) to drive engagement
 * 
 * Point Decay:
 * - Points decay 5% per week for Bronze, Silver, and Gold tiers
 * - Points decay 2% per week for Platinum tier (loyalty reward)
 * 
 * Addictive Features:
 * - Progress Bars: Visual tier progression tracking
 * - Achievement Badges: Reward specific milestones with bonus points
 * - Collaborative Challenges: Group goals with exclusive rewards
 * - Surprise Drops: Random rewards during off-peak hours
 */

public class PointSystemAlgorithm {
    private PointSystem pointSystem;
    private Connection dbConnection;

    public PointSystemAlgorithm(Connection dbConnection) {
        this.pointSystem = new PointSystem();
        this.dbConnection = dbConnection;
    }

    // Initialize the system with existing users from database
    public void initializeFromDatabase() {
        try {
            String sql = "SELECT * FROM users";
            PreparedStatement stmt = dbConnection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String userId = rs.getString("user_id");
                String username = rs.getString("username");
                int totalPoints = rs.getInt("total_points");
                int tierId = rs.getInt("tier_id");
                Timestamp lastLogin = rs.getTimestamp("last_login_date");
                Timestamp lastDecay = rs.getTimestamp("last_points_decay_date");
                int loginStreak = rs.getInt("current_login_streak");
                int followersCount = rs.getInt("followers_count");
                int followingCount = rs.getInt("following_count");
                
                User user = new User(
                    userId, username, totalPoints, 
                    MembershipTier.getTierById(tierId),
                    lastLogin != null ? lastLogin.toLocalDateTime() : null,
                    lastDecay != null ? lastDecay.toLocalDateTime() : null,
                    loginStreak, followersCount, followingCount,
                    dbConnection
                );
                
                pointSystem.addExistingUser(user);
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error initializing from database: " + e.getMessage());
        }
    }

    // Process a new user registration
    public User registerNewUser(String userId, String username) {
        try {
            // Insert into database
            String sql = "INSERT INTO users (user_id, username, total_points, tier_id, " +
                        "last_login_date, last_points_decay_date, current_login_streak, " +
                        "followers_count, following_count) VALUES (?, ?, 0, 1, ?, ?, 0, 0, 0)";
            
            PreparedStatement stmt = dbConnection.prepareStatement(sql);
            stmt.setString(1, userId);
            stmt.setString(2, username);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
            stmt.close();
            
            // Create and register user in memory
            return pointSystem.registerUser(userId, username);
        } catch (SQLException e) {
            System.err.println("Error registering new user: " + e.getMessage());
            return null;
        }
    }

    // Process user login
    public void processUserLogin(String userId) {
        User user = pointSystem.getUser(userId);
        if (user != null) {
            user.login();
        }
    }

    // Process user activity
    public void processActivity(String userId, ActivityType type, String details) {
        User user = pointSystem.getUser(userId);
        if (user != null) {
            user.recordActivity(type, details);
        }
    }

    // Get user statistics
    public String getUserStats(String userId) {
        User user = pointSystem.getUser(userId);
        return user != null ? user.getUserStatsAsString() : "User not found";
    }

    // Get leaderboard
    public List<User> getLeaderboard(int limit) {
        return pointSystem.getTopUsers(limit);
    }

    // Get users by tier
    public List<User> getUsersByTier(MembershipTier tier) {
        return pointSystem.getUsersByTier(tier);
    }

    // Process weekly points decay
    public void processWeeklyPointsDecay() {
        pointSystem.applyWeeklyPointsDecay();
    }

    // Create and join collaborative challenge
    public CollaborativeChallenge createAndJoinChallenge(String userId, String name, 
                                                        String description, int targetPoints, 
                                                        int durationHours, String reward) {
        CollaborativeChallenge challenge = pointSystem.createCollaborativeChallenge(
            name, description, targetPoints, durationHours, reward
        );
        
        if (pointSystem.joinCollaborativeChallenge(userId, challenge.getChallengeId())) {
            return challenge;
        }
        return null;
    }

    // Contribute to challenge
    public boolean contributeToChallenge(String userId, String challengeId, int points) {
        return pointSystem.contributeToChallenge(userId, challengeId, points);
    }

    // Generate surprise rewards
    public void generateSurpriseRewards() {
        pointSystem.generateSurpriseRewards();
    }

    // Create random challenge
    public CollaborativeChallenge createRandomChallenge() {
        return pointSystem.createRandomChallenge();
    }

    // Select random winners for a challenge
    public List<String> selectRandomWinners(String challengeId, int numberOfWinners) {
        return pointSystem.selectRandomWinners(challengeId, numberOfWinners);
    }

    // Generate random bonus points
    public void generateRandomBonusPoints() {
        pointSystem.generateRandomBonusPoints();
    }

    // Mark post as trending
    public void markPostAsTrending(String postId) {
        pointSystem.markPostAsTrending(postId);
    }

    // Check if post is trending
    public boolean isPostTrending(String postId) {
        return pointSystem.isPostTrending(postId);
    }

    // Calculate points for post interaction
    public int calculatePostInteractionPoints(String postId, ActivityType activityType, 
                                            MembershipTier userTier) {
        return pointSystem.calculatePostInteractionPoints(postId, activityType, userTier);
    }
} 