package mytabungan.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import mytabungan.database.DatabaseConfig;
import mytabungan.models.Wishlist;

public class WishlistDAO {

    //  Ambil semua wishlist milik user (status ONGOING) 
    public List<Wishlist> getWishlistsByUserId(int userId) {
        String sql = "SELECT * FROM wishlists WHERE user_id = ? AND status = 'ONGOING' ORDER BY created_at ASC";
        List<Wishlist> list = new ArrayList<>();

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }