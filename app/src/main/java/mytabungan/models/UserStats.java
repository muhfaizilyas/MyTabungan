package mytabungan.models;

import java.util.List;

public class UserStats {
    private int points;
    private int level;
    private String levelName;
    private int nextLevelPoints;
    private int streak;
    private double totalDeposit;
    private int depositCount;
    private double averageDeposit;
    private List<Achievement> achievements;
    
    public UserStats( int points, int level, String levelName, int nextLevelPoints, int streak,
    double totalDeposit, int depositCount, double averageDeposit, List<Achievement> achievements ) {
        this.points = points;
        this.level = level;
        this.levelName = levelName;
        this.streak = streak;
        this.totalDeposit = totalDeposit;
        this.depositCount = depositCount;
        this.averageDeposit = averageDeposit;
        this.achievements = achievements;
    }

    public int getPoints() {
        return points;
    }

    public int getLevel() {
        return level;
    }

    public String getLevelName() {
        return levelName;
    }

    public int getNextLevelPoints() {
        return nextLevelPoints;
    }

    public int getStreak() {
        return streak;
    }

    public double getTotalDeposit() {
        return totalDeposit;
    }

    public int getDepositCount() {
        return depositCount;
    }

    public double getAverageDeposit() {
        return averageDeposit;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }
}