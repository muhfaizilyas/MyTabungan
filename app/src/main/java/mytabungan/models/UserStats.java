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
    private String motivationMessage;
    
    public UserStats( int points, int level, String levelName, int nextLevelPoints, int streak,
    double totalDeposit, int depositCount, double averageDeposit, List<Achievement> achievements, String motivationMessage) {
        this.points = points;
        this.level = level;
        this.levelName = levelName;
        this.nextLevelPoints = nextLevelPoints;
        this.streak = streak;
        this.totalDeposit = totalDeposit;
        this.depositCount = depositCount;
        this.averageDeposit = averageDeposit;
        this.achievements = achievements;
        this.motivationMessage = motivationMessage;
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

    public String getMotivationMessage() {
        return motivationMessage;
    }

    public double getLevelProgress() {
        switch (level) {
            case 1: return points / 500.0;
            case 2: return (points - 500) / 500.0;
            case 3: return (points - 1000) / 1000.0;
            case 4: return (points - 2000) / 1500.0; 
            default: return 1.0;
        }
    }
}