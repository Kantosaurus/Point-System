# Point System Technical Report

## A. Reward Database System

### 1. Primary Storage (JSON Files)

#### users.json
```json
{
  "users": [
    {
      "userId": "user123",
      "username": "johndoe",
      "totalPoints": 1500,
      "tier": "SILVER",
      "lastLoginDate": "2024-03-20T15:30:00",
      "lastPointsDecayDate": "2024-03-19T00:00:00",
      "currentLoginStreak": 7,
      "followersCount": 150,
      "followingCount": 120,
      "activityCounts": {
        "posts": 25,
        "videoPosts": 10,
        "comments": 150,
        "likes": 500,
        "trendingPosts": 2
      }
    }
  ]
}
```

#### activities.json
```json
{
  "activities": [
    {
      "activityId": "act123",
      "userId": "user123",
      "type": "POST",
      "timestamp": "2024-03-20T15:30:00",
      "pointsEarned": 50,
      "details": "Posted new photo",
      "multipliers": {
        "tier": 1.2,
        "time": 2.0,
        "content": 1.0
      }
    }
  ]
}
```

#### user_badges.json
```json
{
  "userBadges": [
    {
      "userId": "user123",
      "badges": [
        {
          "badgeId": "FIRST_POST",
          "earnedDate": "2024-03-01T10:00:00",
          "description": "Posted first content"
        }
      ]
    }
  ]
}
```

#### trending_posts.json
```json
{
  "trendingPosts": [
    {
      "postId": "post123",
      "userId": "user123",
      "trendingScore": 85.5,
      "startTime": "2024-03-20T15:30:00",
      "endTime": "2024-03-21T15:30:00",
      "engagement": {
        "likes": 1000,
        "comments": 150,
        "shares": 50
      }
    }
  ]
}
```

#### challenge_participants.json
```json
{
  "challenges": [
    {
      "challengeId": "challenge123",
      "participants": [
        {
          "userId": "user123",
          "joinDate": "2024-03-20T15:30:00",
          "currentPoints": 500,
          "rank": 3
        }
      ]
    }
  ]
}
```

### 2. Data Relationships
- One-to-Many: Users → Activities
  - Each user can have multiple activities
  - Activities are linked to users via userId
  - Activities maintain chronological order

- Many-to-Many: Users ↔ Badges
  - Users can earn multiple badges
  - Badges can be earned by multiple users
  - Junction table tracks earning dates

- One-to-Many: Users → Challenge Participation
  - Users can participate in multiple challenges
  - Each participation tracks individual progress
  - Challenges can have multiple participants

- One-to-One: Posts ↔ Trending Status
  - Each post can be trending at one time
  - Trending status includes score and duration
  - Posts maintain historical trending data

## B. Point System Details

### 1. Base Point Structure
Detailed breakdown of point values and conditions:

#### Content Creation
- POST: 50 points
  - Minimum 100 characters
  - Must include media or link
  - Quality check required

- VIDEO_POST: 100 points
  - Minimum 30 seconds duration
  - Must include thumbnail
  - Quality check required

- STORY: 30 points
  - 24-hour validity
  - Can include multiple media
  - Interactive elements bonus

#### Engagement
- LIKE: 5 points
  - Daily limit: 100 likes
  - Quality check required
  - Anti-spam measures

- COMMENT: 10 points
  - Minimum 10 characters
  - Must be meaningful
  - Daily limit: 50 comments

- COMMENT_REPLY: 15 points
  - Must be to existing comment
  - Minimum 10 characters
  - Daily limit: 30 replies

- SHARE: 20 points
  - Must be to valid platform
  - Unique share only
  - Daily limit: 20 shares

#### Social
- FOLLOW: 25 points
  - Must be new follow
  - Anti-spam measures
  - Daily limit: 50 follows

#### System
- DAILY_LOGIN: 50 points
  - Consecutive day bonus
  - Time window: 24 hours
  - Streak tracking

- SURPRISE_DROP: 500 points
  - Random chance: 10%
  - 24-hour cooldown
  - Tier-based probability

### 2. Multiplier System

#### Tier Multipliers
- Bronze: 0.8x
  - Points needed: 0
  - Base tier
  - No special benefits
  - Standard decay rate (5%)

- Silver: 1.0x
  - Points needed: 500
  - 20% point bonus
  - Weekend multiplier
  - Reduced decay rate (5%)

- Gold: 1.5x
  - Points needed: 5000
  - 50% point bonus
  - Weekend multiplier
  - Special challenges
  - Lower decay rate (4%)

