package com.pointsystem.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;

public class PointSystem {
    private Map<String, User> users;
    private PriorityQueue<User> leaderboard;
    private Map<String, LocalDateTime> trendingPosts;
    private List<CollaborativeChallenge> activeCollaborativeChallenges;
    private Random random;
    private Map<String, List<String>> productPurchases; // productId -> list of buyer userIds
    private Connection dbConnection;
    private PointCalculator pointCalculator;
    
    // Fast access data structures
    private Map<MembershipTier, Set<String>> usersByTier; // tier -> set of userIds
    private TreeMap<Integer, Set<String>> usersByPoints; // points -> set of userIds
    private static final int POINT_RANGE_SIZE = 1000; // Size of each point range bucket

    public PointSystem() {
        this.users = new HashMap<>();
        this.leaderboard = new PriorityQueue<>();
        this.trendingPosts = new HashMap<>();
        this.activeCollaborativeChallenges = new ArrayList<>();
        this.random = new Random();
        this.productPurchases = new HashMap<>();
        this.usersByTier = new HashMap<>();
        this.usersByPoints = new TreeMap<>();
        this.pointCalculator = new PointCalculator();
        
        // Initialize tier sets
        for (MembershipTier tier : MembershipTier.values()) {
            usersByTier.put(tier, new HashSet<>());
        }
    }

    public PointSystem(Connection dbConnection) {
        this();
        this.dbConnection = dbConnection;
    }

    public User registerUser(String userId, String username) {
        User newUser = new User(userId, username);
        users.put(userId, newUser);
        leaderboard.add(newUser);
        updateUserIndices(newUser);
        return newUser;
    }

