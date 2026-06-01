package mytabungan.scenes;

import java.text.NumberFormat;
import java.time.LocalDate;
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

public class WishlistScene {
    private static final double MAX_TOTAL_ALOKASI = 70.0; // maks 70% total semua wishlist
    private static final NumberFormat RUP = NumberFormat.getInstance(new Locale("id", "ID"));

    private static String formatRupiah(double amount) {
        return "Rp" + RUP.format(amount);
    }

    public static VBox buildPage() {
        int userId             = SessionManager.getCurrentUserId();
        String username        = SessionManager.getCurrentUser().getUsername();
        WishlistDAO wishlistDAO = new WishlistDAO();
        SavingDAO savingDAO    = new SavingDAO();

        // Data dari DB
        List<Wishlist> wishlistsAktif    = wishlistDAO.getWishlistsByUserId(userId); // status ONGOING
        List<Wishlist> wishlistsTercapai = wishlistDAO.getReachedWishlistsByUserId(userId); // status REACHED
        MonthlySaving  tabungan          = savingDAO.getSavingByUserId(userId);

        double terkumpul         = tabungan != null ? tabungan.getSavedAmount()  : 0;
        // double targetBulanan     = tabungan != null ? tabungan.getTargetAmount() : 0;
        double totalAlokasiPct   = wishlistDAO.getTotalMaxLimitByUserId(userId);
        double sisaKapasitasPct  = Math.max(0, MAX_TOTAL_ALOKASI - totalAlokasiPct);
        double alokasiAmt        = terkumpul * totalAlokasiPct / 100;

        // Format periode
        String periodeStr = LocalDate.now()
        .format(DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID")));

        // === Root Page ===
        VBox page = new VBox(18);
        page.setPadding(new Insets(28, 32, 28, 32));
        page.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        page.setStyle("-fx-background-color: #0A2D5A;");

        // === Header ===
        Circle avatar = new Circle(22);
        Label helloLabel    = new Label("Hello,");
        Label usernameLabel = new Label(username + "!");
        helloLabel.setStyle("-fx-text-fill: #001F3F;");
        usernameLabel.setStyle("-fx-text-fill: #001F3F; -fx-font-weight: bold;");
        VBox greetingBox = new VBox(2, helloLabel, usernameLabel);
        greetingBox.setAlignment(Pos.CENTER_LEFT);
        HBox profileBox = new HBox(10, avatar, greetingBox);
        profileBox.setAlignment(Pos.CENTER_LEFT);

        Label namaFitur    = new Label("My Wishlist");
        Label tanggalLabel = new Label(periodeStr);
        tanggalLabel.setStyle("-fx-font-size: 12px;");
        VBox fiturBox = new VBox(2, namaFitur, tanggalLabel);
        fiturBox.setAlignment(Pos.CENTER_RIGHT);

        Region spacerH = new Region();
        HBox.setHgrow(spacerH, Priority.ALWAYS);
        HBox headerBox = new HBox(10, profileBox, spacerH, fiturBox);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // === Metric Card ===
        // Total wishlist
        long jumlahBerjalan = wishlistsAktif.size();
        long jumlahTercapai = wishlistsTercapai.size();
        VBox cardTotal = makeMetricCard(
            "Total Wishlist",
            (wishlistsAktif.size() + wishlistsTercapai.size()) + " item",
            jumlahBerjalan + " sedang berjalan"
        );

        // Alokasi Bulanan
        VBox cardAlokasi = makeMetricCard(
            "Alokasi Bulanan",
            formatRupiah(alokasiAmt),
            (int) totalAlokasiPct + "% dari tabungan"
        );

        // Wishlist tercapai bulan ini
        String namaWishlistTercapaiBulanIni = wishlistsTercapai.isEmpty()
            ? "—" : wishlistsTercapai.get(0).getTitle();
        VBox cardTercapai = makeMetricCard(
            "Tercapai bulan ini",
            jumlahTercapai + " wishlist",
            namaWishlistTercapaiBulanIni
        );

        HBox metricRow = new HBox(12, cardTotal, cardAlokasi, cardTercapai);
        HBox.setHgrow(cardTotal,   Priority.ALWAYS);
        HBox.setHgrow(cardAlokasi, Priority.ALWAYS);
        HBox.setHgrow(cardTercapai, Priority.ALWAYS);

        // Wishlist Aktif (kiri)
        Label wishlistAktifLabel = new Label("Wishlist Aktif");
        wishlistAktifLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Sisa Kapasitas Alokasi
        Label sisaLabel = new Label(
            "Sisa kapasitas alokasi: " + (int) sisaKapasitasPct + "% (maks. 70%)");
        sisaLabel.setStyle(sisaKapasitasPct <= 0
            ? "-fx-font-size: 12px; -fx-text-fill: red;"
            : "-fx-font-size: 12px; -fx-text-fill: #555;");

        // Tombol Tambah Wishlist
        Button tambahBtn = new Button("+ Tambah Wishlist");
        tambahBtn.setDisable(sisaKapasitasPct <= 0); // disable kalau sudah maksimalnya
        tambahBtn.setOnAction(e ->
            showTambahWishlistDialog(userId, sisaKapasitasPct, terkumpul, wishlistDAO));

        HBox wishlistHeader = new HBox(8, wishlistAktifLabel);
        Region spacerWH = new Region();
        HBox.setHgrow(spacerWH, Priority.ALWAYS);
        wishlistHeader.getChildren().addAll(spacerWH, tambahBtn);
        wishlistHeader.setAlignment(Pos.CENTER_LEFT);

        VBox wishlistAktifList = new VBox(8);
        if (wishlistsAktif.isEmpty()) {
            wishlistAktifList.getChildren().add(new Label("Belum ada wishlist aktif."));
        } else {
            for (Wishlist w : wishlistsAktif) {
                wishlistAktifList.getChildren().add(
                    buildWishlistItemCard(w, userId, wishlistDAO, terkumpul, sisaKapasitasPct)
                );
            }
        }

        VBox leftSection = new VBox(8, wishlistHeader, sisaLabel, wishlistAktifList);
        leftSection.setPrefWidth(370);

        // === Riwayat Tercapai (kanan) ===
        Label riwayatLabel = new Label("Riwayat Tercapai");
        riwayatLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        VBox riwayatList = new VBox(8);
        if (wishlistsTercapai.isEmpty()) {
            riwayatList.getChildren().add(new Label("Belum ada wishlist yang tercapai."));
        } else {
            for (Wishlist w : wishlistsTercapai) {
                riwayatList.getChildren().add(buildRiwayatCard(w));
            }
        }

        VBox rightSection = new VBox(8, riwayatLabel, riwayatList);
        HBox.setHgrow(rightSection, Priority.ALWAYS);

        HBox mainRow = new HBox(16, leftSection, rightSection);

        // === Distribusi Alokasi ===
        Label distribusiLabel = new Label("% Distribusi Alokasi Tabungan");
        distribusiLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        VBox distribusiList = new VBox(6);
        for (Wishlist w : wishlistsAktif) {
            Label namaBar = new Label(w.getTitle());
            namaBar.setStyle("-fx-font-size: 13px;");
            namaBar.setPrefWidth(120);

            ProgressBar bar = new ProgressBar(w.getMaxLimit() / 100);
            bar.setPrefWidth(400);
            bar.setPrefHeight(12);

            Label pctBar = new Label((int) w.getMaxLimit() + "%");
            pctBar.setStyle("-fx-font-size: 12px;");

            HBox barRow = new HBox(8, namaBar, bar, pctBar);
            barRow.setAlignment(Pos.CENTER_LEFT);
            distribusiList.getChildren().add(barRow);
        }
        // Sisa (belum dialokasikan)
        if (sisaKapasitasPct > 0) {
            Label namaBar = new Label("Belum dialokasikan");
            namaBar.setStyle("-fx-font-size: 13px; -fx-text-fill: #999;");
            namaBar.setPrefWidth(120);

            ProgressBar bar = new ProgressBar((100 - totalAlokasiPct) / 100);
            bar.setPrefWidth(400);
            bar.setPrefHeight(12);

            Label pctBar = new Label((int)(100 - totalAlokasiPct) + "%");
            pctBar.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");

            HBox barRow = new HBox(8, namaBar, bar, pctBar);
            barRow.setAlignment(Pos.CENTER_LEFT);
            distribusiList.getChildren().add(barRow);
        }

        VBox distribusiSection = new VBox(8, distribusiLabel, distribusiList);

        page.getChildren().addAll(
            headerBox,
            new Separator(),
            metricRow,
            new Separator(),
            mainRow,
            new Separator(),
            distribusiSection
        );

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox wrapper = new VBox(scroll);
        wrapper.setPrefHeight(Double.MAX_VALUE);

        return wrapper;
    }

    // Card Item wishlist aktif (tombol Edit & Hapus)
    private static VBox buildWishlistItemCard(Wishlist w, int userId,
            WishlistDAO wishlistDAO, double terkumpul, double sisaKapasitasPct) {

        double pct = w.getTargetAmount() > 0
            ? Math.min(1.0, w.getSavedAmount() / w.getTargetAmount()) : 0;
        double progress = w.getSavedAmount();

        Label namaLabel = new Label(w.getTitle());
        namaLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label statusLabel = new Label(
            w.isReached() ? "Selesai" : "Berjalan"
        );

        statusLabel.setStyle(w.isReached()
                ? "-fx-text-fill: green; -fx-font-size: 11px;"
                : "-fx-text-fill: gray; -fx-font-size: 11px;"
        );

        Label alokasiLabel = new Label((int) w.getMaxLimit() + "%");
        alokasiLabel.setStyle("-fx-font-size: 12px;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        HBox namaRow = new HBox(namaLabel, sp, alokasiLabel);
        namaRow.setAlignment(Pos.CENTER_LEFT);

        ProgressBar bar = new ProgressBar(pct);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setPrefHeight(8);

        Label savedLabel  = new Label("Terkumpul " + formatRupiah(w.getSavedAmount()));
        savedLabel.setStyle("-fx-font-size: 11px;");
        Label targetLabel = new Label(formatRupiah(w.getTargetAmount()));
        targetLabel.setStyle("-fx-font-size: 11px;");
        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);
        HBox progRow = new HBox(savedLabel, sp2, targetLabel);

        // Tombol Edit & Hapus
        Button editBtn  = new Button("Edit Alokasi");
        Button hapusBtn = new Button("Hapus Item");

        editBtn.setOnAction(e ->
            showEditAlokasiDialog(w, userId, sisaKapasitasPct, wishlistDAO));
        hapusBtn.setOnAction(e ->
            showHapusDialog(w, wishlistDAO));

        HBox btnRow = new HBox(6, editBtn, hapusBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        VBox card = new VBox(5, namaRow, statusLabel, bar, progRow, btnRow);
        card.setPadding(new Insets(8));
        return card;
    }

    // === Card riwayat tercapai ===
    private static VBox buildRiwayatCard(Wishlist w) {
        Label namaLabel   = new Label(w.getTitle());
        namaLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label tercapaiTag = new Label(w.isReached() ? "✓ Tercapai" : "");
        tercapaiTag.setStyle("-fx-font-size: 11px; -fx-text-fill: green;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        HBox namaRow = new HBox(namaLabel, sp, tercapaiTag);
        namaRow.setAlignment(Pos.CENTER_LEFT);

        ProgressBar bar = new ProgressBar(1.0);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setMinHeight(8);
        bar.setPrefHeight(8);
        bar.setStyle("-fx-accent: #DBE64C;");

        Label amtLabel = new Label(
            formatRupiah(w.getSavedAmount()) + " / " + formatRupiah(w.getTargetAmount()));
        amtLabel.setStyle("-fx-font-size: 11px;");

        VBox card = new VBox(5, namaRow, bar, amtLabel);
        card.setPadding(new Insets(8));
        return card;
    }

    // Dialog: Tambah Wishlist
    private static void showTambahWishlistDialog(int userId, double sisaKapasitasPct,
        double terkumpul, WishlistDAO wishlistDAO) {

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Tambah Wishlist");
        dialog.setHeaderText("Sisa kapasitas alokasi: " + (int) sisaKapasitasPct + "%");

        TextField namaField    = new TextField();
        namaField.setPromptText("Nama wishlist");
        TextField targetField  = new TextField();
        targetField.setPromptText("Target harga (Rp)");
        TextField alokasiField = new TextField();
        alokasiField.setPromptText("Alokasi dari tabungan (%)");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        VBox content = new VBox(8,
            new Label("Nama wishlist:"),  namaField,
            new Label("Target harga:"),   targetField,
            new Label("Alokasi (%):"),    alokasiField,
            errorLabel
        );
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        ButtonType simpanType  = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        ButtonType batalType   = new ButtonType("Batal",  ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(simpanType, batalType);

        Button simpanBtn = (Button) dialog.getDialogPane().lookupButton(simpanType);
        // Disable kalau nama atau target kosong
        simpanBtn.disableProperty().bind(
            namaField.textProperty().isEmpty()
                .or(targetField.textProperty().isEmpty())
                .or(alokasiField.textProperty().isEmpty())
        );

        simpanBtn.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
            try {
                String nama     = namaField.getText().trim();
                double target   = Double.parseDouble(targetField.getText().trim());
                double alokasi  = Double.parseDouble(alokasiField.getText().trim());

                if (target < 1000) {
                    errorLabel.setText("Target minimal Rp1.000");
                    return;
                }
                if (alokasi <= 0 || alokasi > sisaKapasitasPct) {
                    errorLabel.setText("Alokasi harus 1–" + (int) sisaKapasitasPct + "%");
                    return;
                }

                String periode = java.time.YearMonth.now()
                    .format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("id", "ID")));
                Wishlist newWishlist = new Wishlist(
                    0, userId, nama, target, 0, alokasi, "ONGOING", periode,
                    java.time.LocalDateTime.now()
                );

                int newId = wishlistDAO.createWishlist(newWishlist);
                if (newId > 0) {
                    if (terkumpul > 0) {
                        double retroAmount = terkumpul * alokasi / 100;
                        wishlistDAO.addToWishlist(newId, retroAmount);
                    }
                    dialog.close();
                    MainScene.refresh();
                } else {
                    errorLabel.setText("Gagal menyimpan, coba lagi.");
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("Masukkan angka yang valid.");
            }
        });

        dialog.setResultConverter(btn -> null);
        dialog.showAndWait();
    }

    // Dialog: Edit alokasi
    private static void showEditAlokasiDialog(Wishlist w, int userId, double sisaKapasitasPct, WishlistDAO wishlistDAO) {
        double kapasitasTersedia = sisaKapasitasPct + w.getMaxLimit();

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Edit Alokasi — " + w.getTitle());
        dialog.setHeaderText("Alokasi saat ini: " + (int) w.getMaxLimit() + "%");

        TextField alokasiField = new TextField(String.valueOf((int) w.getMaxLimit()));
        alokasiField.setPromptText("Alokasi baru (%) — maks " + (int) kapasitasTersedia + "%");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        VBox content = new VBox(8,
            new Label("Alokasi baru (%):"), alokasiField, errorLabel);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        ButtonType simpanType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        ButtonType batalType  = new ButtonType("Batal",  ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(simpanType, batalType);

        Button simpanBtn = (Button) dialog.getDialogPane().lookupButton(simpanType);
        simpanBtn.disableProperty().bind(alokasiField.textProperty().isEmpty());

        simpanBtn.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
            try {
                double newAlokasi = Double.parseDouble(alokasiField.getText().trim());
                if (newAlokasi <= 0 || newAlokasi > kapasitasTersedia) {
                    errorLabel.setText("Alokasi harus 1–" + (int) kapasitasTersedia + "%");
                    return;
                }
                boolean ok = wishlistDAO.updateMaxLimit(w.getId(), newAlokasi);
                if (ok) {
                    wishlistDAO.syncWishlistAllocation(userId);
                    dialog.close();
                    MainScene.refresh();
                } else {
                    errorLabel.setText("Gagal menyimpan, coba lagi.");
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("Masukkan angka yang valid.");
            }
        });

        dialog.setResultConverter(btn -> null);
        dialog.showAndWait();
    }

    // Dialog: Hapus wishlist
    private static void showHapusDialog(Wishlist w, WishlistDAO wishlistDAO) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Hapus Wishlist");
        confirm.setHeaderText("Hapus \"" + w.getTitle() + "\"?");
        confirm.setContentText(
            "Wishlist ini akan dihapus dan alokasi " + (int) w.getMaxLimit() +
            "% akan dibebaskan kembali.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                wishlistDAO.deleteWishlist(w.getId()); // soft delete → status CANCELLED
                MainScene.refresh();
            }
        });
    }

    // metric card 3 baris
    private static VBox makeMetricCard(String title, String value, String sub) {
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 12px;");
        Label valueLbl = new Label(value);
        valueLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label subLbl = new Label(sub);
        subLbl.setStyle("-fx-font-size: 12px;");
        VBox card = new VBox(4, titleLbl, valueLbl, subLbl);
        card.setPadding(new Insets(12));
        return card;
    }
}