    package mytabungan.dao;

    import java.sql.Connection;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;
    import java.sql.Statement;
    import java.util.ArrayList;
    import java.util.List;

    import mytabungan.database.DatabaseConfig;
import mytabungan.models.MonthlySaving;
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

        //  Ambil semua wishlist milik user (status REACHED)
        public List<Wishlist> getReachedWishlistsByUserId(int userId) {
            String sql = "SELECT * FROM wishlists WHERE user_id = ? AND status = 'REACHED' ORDER BY created_at DESC";
            List<Wishlist> list = new ArrayList<>();
            try (Connection conn = DatabaseConfig.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) list.add(mapRow(rs));
            } catch (Exception e) { e.printStackTrace(); }
            return list;
        }

        //  Ambil satu wishlist terbaru
        public Wishlist getWishlistByUserId(int userId) {
            String sql = "SELECT * FROM wishlists WHERE user_id = ? AND status = 'ONGOING' ORDER BY created_at DESC LIMIT 1";

            try (Connection conn = DatabaseConfig.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return mapRow(rs);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        //  Buat wishlist baru, kembalikan id yang di-generate
        public int createWishlist(Wishlist wishlist) {
            String sql = "INSERT INTO wishlists (user_id, title, target_price, saved_amount, max_limit, status, period) "
                    + "VALUES (?, ?, ?, 0, ?, 'ONGOING', ?)";

            try (Connection conn = DatabaseConfig.connect();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, wishlist.getUserId());
                ps.setString(2, wishlist.getTitle());
                ps.setDouble(3, wishlist.getTargetAmount());
                ps.setDouble(4, wishlist.getMaxLimit());
                ps.setString(5, wishlist.getPeriod());

                int affected = ps.executeUpdate();
                if (affected > 0) {
                    ResultSet keys = ps.getGeneratedKeys();
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1;
        }

        //  Tambah uang ke saved_amount wishlist tertentu
        public boolean addToWishlist(int wishlistId, double amount) {
            String sql = "UPDATE wishlists SET saved_amount = saved_amount + ? WHERE id = ?";

            try (Connection conn = DatabaseConfig.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setDouble(1, amount);
                ps.setInt(2, wishlistId);
                boolean ok = ps.executeUpdate() > 0;

                if (ok) {
                    checkAndMarkReached(conn, wishlistId);
                }
                return ok;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        //  Cek setelah deposit: kalau sudah tercapai, ubah status jadi REACHED
        private void checkAndMarkReached(Connection conn, int wishlistId) {
            String checkSql = "SELECT saved_amount, target_price FROM wishlists WHERE id = ?";

            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setInt(1, wishlistId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    double saved  = rs.getDouble("saved_amount");
                    double target = rs.getDouble("target_price");

                    if (saved >= target) {
                        String updateSql = "UPDATE wishlists SET status = 'REACHED' WHERE id = ?";
                        try (PreparedStatement upd = conn.prepareStatement(updateSql)) {
                            upd.setInt(1, wishlistId);
                            upd.executeUpdate();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean updateMaxLimit(int wishlistId, double newMaxLimit) {
            String sql = "UPDATE wishlists SET max_limit = ? WHERE id = ?";
            try (Connection conn = DatabaseConfig.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDouble(1, newMaxLimit);
                ps.setInt(2, wishlistId);
                return ps.executeUpdate() > 0;
            } catch (Exception e) { e.printStackTrace(); }
            return false;
        }

        //  Hapus / batalkan wishlist
        public boolean deleteWishlist(int wishlistId) {
            String sql = "UPDATE wishlists SET status = 'CANCELLED' WHERE id = ?";

            try (Connection conn = DatabaseConfig.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, wishlistId);
                return ps.executeUpdate() > 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        public boolean addToWishlistSaving(int wishlistId, double amount) {
            String sql = "UPDATE wishlists SET saved_amount = saved_amount + ? WHERE id = ?";
            try (Connection conn = DatabaseConfig.connect();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setDouble(1, amount);
                stmt.setInt(2, wishlistId);
                return stmt.executeUpdate() > 0;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        //  Hitung total persen alokasi dari semua wishlist ONGOING milik user
        public double getTotalMaxLimitByUserId(int userId) {
            String sql = "SELECT SUM(max_limit) FROM wishlists WHERE user_id = ? AND status = 'ONGOING'";

            try (Connection conn = DatabaseConfig.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return rs.getDouble(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }

        public void allocateDepositToWishlists(int userId, double depositAmount) {
            String sql = "SELECT id, max_limit FROM wishlists WHERE user_id = ? AND status = 'ONGOING'";
            try ( Connection conn = DatabaseConfig.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int wishlistId = rs.getInt("id");
                    double limit = rs.getDouble("max_limit");
                    double amount = depositAmount * limit / 100;

                    addToWishlist( wishlistId, amount );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void syncWishlistAllocation(int userId) {
            SavingDAO savingDAO = new SavingDAO();
            MonthlySaving saving = savingDAO.getSavingByUserId(userId);
            
            if (saving == null) return;

            double totalTabungan = saving.getSavedAmount();
            String sql = "UPDATE wishlists SET saved_amount = ? WHERE id = ?";
            try (Connection conn = DatabaseConfig.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {

                List<Wishlist> wishlists = getWishlistsByUserId(userId);
                for (Wishlist w : wishlists) {
                    double nominalBaru = totalTabungan * w.getMaxLimit() / 100.0;
                    ps.setDouble(1, nominalBaru);
                    ps.setInt(2, w.getId());
                    ps.addBatch();
                }
                ps.executeBatch();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //  Helper: mapping ResultSet  Wishlist
        private Wishlist mapRow(ResultSet rs) throws Exception {
            return new Wishlist(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("title"),
                rs.getDouble("target_price"),
                rs.getDouble("saved_amount"),
                rs.getDouble("max_limit"),
                rs.getString("status"),
                rs.getString("period"),
                rs.getTimestamp("created_at").toLocalDateTime()
            );
        }
    }