- Platinum: 2.0x
  - Points needed: 10000
  - 100% point bonus
  - All special benefits
  - Minimal decay rate (2%)

#### Time-based Multipliers
- Power Hours (6-9 PM)
  - 2.0x multiplier
  - All content types
  - Stackable with tier

- Weekend Bonus (Silver+)
  - 2.0x multiplier
  - All activities
  - Stackable with power hours

#### Content Multipliers
- Trending Posts: 3.0x
  - Based on engagement
  - Duration: 24 hours
  - Quality requirements

- Video Content: 2.0x
  - Minimum quality
  - Engagement bonus
  - Duration bonus

### 3. Points Decay

#### Weekly Decay Rates
- Bronze: 5%
  - Standard rate
  - Weekly calculation
  - Activity offset

- Silver: 5%
  - Standard rate
  - Activity offset
  - Weekend protection

- Gold: 4%
  - Reduced rate
  - Activity offset
  - Challenge protection

- Platinum: 2%
  - Minimal rate
  - Activity offset
  - All protections

#### Decay Calculation
```java
public void applyPointsDecay() {
    double decayRate = getDecayRate();
    int activityPoints = getWeeklyActivityPoints();
    int decayAmount = (int)(totalPoints * decayRate);
    totalPoints = Math.max(0, totalPoints - decayAmount + activityPoints);
}
```

## C. Data Structures

### 1. Primary Data Structures

#### User Class
```java
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
}
```

#### Activity Class
```java
public class Activity {
    private String activityId;
    private ActivityType type;
    private LocalDateTime timestamp;
    private int pointsEarned;
    private String details;
    private Map<String, Double> multipliers;
    private boolean isValidated;
    private String validationNotes;
}
```

#### PointSystem Class
```java
public class PointSystem {
    private Map<String, User> users;
    private PriorityQueue<User> leaderboard;
    private Map<String, LocalDateTime> trendingPosts;
    private List<CollaborativeChallenge> activeChallenges;
    private Map<String, Double> tierMultipliers;
    private Map<String, Double> timeMultipliers;
    private Map<String, Double> contentMultipliers;
    private Connection dbConnection;
}
```

### 2. Supporting Data Structures

#### HashMap Usage
- User Lookup: O(1) access by userId
- Activity Tracking: O(1) updates
- Badge Management: O(1) status checks
- Challenge Progress: O(1) updates

#### PriorityQueue Usage
- Leaderboard: O(log n) updates
- Top Users: O(1) access to top user
- Rankings: O(log n) position updates
- Challenges: O(log n) participant ranking

#### ArrayList Usage
- Activity History: O(1) append
- Following/Followers: O(n) traversal
- Challenge Participants: O(n) updates
- Trending Posts: O(n) updates

#### HashSet Usage
- Challenge Participants: O(1) checks
- Badge Tracking: O(1) status
- Unique Validations: O(1) checks
- Duplicate Prevention: O(1) verification

## D. Data Structure Justifications

### 1. HashMap for Users
- O(1) Lookup Time
  - Direct access by userId
  - No traversal needed
  - Efficient updates

- Frequent Access
  - User profile views
  - Point updates
  - Activity tracking
  - Status checks

- Unique IDs
  - Natural key usage
  - No collisions
  - Easy validation

### 2. PriorityQueue for Leaderboard
- O(log n) Operations
  - Efficient updates
  - Automatic sorting
  - Quick access to top

- Sorted Order
  - Maintains ranking
  - Easy updates
  - Quick queries

- Top-N Queries
  - O(k log n) for top k
  - Efficient pagination
  - Real-time updates

### 3. ArrayList for Activities
- O(1) Append
  - Fast additions
  - No reordering
  - Memory efficient

- Sequential Access
  - Natural order
  - Easy traversal
  - Efficient iteration

- History Traversal
  - Time-based access
  - Pattern analysis
  - Activity tracking

### 4. HashSet for Challenge Participants
- O(1) Operations
  - Quick checks
  - Fast updates
  - Efficient validation

- Duplicate Prevention
  - Unique entries
  - No duplicates
  - Easy verification

- Membership Testing
  - Quick lookups
  - Status checks
  - Validation

## E. New Reward Idea: "Chain Reactions"

### 1. Concept Details

#### Chain Creation
- Users start chains with hashtags
- Minimum 3 participants required
- 24-hour time window
- Quality validation required

#### Point Structure
- Base points: 100
- Position multiplier: 1.5x
- Record bonus: 500 points
- Streak bonus: 200 points

