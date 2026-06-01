package mytabungan.models;

public class ChartData {
    private String label;
    private double amount;

    public ChartData(String label, double amount) {
        this.label = label;
        this.amount = amount;
    }

    public String getLabel() {
        return label;
    }

    public double getAmount() {
        return amount;
    }
}