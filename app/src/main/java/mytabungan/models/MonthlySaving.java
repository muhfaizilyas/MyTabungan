package mytabungan.models;

public class MonthlySaving {
    private int id;
    private int userId;
    private double targetAmount;
    private double savedAmount;
    private String periodMonth;
    private String createdAt;
    
    public MonthlySaving(int id, int userId, double targetAmount, double savedAmount, String periodMonth,
            String createdAt) {
        this.id = id;
        this.userId = userId;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount;
        this.periodMonth = periodMonth;
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

    public String getPeriodMonth() {
        return periodMonth;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
