package mytabungan.scenes;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import mytabungan.dao.DepositDAO;
import mytabungan.dao.SavingDAO;
import mytabungan.dao.WishlistDAO;
import mytabungan.models.Deposit;
import mytabungan.models.MonthlySaving;
import mytabungan.models.Wishlist;
import mytabungan.utils.SessionManager;

public class TabunganScene {
    private static final String MIDNIGHT_MIRAGE = "#001F3F";
    private static final String NAVY_CARD       = "#0A2D5A";
    private static final String NAVY_SURFACE    = "#0E3570";
    private static final String FIRST_OF_SPRING = "#DBE64C";
    private static final String SPRING_DARK     = "#b8c23a";
    private static final String WHITE           = "#FFFFFF";
    private static final String WHITE_70        = "rgba(255,255,255,0.70)";
    private static final String WHITE_40        = "rgba(255,255,255,0.40)";

    private static final NumberFormat RUP = NumberFormat.getInstance (new Locale("id", "ID"));
    private static String formatRupiah(double amount) {
        return "Rp" + RUP.format(amount);
    }
    
    public static VBox buildPage() {
        String username = SessionManager.getCurrentUser().getUsername();
        int userId = SessionManager.getCurrentUserId();

        // Data dari DAO
        SavingDAO savingDAO = new SavingDAO();
        DepositDAO depositDAO = new DepositDAO();
        // Wishlist wishlist = new WishlistDAO().getWishlistByUserId(userId);
        MonthlySaving tabunganData = savingDAO.getSavingByUserId(userId);
        
        // Halaman saat tabungan belum ada di DB
        if (tabunganData == null) {
            Label title = new Label("Belum ada tabungan");
            title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + FIRST_OF_SPRING + ";");
            Label desc = new Label("Silakan tentukan target tabungan bulan ini terlebih dahulu.");
            desc.setStyle("-fx-font-size: 13px; -fx-text-fill: " + WHITE_70 + ";");

            VBox box = new VBox(12, title, desc);
            box.setAlignment(Pos.CENTER);
            box.setPadding(new Insets(30));
            box.setStyle("-fx-background-color: " + NAVY_CARD +  ";-fx-background-radius: 20;");

            VBox wrapper = new VBox(box);
            wrapper.setAlignment(Pos.CENTER);
            wrapper.setPrefHeight(600);
            wrapper.setStyle("-fx-background-color: " + MIDNIGHT_MIRAGE + "; -fx-padding: 40;");
            return wrapper;
        }
        final MonthlySaving tabungan = tabunganData;

