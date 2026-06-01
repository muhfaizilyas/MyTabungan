package mytabungan.scenes;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import mytabungan.dao.GrowthDAO;
import mytabungan.models.Achievement;
import mytabungan.models.ChartData;
import mytabungan.models.UserStats;
import mytabungan.utils.SessionManager;

public class GrowthScene {

    private static final String MIDNIGHT_MIRAGE = "#001F3F";
    private static final String NAVY_CARD       = "#0A2D5A";
    private static final String NAVY_SURFACE    = "#0E3570";
    private static final String FIRST_OF_SPRING = "#DBE64C";
    private static final String WHITE           = "#FFFFFF";
    private static final String WHITE_70        = "rgba(255,255,255,0.70)";
    private static final String WHITE_40        = "rgba(255,255,255,0.40)";

    private static final NumberFormat RUP = NumberFormat.getInstance(new Locale("id", "ID"));

    private static String formatRupiah(double amount) {
        return "Rp" + RUP.format(amount);
    }

    public static ScrollPane buildPage() {

        int userId = SessionManager.getCurrentUserId();
        String username = SessionManager.getCurrentUser().getUsername();

        GrowthDAO dao = new GrowthDAO();
        UserStats stats = dao.getUserStats(userId);
        List<ChartData> chart = dao.getMonthlyChart(userId);

        VBox page = new VBox(18);
        page.setPadding(new Insets(28, 32, 28, 32));
        page.setStyle("-fx-background-color: " + MIDNIGHT_MIRAGE + ";");

        // ================= HEADER =================
        StackPane avatar = new StackPane();
        avatar.setPrefSize(44, 44);
        avatar.setMinSize(44, 44);
        avatar.setMaxSize(44, 44);
        avatar.setStyle(
                "-fx-background-color: " + NAVY_SURFACE + ";" +
                "-fx-background-radius: 50;"
        );

        Label avatarLbl = new Label(username.substring(0, 1).toUpperCase());
        avatarLbl.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: " + FIRST_OF_SPRING + ";");
        avatar.getChildren().add(avatarLbl);

        Label helloLabel = new Label("Halo,");
        helloLabel.setStyle("-fx-text-fill: " + WHITE_70 + "; -fx-font-size: 12px;");

        Label usernameLabel = new Label(username + "!");
        usernameLabel.setStyle("-fx-text-fill: " + WHITE + "; -fx-font-weight: bold; -fx-font-size: 15px;");

        VBox greetingBox = new VBox(1, helloLabel, usernameLabel);
        HBox profileBox = new HBox(12, avatar, greetingBox);
        profileBox.setAlignment(Pos.CENTER_LEFT);

        Label namafitur = new Label("MyTabungan");
        namafitur.setStyle(
                "-fx-background-color: " + FIRST_OF_SPRING + ";" +
                "-fx-text-fill: " + MIDNIGHT_MIRAGE + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 6 14 6 14;" +
                "-fx-background-radius: 20;"
        );

        Label periodeLabel = new Label("Growth Dashboard");
        periodeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + WHITE_70 + ";");

        VBox fiturBox = new VBox(3, namafitur, periodeLabel);
        fiturBox.setAlignment(Pos.CENTER);

        Region spacerHeader = new Region();
        HBox.setHgrow(spacerHeader, Priority.ALWAYS);

        HBox headerBox = new HBox(profileBox, spacerHeader, fiturBox);
        headerBox.setAlignment(Pos.CENTER);