#### Chain Types
1. Photo Chains
   - Theme-based photos
   - Quality requirements
   - Creative elements

2. Video Chains
   - Story continuation
   - Quality requirements
   - Engagement metrics

3. Challenge Chains
   - Skill-based tasks
   - Time limits
   - Achievement tracking

### 2. Benefits

#### User Engagement
- Viral content creation
- Community building
- Creative expression
- Social interaction

#### Platform Growth
- Increased activity
- Higher retention
- Better content
- More users

#### Competitive Elements
- Chain records
- User rankings
- Achievement badges
- Special rewards

## F. Implementation of Chain Reactions

### 1. Database Structure

#### chain_reactions.json
```json
{
  "chains": [
    {
      "chain_id": "chain123",
      "starter_user_id": "user1",
      "hashtag": "#SummerVibes",
      "start_time": "2024-03-20T10:00:00",
      "current_length": 5,
      "record_length": 10,
      "chain_type": "PHOTO",
      "theme": "Summer Activities",
      "quality_score": 85,
      "participants": [
        {
          "user_id": "user1",
          "position": 1,
          "points_earned": 100,
          "submission_time": "2024-03-20T10:00:00",
          "content_url": "url1",
          "quality_score": 90
        },
        {
          "user_id": "user2",
          "position": 2,
          "points_earned": 200,
          "submission_time": "2024-03-20T10:30:00",
          "content_url": "url2",
          "quality_score": 85
        }
      ],
      "achievements": [
        {
          "type": "RECORD_BREAKER",
          "user_id": "user2",
          "earned_time": "2024-03-20T11:00:00"
        }
      ]
    }
  ]
}
```

### 2. Point Calculation

#### Base Calculation
```java
public int calculateChainPoints(int position, boolean isRecordBreaker) {
    // Base points
    int basePoints = 100;
    
    // Position multiplier
    double positionMultiplier = Math.pow(1.5, position - 1);
    
    // Record breaker bonus
    int recordBonus = isRecordBreaker ? 500 : 0;
    
    // Quality bonus
    int qualityBonus = calculateQualityBonus();
    
    // Time bonus
    int timeBonus = calculateTimeBonus();
    
    return (int)(basePoints * positionMultiplier) + 
           recordBonus + 
           qualityBonus + 
           timeBonus;
}
```

#### Quality Bonus
```java
private int calculateQualityBonus() {
    if (qualityScore >= 90) return 200;
    if (qualityScore >= 80) return 100;
    if (qualityScore >= 70) return 50;
    return 0;
}
```

#### Time Bonus
```java
private int calculateTimeBonus() {
    if (submissionTime.isWithin(1, ChronoUnit.HOURS)) return 100;
    if (submissionTime.isWithin(3, ChronoUnit.HOURS)) return 50;
    if (submissionTime.isWithin(6, ChronoUnit.HOURS)) return 25;
    return 0;
}
```

### 3. Integration with Existing System

#### User Integration
- Activity tracking
- Point updates
- Achievement system
- Profile updates

#### Content Integration
- Quality validation
- Media processing
- Storage management
- Delivery system

#### Social Integration
- Notifications
- Sharing
- Following
- Engagement tracking

### 4. Implementation Steps

#### Phase 1: Foundation
1. Database Structure
   - Create chain_reactions.json
   - Define schemas
   - Set up indexes

2. Core Classes
   - ChainReaction
   - ChainParticipant
   - ChainAchievement

3. Basic Methods
   - Chain creation
   - Participation
   - Point calculation

#### Phase 2: Features
1. Chain Management
   - Validation
   - Quality checks
   - Time management

2. Point System
   - Calculations
   - Multipliers
   - Bonuses

3. Achievement System
   - Badges
   - Records
   - Special rewards

#### Phase 3: Integration
1. User System
   - Profile updates
   - Activity tracking
   - Point management

2. Content System
   - Media handling
   - Quality control
   - Storage

3. Social System
   - Notifications
   - Sharing
   - Engagement

#### Phase 4: UI/UX
1. Chain Creation
   - Interface
   - Guidelines
   - Templates

2. Participation
   - Submission
   - Progress
   - Rewards

3. Visualization
   - Chain display
   - Leaderboards
   - Achievements

### 5. Expected Impact

#### User Engagement
- Increased activity
- Better content
- More interaction
- Higher retention

#### Platform Growth
- More users
- Better metrics
- Higher revenue
- Market position

#### Community Building
- Stronger bonds
- Better culture
- More creativity
- Higher satisfaction 