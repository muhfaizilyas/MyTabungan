package mytabungan.models;

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
                    String createdAt) {

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
    
}
