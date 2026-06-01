package mytabungan.database;

import java.sql.Connection;
import java.sql.Statement;

public class DBIniatializer {
    public static void init() {
        String createUsersTable =
            "CREATE TABLE IF NOT EXISTS users ("
                + "id INT PRIMARY KEY AUTO_INCREMENT,"
                + "username VARCHAR(50) NOT NULL UNIQUE,"
                + "email VARCHAR(100),"
                + "password VARCHAR(255) NOT NULL,"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
            + ");";

        String createMonthlySavings =
            "CREATE TABLE IF NOT EXISTS tabungan_utama ("
                + "id INT PRIMARY KEY AUTO_INCREMENT,"
                + "user_id INT NOT NULL,"
                + "target_amount DECIMAL(12,2) NOT NULL,"
                + "saved_amount DECIMAL(12,2) DEFAULT 0,"
                + "period_month VARCHAR(20) NOT NULL,"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY (user_id) REFERENCES users(id)"
            + ");";

        String createWishlistTable =
            "CREATE TABLE IF NOT EXISTS wishlists ("
                + "id INT PRIMARY KEY AUTO_INCREMENT,"
                + "user_id INT NOT NULL,"
                + "title VARCHAR(100) NOT NULL,"
                + "target_price DECIMAL(12,2) NOT NULL,"
                + "saved_amount DECIMAL(12,2) DEFAULT 0,"
                + "max_limit DECIMAL(12,2) NOT NULL,"
                + "status VARCHAR(20) DEFAULT 'ONGOING',"
                + "period VARCHAR(20) NOT NULL,"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY (user_id) REFERENCES users(id)"
            + ");";

        // String createChallengesTable =
        //     "CREATE TABLE IF NOT EXISTS challenges ("
        //         + "id INT PRIMARY KEY AUTO_INCREMENT,"
        //         + "user_id INT NOT NULL,"
        //         + "title VARCHAR(100) NOT NULL,"
        //         + "estimated_saving DECIMAL(12,2) DEFAULT 0,"
        //         + "status VARCHAR(20) DEFAULT 'ONGOING',"
        //         + "streak INT DEFAULT 0,"
        //         + "start_date DATE,"
        //         + "end_date DATE,"
        //         + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
        //         + "FOREIGN KEY (user_id) REFERENCES users(id)"
        //     + ");";

        String createDepositsTable =
            "CREATE TABLE IF NOT EXISTS deposits ("
                + "id INT PRIMARY KEY AUTO_INCREMENT,"
                + "user_id INT NOT NULL,"
                + "saving_type VARCHAR(20) NOT NULL,"
                + "reference_id INT,"
                + "amount DECIMAL(12,2) NOT NULL,"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY (user_id) REFERENCES users(id)"
            + ");";


        try (Connection conn = DatabaseConfig.connect();
        Statement statement = conn.createStatement()) {

            statement.execute(createUsersTable);
            statement.execute(createMonthlySavings);
            statement.execute(createWishlistTable);
            // statement.execute(createChallengesTable);
            statement.execute(createDepositsTable);

            System.out.println("Semua tabel telah tersedia!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