        // Reset tabungan per bulannya
        String currentPeriod = YearMonth.now()
            .format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("id", "ID")));
        String periode = tabungan.getPeriodMonth();
        if (!periode.equals(currentPeriod)) {
            savingDAO.resetTabungan(tabungan.getId(), currentPeriod);
            tabunganData = savingDAO.getSavingByUserId(userId); // untuk refresh datanya lagi
        }

        // String wishlistAktif = "—";
        WishlistDAO wishlistDAO = new WishlistDAO();
        List<Wishlist> wishlists = wishlistDAO.getWishlistsByUserId(userId);
        List<Deposit> deposits = new DepositDAO().getDepositsByUserId(userId);

        // === Root Page ===
        VBox page = new VBox(18);
        page.setPadding(new Insets(28, 32, 28, 32));
        page.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        page.setStyle("-fx-backround-color:" + NAVY_CARD + ";");
        
        // Header
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

        Label helloLabel    = new Label("Halo,");
        helloLabel.setStyle("-fx-text-fill: " + WHITE_70 + "; -fx-font-size: 12px;");
        Label usernameLabel = new Label(username + "!");
        usernameLabel.setStyle("-fx-text-fill: " + WHITE + "; -fx-font-weight: bold; -fx-font-size: 15px;");
        VBox greetingBox = new VBox(1, helloLabel, usernameLabel);
        HBox profileBox  = new HBox(12, avatar, greetingBox);
        profileBox.setAlignment(Pos.CENTER_LEFT);
        
        // Header: Nama Fitur + Periode (kanan)
        Label namafitur = new Label("MyTabungan");
        namafitur.setStyle(
            "-fx-background-color: " + FIRST_OF_SPRING + ";" +
            "-fx-text-fill: " + MIDNIGHT_MIRAGE + "; -fx-font-size: 13px; -fx-font-weight: bold;" +
            "-fx-padding: 6 14 6 14; -fx-background-radius: 20;"
        );
        Label periodeLabel = new Label("Periode " + periode);
        periodeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: "+ WHITE_70 +";");

        VBox fiturBox = new VBox(3, namafitur, periodeLabel);
        fiturBox.setAlignment(Pos.CENTER);

        Region spacerHeader = new Region();
        HBox.setHgrow(spacerHeader, Priority.ALWAYS);

        HBox headerBox = new HBox(profileBox, spacerHeader, fiturBox);
        headerBox.setAlignment(Pos.CENTER);

        // Target Bulanan + button "Ubah Target"
        double target = tabungan.getTargetAmount();
        double terkumpul = tabungan.getSavedAmount();
        // double pct = tabungan.getProgressPercentage() / 100;
        // int pctInt = (int) Math.round(pct * 100);

        double rasioMurni = (target > 0) ? (terkumpul / target) : 0.0;
        int pctInt = (int) Math.round(rasioMurni * 100);

        Label targetLabel  = new Label("Target Bulanan");
        targetLabel.setStyle("-fx-text-fill: " + WHITE_70 + "; -fx-font-size: 12px;");
        Label targetAmount = new Label(formatRupiah(target));
        targetAmount.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + WHITE + ";");
        VBox targetInfoBox = new VBox(2, targetLabel, targetAmount);

        Button ubahTargetBtn = new Button("Ubah Target");
        ubahTargetBtn.setStyle(btnLime());
        ubahTargetBtn.setOnMouseEntered(e -> ubahTargetBtn.setStyle(btnLimeHover()));
        ubahTargetBtn.setOnMouseExited(e  -> ubahTargetBtn.setStyle(btnLime()));
        ubahTargetBtn.setOnAction(e -> showUbahTargetDialog(tabungan.getId(), targetAmount, target, savingDAO));

        Region spacerTarget = new Region();
        HBox.setHgrow(spacerTarget, Priority.ALWAYS);

        HBox targetRow = new HBox(targetInfoBox, spacerTarget, ubahTargetBtn);
        targetRow.setAlignment(Pos.CENTER_LEFT);

        // Progress Bar
        HBox barBg = new HBox();
        barBg.setMaxWidth(Double.MAX_VALUE);
        barBg.setPrefHeight(12);
        barBg.setStyle("-fx-background-color: " + WHITE_40+ "; -fx-background-radius: 8;");
        
        HBox barFill = new HBox();
        barFill.setPrefHeight(12);
        barFill.setPrefWidth(0);
        barFill.setMaxWidth(Region.USE_PREF_SIZE);
        barFill.setStyle("-fx-background-color: " + FIRST_OF_SPRING + "; -fx-background-radius: 8;");
        barBg.widthProperty().addListener((obs, oldW, newW) ->{
            double lebarBarHijau = newW.doubleValue() * Math.min(1.0, rasioMurni);
            barFill.setPrefWidth(lebarBarHijau);
        }
    );
    
    StackPane progressBarPane = new StackPane(barBg, barFill);
    progressBarPane.setMinHeight(12);
    progressBarPane.setPrefHeight(12);
        StackPane.setAlignment(barFill, Pos.CENTER_LEFT);
        // barFill.setManaged(false);
        HBox.setHgrow(progressBarPane, Priority.ALWAYS);

        Label pctLabel = new Label("Sudah terkumpul " + pctInt + "%");
        pctLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + WHITE_70 + ";");

        Label statusLabel = new Label(
            tabungan.isReached() ? "Status: Target Tercapai" : "Status: Masih Menabung"
        );
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + WHITE_70 + "; -fx-font-style: italic;");

        // Stat cards (3 kartu)
        double totalTabungan = new DepositDAO().getTotalDepositByUser(userId);
        VBox cardTerkumpul = makeStatCard("Terkumpul",                  formatRupiah(terkumpul),              false);
        VBox cardSisa      = makeStatCard("Sisa Target",                formatRupiah(tabungan.getRemaining()), false);
        VBox cardTotal     = makeStatCard("Total Tabungan Keseluruhan", formatRupiah(totalTabungan),           false);
        HBox metricRow = new HBox(10, cardTerkumpul, cardSisa, cardTotal);
        HBox.setHgrow(cardTerkumpul, Priority.ALWAYS); cardTerkumpul.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(cardSisa,      Priority.ALWAYS); cardSisa.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(cardTotal,     Priority.ALWAYS); cardTotal.setMaxWidth(Double.MAX_VALUE);

        VBox progressCard = new VBox(10, targetRow, progressBarPane, pctLabel, statusLabel, metricRow);
        progressCard.setStyle(
            "-fx-background-color: " + NAVY_SURFACE + ";" +
            "-fx-background-radius: 16; -fx-padding: 18 20 18 20;"
        );

        // Form setor tabungan
        Label setorLabel = new Label("Setor Tabungan");
        setorLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + WHITE + ";");

        TextField nominalField = new TextField();
        nominalField.setPromptText("Nominal (Rp)");
        nominalField.setStyle(
            "-fx-background-color: " + NAVY_CARD + ";" +
            "-fx-text-fill: " + WHITE + "; -fx-prompt-text-fill: " + WHITE_40 + ";" +
            "-fx-font-size: 13px; -fx-padding: 9 14;" +
            "-fx-background-radius: 8; -fx-border-color: transparent; -fx-pref-height: 38;"
        );

        Button tambahBtn = new Button("+ Tambah");
        tambahBtn.setStyle(btnLime());
        tambahBtn.setOnMouseEntered(e -> tambahBtn.setStyle(btnLimeHover()));
        tambahBtn.setOnMouseExited(e  -> tambahBtn.setStyle(btnLime()));

        Label  msgLabel = new Label();
        msgLabel.setWrapText(true);
        msgLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #FF6B6B;");
        // === Action Setor Tabungan ===
        tambahBtn.setOnAction(e -> {
            try {
                if (tabungan.isReached()) {
                    msgLabel.setText("Target tabungan kamu sudah tercapai!");
                    return;
                }

                double nominal = Double.parseDouble(nominalField.getText().trim());
                if (nominal < 1000) {
                    msgLabel.setText("Minimal Rp1.000");
                    return;
                }

                double sisa = tabungan.getTargetAmount() - tabungan.getSavedAmount();
                if (nominal > sisa) {
                    msgLabel.setText("Maksimal setor Rp" + formatRupiah(sisa));
                    return;
                }

                boolean updateSaving = savingDAO.updateSavingAmount(tabungan.getId(), nominal);
                boolean saveDeposit = depositDAO.addDeposit(new Deposit( 0, userId,
                    "MAIN_SAVING", tabungan.getId(), nominal, null));
                    
                if (updateSaving && saveDeposit) {
                    msgLabel.setText("Berhasil menabung!");
                    wishlistDAO.allocateDepositToWishlists(userId, nominal);
                    nominalField.clear();
                    MainScene.refresh();
                } else {
                    msgLabel.setText("Gagal menyimpan.");
                }

            } catch (NumberFormatException ex) {
                msgLabel.setText("Masukkan angka yang valid.");
            }
        });
        HBox setorInputRow = new HBox(10, nominalField, tambahBtn);
        HBox.setHgrow(nominalField, Priority.ALWAYS);
        setorInputRow.setAlignment(Pos.CENTER_LEFT);

        // Wishlist info
        Label wishlistTitle = new Label("Wishlist Aktif");
        wishlistTitle.setStyle(
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + WHITE + ";"
        );
        VBox wishlistList = new VBox(8);

        // VBox wishlistCard;
        if (wishlists.isEmpty()){
            wishlistList.getChildren().add(
                buildWishlistCard(
            "Tidak ditemukan",
            0,
            0
        )
    );
        }else {
            for (Wishlist w : wishlists){

                VBox card = buildWishlistCard(
                    w.getTitle(),
                    w.getMaxLimit(),
                    w.calculateMonthlyLimit(tabungan)
                );
                wishlistList.getChildren().add(card);
            }
        }
        
        ScrollPane wishlistScroll = new ScrollPane(wishlistList);
        wishlistScroll.setFitToWidth(true);
        wishlistScroll.setPrefHeight(150);

        wishlistScroll.setStyle(
            "-fx-background: transparent;" +
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;"
        );

        wishlistList.setPadding(new Insets(12));
        wishlistList.setStyle(
            "-fx-background-color: " + NAVY_CARD + ";" +
            "-fx-background-radius: 12;"
        );
        // wishlistScroll.setFitToWidth(true);
        // wishlistScroll.setPrefHeight(150);
        
        VBox wishlistCard = new VBox(8, wishlistTitle, wishlistScroll);
        wishlistCard.setFillWidth(true);

        // Left Side
        VBox leftBottom = new VBox(8, setorLabel, setorInputRow, msgLabel, wishlistCard);
        leftBottom.setPrefWidth(330);
        leftBottom.setStyle(
            "-fx-background-color: " + NAVY_SURFACE + ";" +
            "-fx-background-radius: 16; -fx-padding: 18 20 18 20;"
        );

        // Riwayat Deposit dari DB (Right Side)
        Label riwayatLabel = new Label("Riwayat Deposit");
        riwayatLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + WHITE + ";");

        DateTimeFormatter tglFormatter =
            DateTimeFormatter.ofPattern("d MMM yyyy", new Locale("id", "ID"));
            VBox depositList = new VBox(4);
            depositList.setStyle("-fx-background-color: transparent;");
            
        if (deposits.isEmpty()) {
            Label emptyLbl = new Label("Belum ada deposit bulan ini.");
            emptyLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + WHITE_40 + ";");
            depositList.getChildren().add(emptyLbl);
        } else {
            for (Deposit d : deposits) {
                Label amountLbl = new Label("+" + formatRupiah(d.getAmount()));
                amountLbl.setStyle("-fx-text-fill: " + FIRST_OF_SPRING + "; -fx-font-weight: bold; -fx-font-size: 13px;");

                Label dateLbl = new Label(d.getCreatedAt().format(tglFormatter));
                dateLbl.setStyle(
                    "-fx-text-fill: " + WHITE_70 + "; -fx-font-size: 11px;" +
                    "-fx-background-color: " + NAVY_SURFACE + ";" +
                    "-fx-background-radius: 6; -fx-padding: 3 8;"
                );

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                HBox row = new HBox(amountLbl, spacer, dateLbl);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle(
                    "-fx-background-color: " + NAVY_CARD + ";" +
                    "-fx-background-radius: 8; -fx-padding: 9 12;"
                );
                depositList.getChildren().addAll(row);
            }
        }

        // Scroll Bar Riwayat Deposit
        ScrollPane riwayatScroll = new ScrollPane(depositList);
        riwayatScroll.setFitToWidth(true);
        riwayatScroll.setPrefHeight(200);
        riwayatScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        riwayatScroll.setStyle(
            "-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;"
        );

        VBox.setVgrow(riwayatScroll, Priority.ALWAYS);

        VBox rightBottom = new VBox(12, riwayatLabel, riwayatScroll);
        rightBottom.setStyle(
            "-fx-background-color: " + NAVY_SURFACE + ";" +
            "-fx-background-radius: 16; -fx-padding: 18 20 18 20;"
        );
        HBox.setHgrow(rightBottom, Priority.ALWAYS);
 
        HBox bottomRow = new HBox(12, leftBottom, rightBottom);
        VBox.setVgrow(bottomRow, Priority.ALWAYS);
 
        page.getChildren().addAll(headerBox, progressCard, bottomRow);
        return page;
    }

    private static VBox buildWishlistCard(String wishlistAktif, double alokasi, double alokasiAmt) {
    Label wishName = new Label(wishlistAktif);
    wishName.setStyle(
        "-fx-font-size: 13px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: " + WHITE + ";"
    );

    Label alokasiLabel = new Label(
        "Alokasi: " + (int) alokasi + "%"
    );
    alokasiLabel.setStyle(
        "-fx-font-size: 12px;" +
        "-fx-text-fill: " + WHITE_70 + ";"
    );

    Label estimasiLabel = new Label(
        "Estimasi: " + formatRupiah(alokasiAmt)
    );
    estimasiLabel.setStyle(
        "-fx-font-size: 12px;" +
        "-fx-text-fill: " + WHITE + ";"
    );

    VBox item = new VBox(
        3,
        wishName,
        alokasiLabel,
        estimasiLabel
    );
    item.setPadding(new Insets(0,0,8,0));

    

    return item;
    }
 
    // Stat card
    private static VBox makeStatCard(String label, String value, boolean limeAccent) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + WHITE_70 + ";");
        Label val = new Label(value);
        val.setStyle(
            "-fx-font-size: 17px; -fx-font-weight: bold;" +
            "-fx-text-fill: " + (limeAccent ? FIRST_OF_SPRING : WHITE) + ";"
        );
        VBox card = new VBox(4, lbl, val);
        card.setStyle(
            "-fx-background-color: " + NAVY_CARD + ";" +
            "-fx-background-radius: 10; -fx-padding: 12 14;"
        );
        return card;
    }

    // Button style helpers
    private static String btnLime() {
        return "-fx-background-color: " + FIRST_OF_SPRING + ";" +
                "-fx-text-fill: " + MIDNIGHT_MIRAGE + ";" +
                "-fx-font-size: 13px; -fx-font-weight: bold;" +
                "-fx-padding: 9 18; -fx-background-radius: 8;" +
                "-fx-cursor: hand; -fx-pref-height: 38;";
        }
    private static String btnLimeHover() {
        return "-fx-background-color: " + SPRING_DARK + ";" +
                "-fx-text-fill: " + MIDNIGHT_MIRAGE + ";" +
                "-fx-font-size: 13px; -fx-font-weight: bold;" +
                "-fx-padding: 9 18; -fx-background-radius: 8;" +
                "-fx-cursor: hand; -fx-pref-height: 38;";
    }
 

    //  Dialog methods 
    private static void showUbahTargetDialog(int savingId, Label targetDisplay, double current, SavingDAO dao) {
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("");
        dialog.getDialogPane().setMinWidth(360);
        dialog.getDialogPane().setMinHeight(280);
        dialog.getDialogPane().setPrefWidth(380);

        dialog.getDialogPane().setStyle(
            "-fx-background-color: " + NAVY_SURFACE + ";" +
            "-fx-background-radius: 16;" +
            "-fx-border-radius: 16;" +
            "-fx-padding: 0;"
        );

        // dialog.getDialogPane().setHeader(null);
        // dialog.getDialogPane().setGraphic(null);

        Label titleLbl = new Label("Ubah Target");
        titleLbl.setStyle(
            "-fx-background-color: " + FIRST_OF_SPRING + ";" +
            "-fx-text-fill: " + MIDNIGHT_MIRAGE + ";" +
            "-fx-font-size: 14px; -fx-font-weight: bold;" +
            "-fx-padding: 8 20; -fx-background-radius: 8;"
        );

        VBox.setMargin(titleLbl, new Insets(10,0,0,0));

        Label subtitleLbl = new Label("Target saat ini: " + formatRupiah(current));
        subtitleLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + WHITE_70 + ";");

        // input 
        Label inputLabel = new Label("Masukkan target baru:");
        inputLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + WHITE_70 + ";");

        TextField inputField = new TextField();
        inputField.setPromptText("Nominal (Rp)");
        inputField.setStyle(
            "-fx-background-color: " + NAVY_CARD + ";" +
            "-fx-text-fill: " + WHITE + ";" +
            "-fx-prompt-text-fill: " + WHITE_40 + ";" +
            "-fx-font-size: 13px; -fx-padding: 10 14;" +
            "-fx-background-radius: 8; -fx-border-color: transparent;" +
            "-fx-pref-height: 40;"
        );

        inputField.setMaxWidth(Double.MAX_VALUE);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 12px;");
        errorLabel.setWrapText(true);

        dialog.getDialogPane().setHeader(null);
        dialog.getDialogPane().setGraphic(null);
        VBox content = new VBox(10, titleLbl, subtitleLbl, inputLabel, inputField, errorLabel);
        content.setStyle("-fx-padding: 0 20 10 20;");
        dialog.getDialogPane().setContent(content);

        ButtonType cancelType = new ButtonType("Keluar",  ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType saveType   = new ButtonType("Simpan", ButtonBar.ButtonData.NEXT_FORWARD);
        dialog.getDialogPane().getButtonTypes().addAll(cancelType, saveType);
        Button simpanBtn = (Button) dialog.getDialogPane().lookupButton(saveType);
        Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(cancelType);

        simpanBtn.setMinWidth(100);
        cancelBtn.setMinWidth(100);
        simpanBtn.setStyle(
            "-fx-background-color: " + FIRST_OF_SPRING + ";" +
            "-fx-text-fill: " + MIDNIGHT_MIRAGE + ";" +
            "-fx-font-size: 13px; -fx-font-weight: bold;" +
            "-fx-padding: 10 28; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        simpanBtn.setOnMouseEntered(e -> simpanBtn.setStyle(
            "-fx-background-color: " + SPRING_DARK + ";" +
            "-fx-text-fill: " + MIDNIGHT_MIRAGE + ";" +
            "-fx-font-size: 13px; -fx-font-weight: bold;" +
            "-fx-padding: 10 28; -fx-background-radius: 8; -fx-cursor: hand;"
        ));
        simpanBtn.setOnMouseExited(e -> simpanBtn.setStyle(
            "-fx-background-color: " + FIRST_OF_SPRING + ";" +
            "-fx-text-fill: " + MIDNIGHT_MIRAGE + ";" +
            "-fx-font-size: 13px; -fx-font-weight: bold;" +
            "-fx-padding: 10 28; -fx-background-radius: 8; -fx-cursor: hand;"
        ));

        cancelBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + WHITE + ";" +
            "-fx-font-size: 13px; -fx-font-weight: bold;" +
            "-fx-padding: 10 28; -fx-background-radius: 8;" +
            "-fx-border-color: " + WHITE_40 + "; -fx-border-radius: 8; -fx-cursor: hand;"
        );
        cancelBtn.setOnMouseEntered(e -> cancelBtn.setStyle(
            "-fx-background-color: " + NAVY_CARD + ";" +
            "-fx-text-fill: " + WHITE + ";" +
            "-fx-font-size: 13px; -fx-font-weight: bold;" +
            "-fx-padding: 10 28; -fx-background-radius: 8;" +
            "-fx-border-color: " + WHITE_40 + "; -fx-border-radius: 8; -fx-cursor: hand;"
        ));
        cancelBtn.setOnMouseExited(e -> cancelBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + WHITE + ";" +
            "-fx-font-size: 13px; -fx-font-weight: bold;" +
            "-fx-padding: 10 28; -fx-background-radius: 8;" +
            "-fx-border-color: " + WHITE_40 + "; -fx-border-radius: 8; -fx-cursor: hand;"
        ));

        dialog.getDialogPane().lookup(".button-bar").setStyle(
            "-fx-background-color: " + NAVY_SURFACE + ";" +
            "-fx-padding: 10 20 20 20;"
        );

        cancelBtn.addEventFilter(ActionEvent.ACTION, e -> {
            dialog.close();
        });
        simpanBtn.disableProperty().bind(inputField.textProperty().isEmpty());

        simpanBtn.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                double newTarget = Double.parseDouble(inputField.getText().trim());
                if (newTarget < 10000) {
                    errorLabel.setText("Target minimal Rp10.000");
                    event.consume();
                    return;
                }
                boolean ok = dao.updateTargetAmount(savingId, newTarget);
                if (ok) {
                    MainScene.refresh();
                } else {
                    errorLabel.setText("Gagal menyimpan, coba lagi.");
                    event.consume();
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("Masukkan angka yang valid.");
                event.consume();
            }
        });

        dialog.setResultConverter(btn -> null);
        dialog.showAndWait();
    }


    // === Action Form Field Create a Saving ===
    // Kolom formulir tentukan target tabungan yang muncul setelah user login dan terdeteksi belum memiliki tabungan
    public static MonthlySaving showCreateSavingDialog(int userId, SavingDAO dao) {
        Dialog<MonthlySaving> dialog = new Dialog<>();
        dialog.setTitle("Mari mulai Menabung!");
        dialog.setHeaderText("Tentukan target bulan ini");
        dialog.getDialogPane().getStylesheets().add(
            Sidebar.class.getResource("/style.css").toExternalForm()
        );
        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(e -> e.consume()); // agar tidak bisa ditutup

        TextField inputField = new TextField();
        inputField.setPromptText("Masukkan target tabungan (Rp)");
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        VBox content = new VBox(10, new Label("Berapa target tabunganmu bulan ini?"),
            inputField, errorLabel
        );
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        ButtonType simpanType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(simpanType);
        Button simpanBtn = (Button) dialog.getDialogPane().lookupButton(simpanType);
        simpanBtn.getStyleClass().add("buttonOK");
        simpanBtn.disableProperty().bind(inputField.textProperty().isEmpty());

        MonthlySaving[] resultHolder = {null};
        // Action triggered
        simpanBtn.setOnAction(event -> {
            try {
                double target = Double.parseDouble(inputField.getText().trim());
                if (target <= 0) {
                    errorLabel.setText("Target harus lebih dari 0.");
                    return;
                }
                String periode = YearMonth.now().format(
                    DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("id", "ID"))
                );

                MonthlySaving newSaving = new MonthlySaving(0, userId, target,
                0, periode, LocalDateTime.now());
                boolean created = dao.createSaving(newSaving);
                if (created) {
                    resultHolder[0] = newSaving;
                    dialog.setResult(newSaving);
                    dialog.close();
                } else {
                    errorLabel.setText("Gagal menyimpan.");
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("Masukkan angka yang valid.");
            }
        });

        dialog.setResultConverter(btn -> resultHolder[0]);
        dialog.showAndWait();
        return resultHolder[0];
    }
}