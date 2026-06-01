package mytabungan.scenes;

import java.text.NumberFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import mytabungan.dao.*;
import mytabungan.models.*;
import mytabungan.utils.SessionManager;

public class TabunganScene {
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
            title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
            Label desc = new Label("Silakan tentukan target tabungan bulan ini terlebih dahulu.");
            desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");

            VBox box = new VBox(8, title, desc);
            box.setAlignment(Pos.CENTER);
            box.setPadding(new Insets(20));
            VBox.setMargin(box, new Insets(115, 0, 0, 0));

            VBox wrapper = new VBox(box);
            wrapper.setAlignment(Pos.CENTER);
            wrapper.setPrefHeight(600);
            wrapper.setMinHeight(600);
            wrapper.setStyle("-fx-background-color: #F6F7ED;");
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
        VBox page = new VBox(20);
        page.setPrefWidth(750);
        page.setMinWidth(750);
        page.setMaxWidth(Double.MAX_VALUE);
        page.setPadding(new Insets(30));
        page.setStyle("-fx-background-color: #F6F7ED;");
        
        // Header: Profil + Teks Hello (kiri)
        Circle avatar = new Circle(22);
        Label helloLabel = new Label("Hello, ");
        Label usernameLabel = new Label(username + "!");
        helloLabel.setStyle("-fx-text-fill: #001F3F;");
        usernameLabel.setStyle("-fx-text-fill: #001F3F; -fx-font-weight: bold;");

        VBox greetingBox = new VBox(2, helloLabel, usernameLabel);
        greetingBox.setAlignment(Pos.CENTER_LEFT);
        HBox profileBox = new HBox(10, avatar, greetingBox);
        profileBox.setAlignment(Pos.CENTER_LEFT);
        
        // Header: Nama Fitur + Periode (kanan)
        Label namafitur = new Label("My Tabungan");
        Label periodeLabel = new Label("Periode " + periode);
        periodeLabel.setStyle("-fx-font-size: 12px;");

        VBox fiturBox = new VBox(2, namafitur, periodeLabel);
        fiturBox.setAlignment(Pos.CENTER_RIGHT);

        Region spacerHeader = new Region();
        HBox.setHgrow(spacerHeader, Priority.ALWAYS);

        HBox headerBox = new HBox(profileBox, spacerHeader, fiturBox);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // Target Bulanan + button "Ubah Target"
        double target = tabungan.getTargetAmount();
        Label targetLabel  = new Label("Target Bulanan");
        Label targetAmount = new Label(formatRupiah(target));
        targetAmount.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        VBox targetInfoBox = new VBox(4, targetLabel, targetAmount);

        Button ubahTargetBtn = new Button("Ubah Target");
        ubahTargetBtn.setOnAction(e -> showUbahTargetDialog(tabungan.getId(), targetAmount, target, savingDAO));

        Region spacerTarget = new Region();
        HBox.setHgrow(spacerTarget, Priority.ALWAYS);

        HBox targetRow = new HBox(targetInfoBox, spacerTarget, ubahTargetBtn);
        targetRow.setAlignment(Pos.CENTER_LEFT);

        // Progress Bar
        double pct = tabungan.getProgressPercentage() / 100;
        Label terkumpulLabel = new Label("Sudah terkumpul");
        Label pctLabel = new Label(Math.round(pct * 100) + "%");

        Region spacerP = new Region();
        HBox.setHgrow(spacerP, Priority.ALWAYS);

        HBox progLabelRow = new HBox(terkumpulLabel, spacerP, pctLabel);
        ProgressBar progressBar = new ProgressBar(pct);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(20);
        progressBar.setMinHeight(15);

        // Status
        Label statusLabel = new Label();
        if (tabungan.isReached()) {
            statusLabel.setText("Status: Target Tercapai");
        } else {
            statusLabel.setText("Status: Masih Menabung");
        }
        VBox progressSection = new VBox(4, progLabelRow, progressBar, statusLabel);

        double terkumpul = tabungan.getSavedAmount();
        double totalTabungan = new DepositDAO().getTotalDepositByUser(userId);
        
        VBox cardTerkumpul = makeMetricCard("Terkumpul", formatRupiah(terkumpul));
        VBox cardSisa = makeMetricCard("Sisa Target", formatRupiah(tabungan.getRemaining()));
        VBox cardTotal = makeMetricCard("Total Tabungan Keseluruhan", formatRupiah(totalTabungan));

        HBox metricRow = new HBox(12, cardTerkumpul, cardSisa, cardTotal);
        HBox.setHgrow(cardTerkumpul, Priority.ALWAYS);
        HBox.setHgrow(cardSisa, Priority.ALWAYS);
        HBox.setHgrow(cardTotal, Priority.ALWAYS);

        // Form setor tabungan
        Label setorLabel = new Label("Setor Tabungan");
        setorLabel.setStyle("-fx-font-weight: bold;");

        TextField nominalField = new TextField();
        nominalField.setPromptText("Nominal (Rp)");

        Button tambahBtn = new Button("+ Tambah");
        Label  msgLabel = new Label();

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
                    nominalField.clear();
                    MainScene.refresh();
                } else {
                    msgLabel.setText("Gagal menyimpan.");
                }

            } catch (NumberFormatException ex) {
                msgLabel.setText("Masukkan angka yang valid.");
            }
        });
        HBox setorInputRow = new HBox(8, nominalField, tambahBtn);

        // Wishlist info
        Label wishlistTitle = new Label("Wishlist Aktif");
        wishlistTitle.setStyle("-fx-font-weight: bold;");
        VBox wishlistList = new VBox(8);

        if (wishlists.isEmpty()) {
            wishlistList.getChildren().add(new Label("Belum ada wishlist."));
        } else {
            for (Wishlist w : wishlists) {
                double estimasi = w.calculateMonthlyLimit(tabungan);
                Label namaLabel = new Label(w.getTitle());
                namaLabel.setStyle("-fx-font-weight: bold;");
                Label alokasiLabel = new Label("Alokasi: " + (int) w.getMaxLimit() + "%");
                Label estimasiLabel = new Label("Estimasi bulan ini: " + formatRupiah(estimasi));

                VBox card = new VBox( 2, namaLabel, alokasiLabel, estimasiLabel);
                card.setPadding(new Insets(6));
                wishlistList.getChildren().add(card);
            }
        }
        ScrollPane wishlistScroll = new ScrollPane(wishlistList);
        wishlistScroll.setFitToWidth(true);
        wishlistScroll.setPrefHeight(180);

        VBox wishlistCard = new VBox(8, wishlistTitle, wishlistScroll);

        // Left Side
        VBox leftBottom = new VBox(12, setorLabel, setorInputRow, msgLabel, wishlistCard);
        leftBottom.setPrefWidth(280);
        wishlistCard.setPadding(new Insets(10));

        // Riwayat Deposit dari DB (Right Side)
        Label riwayatLabel = new Label("Riwayat Deposit");
        riwayatLabel.setStyle("-fx-font-weight: bold;");

        DateTimeFormatter tglFormatter =
            DateTimeFormatter.ofPattern("d MMM yyyy", new Locale("id", "ID"));

        VBox depositList = new VBox(0);
        if (deposits.isEmpty()) {
            depositList.getChildren().add(new Label("Belum ada deposit bulan ini."));
        } else {
            for (Deposit d : deposits) {
                Label amountLbl = new Label("+" + formatRupiah(d.getAmount()));
                amountLbl.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

                Label dateLbl = new Label(d.getCreatedAt().format(tglFormatter));
                dateLbl.setStyle("-fx-font-size: 12px;");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                HBox row = new HBox(amountLbl, spacer, dateLbl);
                row.setPadding(new Insets(8, 0, 8, 0));
                row.setAlignment(Pos.CENTER_LEFT);
                depositList.getChildren().addAll(row, new Separator());
            }
        }

        // Scroll Bar Riwayat Deposit
        ScrollPane riwayatScroll = new ScrollPane(depositList);
        riwayatScroll.setFitToWidth(true);
        riwayatScroll.setPrefHeight(200);
        riwayatScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox rightBottom = new VBox(10, riwayatLabel, riwayatScroll);
        HBox.setHgrow(rightBottom, Priority.ALWAYS);
        HBox bottomRow = new HBox(16, leftBottom, rightBottom);

        page.getChildren().addAll(headerBox, new Separator(), targetRow,
            progressSection, metricRow, bottomRow
        );
        return page;
    }

    // === Card Box ===
    private static VBox makeMetricCard(String label, String value) {
        Label lbl = new Label(label);
        Label val = new Label(value);
        val.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        VBox card = new VBox(4, lbl, val);
        card.setPadding(new Insets(12));
        return card;
    }

    // === Tag Box ===
    private static HBox makeTagBox(Label content) {
        HBox box = new HBox(content);
        box.setPadding(new Insets(2, 10, 2, 10));
        return box;
    }

    // === Action Form Field from Button "Ubah Target" ===
    private static void showUbahTargetDialog(int savingId, Label targetDisplay, double current, SavingDAO dao) {
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Ubah Target Bulanan");
        dialog.setHeaderText("Target saat ini: " + formatRupiah(current));

        TextField inputField = new TextField();
        inputField.setPromptText("Target baru (Rp)");
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        VBox content = new VBox(8, new Label("Masukkan target baru:"), inputField, errorLabel);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getStylesheets().add(
            TabunganScene.class.getResource("/style.css").toExternalForm()
        );

        ButtonType saveType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelType = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, cancelType);

        Button simpanBtn = (Button) dialog.getDialogPane().lookupButton(saveType);
        Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(cancelType);
        simpanBtn.getStyleClass().add("buttonOK");
        cancelBtn.getStyleClass().add("buttonCancel");

        simpanBtn.disableProperty().bind(inputField.textProperty().isEmpty());
        // Action triggered
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
                    // targetDisplay.setText(formatRupiah(newTarget));
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