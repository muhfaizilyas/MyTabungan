package mytabungan.models;

public class MonthlySaving extends Saving {
    
    private String periodMonth;  
    
        public MonthlySaving(int id,
                         int userId,
                         double targetAmount,
                         double savedAmount,
                         String periodMonth,
                         String createdAt) {

        super(id, userId, targetAmount, savedAmount, createdAt);

        this.periodMonth = periodMonth;
    }

        public String getPeriodMonth() {
            return periodMonth;
        }         
}

