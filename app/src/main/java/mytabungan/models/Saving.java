package mytabungan.models;

import java.time.LocalDateTime;

public abstract class Saving {
    private int id;
    private int userId;
    protected double targetAmount;
    protected double savedAmount;
    protected LocalDateTime createdAt;
    
    public Saving(int id, int userId, double targetAmount, double savedAmount, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public double getTargetAmount() {
        return targetAmount;
    }
    public double getSavedAmount() {
        return savedAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public abstract boolean isReached();

    abstract double getRemaining();
    abstract double getProgressPercentage();
}