        // ================= LEVEL =================
        Label levelLbl = new Label("LV " + stats.getLevel() + " - " + stats.getLevelName());
        levelLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + WHITE + ";");

        double progress = Math.min(stats.getLevelProgress(), 1);

        ProgressBar levelBar = new ProgressBar(progress);
        levelBar.setPrefWidth(Double.MAX_VALUE);
        levelBar.setStyle("-fx-accent: " + FIRST_OF_SPRING + ";");

        Label pointLbl = new Label(stats.getPoints() + " poin");
        pointLbl.setStyle("-fx-text-fill: " + WHITE_70 + "; -fx-font-size: 12px;");

        VBox levelCard = new VBox(8, levelLbl, levelBar, pointLbl);
        levelCard.setPadding(new Insets(16));
        levelCard.setStyle(cardStyle());

        // ================= STATS (FIX: 1 ROW 4 CARD) =================
        VBox card1 = statCard("Total Deposit", formatRupiah(stats.getTotalDeposit()));
        VBox card2 = statCard("Streak Harian", stats.getStreak() + " hari");
        VBox card3 = statCard("Streak Mingguan", stats.getStreak() / 7 + " minggu");
        VBox card4 = statCard("Avg Deposit", formatRupiah(stats.getAverageDeposit()));

        card1.setMinWidth(150);
        card2.setMinWidth(150);
        card3.setMinWidth(150);
        card4.setMinWidth(150);

        HBox statRow = new HBox(10, card1, card2, card3, card4);
        statRow.setAlignment(Pos.CENTER_LEFT);

        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        HBox.setHgrow(card3, Priority.ALWAYS);
        HBox.setHgrow(card4, Priority.ALWAYS);

        // ================= CHART =================
        VBox chartBox = new VBox(8);
        chartBox.setPadding(new Insets(16));
        chartBox.setStyle(cardStyle());

        Label chartTitle = new Label("Grafik Deposit Bulanan");
        chartTitle.setStyle("-fx-text-fill: " + WHITE + "; -fx-font-weight: bold;");

        VBox bars = new VBox(6);

        double max = chart.stream().mapToDouble(ChartData::getAmount).max().orElse(1);

        for (ChartData c : chart) {
            HBox row = new HBox(8);

            Label lbl = new Label(c.getLabel());
            lbl.setStyle("-fx-text-fill: " + WHITE_70 + "; -fx-font-size: 11px;");

            Region bar = new Region();
            double ratio = c.getAmount() / max;

            bar.setPrefHeight(8);
            bar.setPrefWidth(Math.max(10, ratio * 200));
            bar.setStyle("-fx-background-color: " + FIRST_OF_SPRING + "; -fx-background-radius: 6;");

            Label val = new Label(formatRupiah(c.getAmount()));
            val.setStyle("-fx-text-fill: " + WHITE_40 + "; -fx-font-size: 10px;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            row.getChildren().addAll(lbl, bar, spacer, val);
            bars.getChildren().add(row);
        }

        chartBox.getChildren().addAll(chartTitle, bars);

        // ================= ACHIEVEMENTS =================
        VBox achBox = new VBox(8);
        achBox.setPadding(new Insets(16));
        achBox.setStyle(cardStyle());

        Label achTitle = new Label("Achievements");
        achTitle.setStyle("-fx-text-fill: " + WHITE + "; -fx-font-weight: bold;");

        VBox achList = new VBox(6);

        for (Achievement a : stats.getAchievements()) {
            Label item = new Label(a.getIcon() + " " + a.getTitle());
            item.setStyle(
                    "-fx-text-fill: " + (a.isUnlocked() ? FIRST_OF_SPRING : WHITE_40) +
                    "; -fx-font-size: 12px;"
            );
            achList.getChildren().add(item);
        }

        achBox.getChildren().addAll(achTitle, achList);

        // ================= SIDE BY SIDE =================
        HBox middleRow = new HBox(12, chartBox, achBox);
        HBox.setHgrow(chartBox, Priority.ALWAYS);
        HBox.setHgrow(achBox, Priority.ALWAYS);

        // ================= PAGE =================
        page.getChildren().addAll(
                headerBox,
                levelCard,
                statRow,
                middleRow
        );

        // ================= SCROLL =================
        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        return scroll;
    }

    private static VBox statCard(String title, String value) {
        Label t = new Label(title);
        t.setStyle("-fx-text-fill: " + WHITE_70 + "; -fx-font-size: 11px;");

        Label v = new Label(value);
        v.setStyle("-fx-text-fill: " + WHITE + "; -fx-font-size: 15px; -fx-font-weight: bold;");

        VBox box = new VBox(4, t, v);
        box.setPadding(new Insets(12));
        box.setStyle(cardStyle());

        return box;
    }

    private static String cardStyle() {
        return "-fx-background-color: " + NAVY_CARD + ";" +
               "-fx-background-radius: 14;";
    }
}