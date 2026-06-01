package mytabungan.models;

import java.time.LocalDateTime;

public class Wishlist extends Saving {
    private String title;
    private double maxLimit;
    private String status;
    private String period;

    public Wishlist(int id,
                    int userId,
                    String title,
                    double targetAmount,
                    double savedAmount,
                    double maxLimit,
                    String status,
                    String period,
                    LocalDateTime createdAt) {

        super(id, userId, targetAmount, savedAmount, createdAt);

        this.title = title;
        this.maxLimit = maxLimit;
        this.status = status;
        this.period = period;
    }

    public String getTitle() {
        return title;
    }

    public double getMaxLimit() {
        return maxLimit;
    }

    public String getStatus() {
        return status;
    }

    public String getPeriod() {
        return period;
    }

    public double calculateMonthlyLimit(MonthlySaving monthlySaving) {
        double percentage = Math.min(maxLimit, 70);
        return monthlySaving.getSavedAmount() * (percentage / 100);
    }

    public double calculateAllocation(double totalSaving) {
        return totalSaving * maxLimit / 100;
    }

    @Override
    public double getProgressPercentage() {
        if (targetAmount == 0) {
            return 0;
        }
        return Math.min(100, (savedAmount / targetAmount) * 100);
    }

    @Override
    public double getRemaining() {
        return Math.max(0, getTargetAmount() - getSavedAmount());
    }

    @Override
    public boolean isReached() {
        return getSavedAmount() >= getTargetAmount();
    }
}