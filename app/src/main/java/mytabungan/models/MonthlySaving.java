package mytabungan.models;

import java.time.LocalDateTime;

public class MonthlySaving extends Saving {
    
    private String periodMonth;
    
    public MonthlySaving(int id,
                        int userId,
                        double targetAmount,
                        double savedAmount,
                        String periodMonth,
                        LocalDateTime createdAt) {

        super(id, userId, targetAmount, savedAmount, createdAt);

        this.periodMonth = periodMonth;
    }

    public String getPeriodMonth() {
        return periodMonth;
    }

    // Cek sudah tercapai atau belum targetnya
    @Override
    public boolean isReached() {
        return getSavedAmount() >= getTargetAmount();
    }

    public double getRemainingSaving(double allocationAmount) {
        return savedAmount - allocationAmount;
    }

    @Override
    public double getRemaining() {
        return Math.max(0,
            getTargetAmount() - getSavedAmount()
        );
    }

    // Persentase
    @Override
    public double getProgressPercentage() {
        if (targetAmount == 0) {
            return 0;
        }
        return Math.min( 100,
            (savedAmount / targetAmount) * 100
        );
    }

    public double getRemainingAllocationPercentage(double allocatedPercentage) {
        return Math.max(0, 100 - allocatedPercentage);
    }
}

