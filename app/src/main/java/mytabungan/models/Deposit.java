package mytabungan.models;

import java.time.LocalDateTime;

public class Deposit {
    private int id;
    private int userId;
    private String savingType;
    private int referenceId;
    private double amount;
    private LocalDateTime createdAt;

    public Deposit(int id, int userId, String savingType, int referenceId, double amount, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.savingType = savingType;
        this.referenceId = referenceId;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public int getId(){
        return id;
    }

    public int getUserId(){
        return userId;
    }

    public String getSavingType(){
        return savingType;
    }

    public int getReferenceId(){
        return referenceId;
    }

    public double getAmount(){
        return amount;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
}
