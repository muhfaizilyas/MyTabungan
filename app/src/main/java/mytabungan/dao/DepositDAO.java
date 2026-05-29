package mytabungan.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import mytabungan.database.DatabaseConfig;
import mytabungan.models.Deposit;

public class DepositDAO {

    public boolean addDeposit(Deposit deposit) {
        String sql = "INSERT INTO deposits (user_id, saving_type, reference_id, amount) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.connect();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, deposit.getUserId());
            ps.setString(2, deposit.getSavingType());
            ps.setInt(3, deposit.getReferenceId());
            ps.setDouble(4, deposit.getAmount());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Deposit> getDepositsByUserId(int userId) {
        String sql = "SELECT * FROM deposits WHERE user_id = ? ORDER BY created_at DESC";
        List<Deposit> deposits = new ArrayList<>();

        try (Connection conn = DatabaseConfig.connect();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                deposits.add(new Deposit(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("saving_type"),
                    rs.getInt("reference_id"),
                    rs.getDouble("amount"),
                    rs.getTimestamp("created_at").toString()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deposits;
    }
}