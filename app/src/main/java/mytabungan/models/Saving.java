package mytabungan.models;

public class Saving {
    protected int id;
    protected int userId;
    protected double targetAmount;
    protected double savedAmount;
    protected String createdAt;
    
    public Saving(int id, int userId, double targetAmount, double savedAmount, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
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

    public String getCreatedAt() {
        return createdAt;
    }
}