    public void addExistingUser(User user) {
        users.put(user.getUserId(), user);
        leaderboard.add(user);
        updateUserIndices(user);
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public void updateLeaderboard() {
        leaderboard.clear();
        leaderboard.addAll(users.values());
    }
    
    public void applyWeeklyPointsDecay() {
        for (User user : users.values()) {
            user.applyPointsDecay();
        }
        updateLeaderboard();
    }
    
    public List<User> getTopUsers(int n) {
        List<User> topUsers = new ArrayList<>();
        PriorityQueue<User> tempQueue = new PriorityQueue<>(leaderboard);
        
        for (int i = 0; i < n && !tempQueue.isEmpty(); i++) {
            topUsers.add(tempQueue.poll());
        }
        
        return topUsers;
    }
    
    public List<User> getUsersByTier(MembershipTier tier) {
        List<User> tierUsers = new ArrayList<>();
        
        for (User user : users.values()) {
            if (user.getTier() == tier) {
                tierUsers.add(user);
            }
        }
        
        return tierUsers;
    }
    
    public void markPostAsTrending(String postId) {
        trendingPosts.put(postId, LocalDateTime.now());
    }
    
    public boolean isPostTrending(String postId) {
        if (trendingPosts.containsKey(postId)) {
            LocalDateTime trendingStartTime = trendingPosts.get(postId);
            LocalDateTime now = LocalDateTime.now();
            
            if (now.isBefore(trendingStartTime.plusHours(24))) {
                return true;
            } else {
                trendingPosts.remove(postId);
            }
        }
        return false;
    }
    
    public int calculatePoints(String userId, String itemId, ActivityType activityType, double amount) {
        User user = users.get(userId);
        if (user == null) return 0;

        int basePoints = pointCalculator.calculatePoints(userId, itemId, activityType, amount);
        if (basePoints == 0) return 0;

        double multiplier = 1.0;
        
        // Apply tier multiplier
        multiplier *= user.getTier().getPointMultiplier();
        
        // Apply trending multiplier if applicable
        if (itemId != null && isPostTrending(itemId)) {
            multiplier *= 3.0;
        }
        
        // Apply power hour multiplier
        LocalTime currentTime = LocalTime.now();
        LocalTime startPowerHour = LocalTime.of(18, 0);
        LocalTime endPowerHour = LocalTime.of(21, 0);
        if (currentTime.isAfter(startPowerHour) && currentTime.isBefore(endPowerHour)) {
            multiplier *= 2.0;
        }

        return (int)(basePoints * multiplier);
    }

    public void resetDailyCounts() {
        pointCalculator.resetDailyCounts();
    }

    public CollaborativeChallenge createCollaborativeChallenge(String name, String description, 
                                                              int targetPoints, int durationHours, 
                                                              String reward) {
        String challengeId = "challenge-" + UUID.randomUUID().toString().substring(0, 8);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(durationHours);
        
        CollaborativeChallenge challenge = new CollaborativeChallenge(
            challengeId, name, description, targetPoints, startTime, endTime, reward
        );
        
        activeCollaborativeChallenges.add(challenge);
        return challenge;
    }
    
    public boolean joinCollaborativeChallenge(String userId, String challengeId) {
        User user = users.get(userId);
        if (user == null) {
            return false;
        }
        
        for (CollaborativeChallenge challenge : activeCollaborativeChallenges) {
            if (challenge.getChallengeId().equals(challengeId) && challenge.isActive()) {
                challenge.addParticipant(userId);
                user.joinChallenge(challengeId);
                return true;
            }
        }
        
        return false;
    }
    
    public boolean contributeToChallenge(String userId, String challengeId, int points) {
        User user = users.get(userId);
        if (user == null || !user.getParticipatingChallenges().contains(challengeId)) {
            return false;
        }
        
        for (CollaborativeChallenge challenge : activeCollaborativeChallenges) {
            if (challenge.getChallengeId().equals(challengeId) && challenge.isActive()) {
                challenge.addPoints(points);
                
                if (challenge.isCompleted()) {
                    challenge.setActive(false);
                    for (String participantId : challenge.getParticipants()) {
                        User participant = users.get(participantId);
                        if (participant != null) {
                            participant.addReward(challenge.getReward());
                        }
                    }
                }
            }
        }
        return true;
    }

    public void generateSurpriseRewards() {
        for (User user : users.values()) {
            if (random.nextInt(100) < 5) {
                int rewardPoints = random.nextInt(50) + 50;
                user.addReward("Surprise Reward: " + rewardPoints + " points!");
                user.addPoints(rewardPoints, PointType.EXPIRING);
            }
        }
    }

    public CollaborativeChallenge createRandomChallenge() {
        String[] challengeTypes = {
            "Video Marathon", "Comment Spree", "Like Party", "Share Fest", "Tag Team"
        };
        String[] rewards = {
            "Exclusive Badge", "Special Filter", "Profile Frame", "Custom Emoji", "Priority Support"
        };
        
        String name = challengeTypes[random.nextInt(challengeTypes.length)];
        String description = "Complete this " + name.toLowerCase() + " challenge to earn rewards!";
        int targetPoints = random.nextInt(1000) + 500;
        int durationHours = random.nextInt(24) + 24;
        String reward = rewards[random.nextInt(rewards.length)];
        
        return createCollaborativeChallenge(name, description, targetPoints, durationHours, reward);
    }

    public List<String> selectRandomWinners(String challengeId, int numberOfWinners) {
        List<String> winners = new ArrayList<>();
        CollaborativeChallenge challenge = null;
        
        for (CollaborativeChallenge c : activeCollaborativeChallenges) {
            if (c.getChallengeId().equals(challengeId)) {
                challenge = c;
                break;
            }
        }
        
        if (challenge != null) {
            List<String> participants = new ArrayList<>(challenge.getParticipants());
            int numWinners = Math.min(numberOfWinners, participants.size());
            
            for (int i = 0; i < numWinners; i++) {
                if (participants.isEmpty()) break;
                int winnerIndex = random.nextInt(participants.size());
                winners.add(participants.remove(winnerIndex));
            }
        }
        
        return winners;
    }

    public void generateRandomBonusPoints() {
        LocalTime currentTime = LocalTime.now();
        LocalTime startOffPeak = LocalTime.of(23, 0);
        LocalTime endOffPeak = LocalTime.of(6, 0);
        
        int bonusChance = (currentTime.isAfter(startOffPeak) || currentTime.isBefore(endOffPeak)) ? 15 : 5;
        
        for (User user : users.values()) {
            if (random.nextInt(100) < bonusChance) {
                int bonusPoints = random.nextInt(20) + 10;
                user.addPoints(bonusPoints, PointType.EXPIRING);
                user.recordActivity(ActivityType.SURPRISE_DROP, 
                    "Random Bonus: " + bonusPoints + " points for being active!");
            }
        }
    }

    /**
     * Conducts a lucky draw event that can affect users based on their points range and/or tier.
     * @param minPoints Minimum points for the range (inclusive)
     * @param maxPoints Maximum points for the range (inclusive)
     * @param selectedTier Optional tier to filter users (null for all tiers)
     * @return Number of users affected by the lucky draw
     */
    public int conductLuckyDraw(int minPoints, int maxPoints, MembershipTier selectedTier) {
        int affectedUsers = 0;
        Set<String> eligibleUsers = new HashSet<>();

        // Get eligible users based on points range
        for (Map.Entry<Integer, Set<String>> entry : usersByPoints.entrySet()) {
            int rangeStart = entry.getKey();
            if (rangeStart >= minPoints && rangeStart <= maxPoints) {
                eligibleUsers.addAll(entry.getValue());
            }
        }

        // Filter by tier if specified
        if (selectedTier != null) {
            eligibleUsers.retainAll(usersByTier.get(selectedTier));
        }

        // Apply lucky draw to eligible users
        for (String userId : eligibleUsers) {
            User user = users.get(userId);
            if (user != null) {
                double tierMultiplier = user.getTier().getPointMultiplier();
                int originalPoints = user.getTotalPoints();
                int newPoints = (int) (originalPoints * tierMultiplier);
                int bonusPoints = newPoints - originalPoints;
                
                user.addPoints(bonusPoints, PointType.EXPIRING);
                user.recordActivity(ActivityType.SURPRISE_DROP, 
                    String.format("Lucky Draw: Points multiplied by %.2fx! (+%d points)", 
                        tierMultiplier, bonusPoints));
                
                affectedUsers++;
            }
        }

        updateLeaderboard();
        return affectedUsers;
    }

    /**
     * Conducts a lucky draw event with random point range.
     * @return Number of users affected by the lucky draw
     */
    public int conductRandomLuckyDraw() {
        // Randomly select a point range
        int minPoints = random.nextInt(1000) + 500; // 500-1500
        int maxPoints = minPoints + random.nextInt(1000) + 500; // minPoints to minPoints+1500

        // 30% chance to select a specific tier
        MembershipTier selectedTier = null;
        if (random.nextDouble() < 0.3) {
            MembershipTier[] tiers = MembershipTier.values();
            selectedTier = tiers[random.nextInt(tiers.length)];
        }

        return conductLuckyDraw(minPoints, maxPoints, selectedTier);
    }

    /**
     * Records a product purchase by a user
     * @param userId The ID of the user who made the purchase
     * @param productId The ID of the purchased product
     */
    public void recordProductPurchase(String userId, String productId) {
        productPurchases.computeIfAbsent(productId, _ -> new ArrayList<>()).add(userId);
    }

    /**
     * Conducts a product promotion where a random buyer from non-platinum tiers gets upgraded to Platinum
     * @param productId The ID of the product for the promotion
     * @return The selected user's ID if a winner was chosen, null otherwise
     */
    public String conductProductPromotion(String productId) {
        List<String> buyers = productPurchases.get(productId);
        if (buyers == null || buyers.isEmpty()) {
            return null;
        }

        // Get eligible buyers (non-platinum tiers) using the tier index
        Set<String> eligibleBuyers = new HashSet<>();
        for (MembershipTier tier : Arrays.asList(MembershipTier.BRONZE, MembershipTier.SILVER, MembershipTier.GOLD)) {
            eligibleBuyers.addAll(usersByTier.get(tier));
        }
        eligibleBuyers.retainAll(buyers);

        if (eligibleBuyers.isEmpty()) {
            return null;
        }

        // Select random winner
        String winnerId = new ArrayList<>(eligibleBuyers).get(random.nextInt(eligibleBuyers.size()));
        User winner = users.get(winnerId);
        
        // Remove user from old indices
        removeUserFromIndices(winner);
        
        // Upgrade winner to Platinum
        winner.setTier(MembershipTier.PLATINUM);
        winner.recordActivity(ActivityType.REWARD_EARNED, 
            "Congratulations! You've been upgraded to Platinum tier through the product promotion!");
        
        // Update indices with new tier
        updateUserIndices(winner);
        
        // Update database
        try {
            String sql = "UPDATE users SET tier_id = ? WHERE user_id = ?";
            PreparedStatement stmt = dbConnection.prepareStatement(sql);
            stmt.setInt(1, MembershipTier.PLATINUM.getTierId());
            stmt.setString(2, winnerId);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error updating user tier: " + e.getMessage());
        }

        return winnerId;
    }

    private void updateUserIndices(User user) {
        // Update tier index
        usersByTier.get(user.getTier()).add(user.getUserId());
        
        // Update points index
        int pointsRange = (user.getTotalPoints() / POINT_RANGE_SIZE) * POINT_RANGE_SIZE;
        usersByPoints.computeIfAbsent(pointsRange, _ -> new HashSet<>()).add(user.getUserId());
    }

    private void removeUserFromIndices(User user) {
        // Remove from tier index
        usersByTier.get(user.getTier()).remove(user.getUserId());
        
        // Remove from points index
        int pointsRange = (user.getTotalPoints() / POINT_RANGE_SIZE) * POINT_RANGE_SIZE;
        Set<String> usersInRange = usersByPoints.get(pointsRange);
        if (usersInRange != null) {
            usersInRange.remove(user.getUserId());
            if (usersInRange.isEmpty()) {
                usersByPoints.remove(pointsRange);
            }
        }
    }
} 