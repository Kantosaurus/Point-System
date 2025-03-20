# Social Media Point System

A comprehensive point system implementation for social media platforms that includes user engagement tracking, achievement badges, collaborative challenges, and gamification features.

## Package Structure

The system is organized under the `com.pointsystem.model` package with the following components:

### Core Classes

#### PointSystem.java
The main system class that manages the entire point system. Key features:
- User management (registration, retrieval)
- Leaderboard maintenance
- Trending posts tracking
- Point calculation and distribution
- Database integration for persistence

#### User.java
Represents a user in the system. Features:
- User profile management
- Points tracking and decay
- Activity history
- Achievement badges
- Following/followers management
- Post engagement tracking
- Video watch time tracking
- Surprise drop mechanics
- Login streak tracking

#### Activity.java
Represents user activities in the system. Contains:
- Activity type
- Points earned
- Activity details
- Timestamp

#### CollaborativeChallenge.java
Manages collaborative challenges between users. Features:
- Challenge creation and management
- Participant tracking
- Reward distribution
- Challenge status monitoring

### Enums

#### ActivityType.java
Defines different types of user activities:
- POST
- COMMENT
- LIKE
- SHARE
- FOLLOW
- CHALLENGE_COMPLETION
- ACHIEVEMENT_EARNED
- SURPRISE_DROP

#### AchievementBadge.java
Defines achievement badges users can earn:
- FIRST_POST
- POPULAR_POST
- ENGAGEMENT_MASTER
- CHALLENGE_CHAMPION
- STREAK_MASTER
- SOCIAL_BUTTERFLY
- CONTENT_CREATOR
- TRENDING_TOPIC
- VIDEO_STAR
- COLLABORATION_KING

#### MembershipTier.java
Defines user membership tiers:
- BRONZE
- SILVER
- GOLD
- PLATINUM

#### PointType.java
Defines different types of points:
- POST_POINTS
- COMMENT_POINTS
- LIKE_POINTS
- SHARE_POINTS
- FOLLOW_POINTS
- CHALLENGE_POINTS
- ACHIEVEMENT_POINTS
- SURPRISE_POINTS

### Algorithm Implementation

#### PointSystemAlgorithm.java
Implements the core point system algorithm. Features:
- Point calculation based on user actions
- Tier-based point multipliers
- Streak bonuses
- Engagement tracking
- Database integration
- Leaderboard management
- Activity recording
- User statistics

## Key Features

1. **User Engagement Tracking**
   - Post creation and interaction
   - Comment and like tracking
   - Video watch time monitoring
   - Following/followers management

2. **Points System**
   - Dynamic point calculation
   - Tier-based multipliers
   - Streak bonuses
   - Points decay mechanism
   - Surprise drops

3. **Achievement System**
   - Multiple achievement badges
   - Progress tracking
   - Badge unlocking
   - Achievement rewards

4. **Collaborative Features**
   - Challenge creation
   - Team participation
   - Group rewards
   - Challenge progress tracking

5. **Gamification Elements**
   - Login streaks
   - Surprise drops
   - Leaderboards
   - Tier progression

## Database Integration

The system uses a SQL database with the following main tables:
- users
- activities
- badges
- challenges
- challenge_participants
- user_rewards

## Usage

1. Initialize the system:
```java
PointSystem pointSystem = new PointSystem();
```

2. Register a new user:
```java
pointSystem.registerUser("user123", "username");
```

3. Record user activities:
```java
pointSystem.recordActivity("user123", ActivityType.POST, "Created a new post");
```

4. Get user statistics:
```java
String stats = pointSystem.getUserStats("user123");
```

5. View leaderboard:
```java
List<User> topUsers = pointSystem.getLeaderboard();
```

## Dependencies

- Java 8 or higher
- SQL database (MySQL/PostgreSQL)
- JDBC driver for database connectivity

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 