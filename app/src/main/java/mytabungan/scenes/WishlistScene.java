package mytabungan.scenes;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import mytabungan.dao.SavingDAO;
import mytabungan.dao.WishlistDAO;
import mytabungan.models.MonthlySaving;
import mytabungan.models.Wishlist;
import mytabungan.utils.SessionManager;

public class WishlistScene {

    // ── Colour palette (sama persis dengan TabunganScene) ──
    private static final String MIDNIGHT_MIRAGE = "#001F3F";
    private static final String NAVY_CARD       = "#0A2D5A";
    private static final String NAVY_SURFACE    = "#0E3570";
    private static final String CARD_SECTION    = "#0E3570";
    private static final String CARD_ITEM       = "#123B78";
    private static final String FIRST_OF_SPRING = "#DBE64C";
    private static final String SPRING_DARK     = "#b8c23a";
    private static final String WHITE           = "#FFFFFF";
    private static final String WHITE_70        = "rgba(255,255,255,0.70)";
    private static final String WHITE_40        = "rgba(255,255,255,0.40)";

    private static final double MAX_TOTAL_ALOKASI = 70.0;
    private static final NumberFormat RUP =
        NumberFormat.getInstance(new Locale("id", "ID"));

    private static String formatRupiah(double amount) {
        return "Rp" + RUP.format(amount);
    }

    // ══════════════════════════════════════════════
    //  BUILD PAGE
    // ══════════════════════════════════════════════
    public static VBox buildPage() {
        int    userId      = SessionManager.getCurrentUserId();
        String username    = SessionManager.getCurrentUser().getUsername();

        WishlistDAO  wishlistDAO = new WishlistDAO();
        SavingDAO    savingDAO   = new SavingDAO();

        List<Wishlist> wishlistsAktif    = wishlistDAO.getWishlistsByUserId(userId);
        List<Wishlist> wishlistsTercapai = wishlistDAO.getReachedWishlistsByUserId(userId);
        MonthlySaving  tabungan          = savingDAO.getSavingByUserId(userId);

        double terkumpul        = tabungan != null ? tabungan.getSavedAmount()  : 0;
        double totalAlokasiPct  = wishlistDAO.getTotalMaxLimitByUserId(userId);
        double sisaKapasitasPct = Math.max(0, MAX_TOTAL_ALOKASI - totalAlokasiPct);
        double alokasiAmt       = terkumpul * totalAlokasiPct / 100;

        String periodeStr = LocalDate.now()
            .format(DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID")));

        // ── Root ──
        VBox page = new VBox(10);
        page.setPadding(new Insets(28, 32, 28, 32));
        page.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        page.setStyle("-fx-background-color:" + MIDNIGHT_MIRAGE + ";");

        // ── Header ──
        StackPane avatar = new StackPane();
        avatar.setPrefSize(44, 44);
        avatar.setMinSize(44, 44);
        avatar.setMaxSize(44, 44);
        avatar.setStyle(
            "-fx-background-color:" + NAVY_SURFACE + ";" +
            "-fx-background-radius:50;"
        );
        Label avatarLbl = new Label(username.substring(0, 1).toUpperCase());
        avatarLbl.setStyle(
            "-fx-font-size:17px; -fx-font-weight:bold;" +
            "-fx-text-fill:" + FIRST_OF_SPRING + ";"
        );
        avatar.getChildren().add(avatarLbl);

        Label helloLabel    = new Label("Halo,");
        helloLabel.setStyle("-fx-text-fill:" + WHITE_70 + "; -fx-font-size:12px;");
        Label usernameLabel = new Label(username + "!");
        usernameLabel.setStyle(
            "-fx-text-fill:" + WHITE + "; -fx-font-weight:bold; -fx-font-size:15px;"
        );
        VBox greetingBox = new VBox(2, helloLabel, usernameLabel);
        greetingBox.setAlignment(Pos.CENTER_LEFT);
        HBox profileBox = new HBox(10, avatar, greetingBox);
        profileBox.setAlignment(Pos.CENTER_LEFT);

        // Badge "MyWishlist"
        Label namaFitur = new Label("MyWishlist");
        namaFitur.setStyle(
            "-fx-background-color:" + FIRST_OF_SPRING + ";" +
            "-fx-text-fill:" + MIDNIGHT_MIRAGE + ";" +
            "-fx-font-weight:bold;" +
            "-fx-padding:8 18;" +
            "-fx-background-radius:20;"
        );
        Label tanggalLabel = new Label(periodeStr);
        tanggalLabel.setStyle("-fx-font-size:12px; -fx-text-fill:" + FIRST_OF_SPRING + ";");
        VBox fiturBox = new VBox(4, namaFitur, tanggalLabel);
        fiturBox.setAlignment(Pos.CENTER_RIGHT);

        Region spacerH = new Region();
        HBox.setHgrow(spacerH, Priority.ALWAYS);
        HBox headerBox = new HBox(10, profileBox, spacerH, fiturBox);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // ── Metric Row ──
        long jumlahBerjalan  = wishlistsAktif.size();
        long jumlahTercapai  = wishlistsTercapai.size();
        long totalWishlist   = jumlahBerjalan + jumlahTercapai;

        // Kartu Tambah Wishlist
        VBox cardTambah = makeTambahCard();

        // Kartu Tercapai bulan ini
        String namaWLTercapai = wishlistsTercapai.isEmpty()
            ? "—" : wishlistsTercapai.get(0).getTitle();
        VBox cardTercapai = makeMetricCard(
            "Tercapai bulan ini",
            jumlahTercapai + " wishlist",
            namaWLTercapai
        );

        // Kartu Alokasi Bulanan
        VBox cardAlokasi = makeMetricCard(
            "Alokasi Bulanan",
            formatRupiah(alokasiAmt),
            (int) totalAlokasiPct + "% dari tabungan"
        );

        // Kartu Total Wishlist
        VBox cardTotal = makeMetricCard(
            "Total Wishlist",
            totalWishlist + " item",
            jumlahBerjalan + " sedang berjalan"
        );

        // Klik "Tambah Wishlist"
        cardTambah.setOnMouseClicked(e ->
            showTambahWishlistDialog(userId, sisaKapasitasPct, terkumpul, wishlistDAO)
        );
        cardTambah.setOnMouseEntered(e -> cardTambah.setStyle(
            "-fx-background-color:" + NAVY_SURFACE + ";" +
            "-fx-background-radius:24; -fx-cursor:hand;"
        ));
        cardTambah.setOnMouseExited(e -> cardTambah.setStyle(
            "-fx-background-color:" + CARD_SECTION + ";" +
            "-fx-background-radius:24; -fx-cursor:hand;"
        ));

        HBox metricRow = new HBox(6, cardTambah, cardTercapai, cardAlokasi, cardTotal);
        HBox.setHgrow(cardTercapai, Priority.ALWAYS);
        HBox.setHgrow(cardAlokasi,  Priority.ALWAYS);
        HBox.setHgrow(cardTotal,    Priority.ALWAYS);

        // ── Wishlist Aktif (kiri) ──
        Label wishlistAktifLabel = new Label("Wishlist Aktif");
        wishlistAktifLabel.setStyle(
            "-fx-font-weight:bold; -fx-font-size:14px; -fx-text-fill:" + WHITE + ";"
        );

        VBox wishlistAktifList = new VBox(8);
        if (wishlistsAktif.isEmpty()) {
            Label empty = new Label("Belum ada wishlist aktif.");
            empty.setStyle("-fx-text-fill:" + WHITE_70 + "; -fx-font-size:13px;");
            wishlistAktifList.getChildren().add(empty);
        } else {
            for (Wishlist w : wishlistsAktif) {
                wishlistAktifList.getChildren().add(
                    buildWishlistItemCard(w, userId, wishlistDAO, terkumpul, sisaKapasitasPct)
                );
            }
        }

        VBox leftSection = new VBox(10, wishlistAktifLabel, wishlistAktifList);
        leftSection.setPrefWidth(370);
        leftSection.setStyle(
            "-fx-background-color:" + CARD_SECTION + ";" +
            "-fx-background-radius:20;" +
            "-fx-padding:20;"
        );

        // ── Riwayat Tercapai (kanan) ──
        Label riwayatLabel = new Label("Riwayat Tercapai");
        riwayatLabel.setStyle(
            "-fx-font-weight:bold; -fx-font-size:14px; -fx-text-fill:" + WHITE + ";"
        );

        VBox riwayatList = new VBox(8);
        if (wishlistsTercapai.isEmpty()) {
            Label empty = new Label("Belum ada wishlist yang tercapai.");
            empty.setStyle("-fx-text-fill:" + WHITE_70 + "; -fx-font-size:13px;");
            riwayatList.getChildren().add(empty);
        } else {
            for (Wishlist w : wishlistsTercapai) {
                riwayatList.getChildren().add(buildRiwayatCard(w));
            }
        }

        VBox rightSection = new VBox(10, riwayatLabel, riwayatList);
        HBox.setHgrow(rightSection, Priority.ALWAYS);
        rightSection.setStyle(
            "-fx-background-color:" + CARD_SECTION + ";" +
            "-fx-background-radius:20;" +
            "-fx-padding:20;"
        );

        HBox mainRow = new HBox(8, leftSection, rightSection);

        // ── Distribusi Alokasi Tabungan ──
        Label distribusiLabel = new Label("% Distribusi Alokasi Tabungan");
        distribusiLabel.setStyle(
            "-fx-font-weight:bold; -fx-font-size:14px; -fx-text-fill:" + WHITE + ";"
        );

        VBox distribusiList = new VBox(8);
        for (Wishlist w : wishlistsAktif) {
            Label namaBar = new Label(w.getTitle());
            namaBar.setStyle("-fx-font-size:13px; -fx-text-fill:" + WHITE + ";");
            namaBar.setPrefWidth(160);
            namaBar.setMinWidth(160);

            ProgressBar bar = new ProgressBar(w.getMaxLimit() / 100.0);
            bar.setMaxWidth(Double.MAX_VALUE);
            bar.setPrefHeight(12);
            bar.setStyle("-fx-accent:" + FIRST_OF_SPRING + ";");
            HBox.setHgrow(bar, Priority.ALWAYS);

            Label pctBar = new Label((int) w.getMaxLimit() + "%");
            pctBar.setStyle("-fx-font-size:12px; -fx-text-fill:" + WHITE_70 + ";");

            HBox barRow = new HBox(10, namaBar, bar, pctBar);
            barRow.setAlignment(Pos.CENTER_LEFT);
            distribusiList.getChildren().add(barRow);
        }

        // Free savings / sisa
        double freePct = Math.max(0, 100 - totalAlokasiPct);
        if (freePct > 0) {
            Label namaBar = new Label("Free savings");
            namaBar.setStyle("-fx-font-size:13px; -fx-text-fill:" + WHITE_70 + ";");
            namaBar.setPrefWidth(160);
            namaBar.setMinWidth(160);

            ProgressBar bar = new ProgressBar(freePct / 100.0);
            bar.setMaxWidth(Double.MAX_VALUE);
            bar.setPrefHeight(12);
            bar.setStyle("-fx-accent: rgba(255,255,255,0.35);");
            HBox.setHgrow(bar, Priority.ALWAYS);

            Label pctBar = new Label((int) freePct + "%");
            pctBar.setStyle("-fx-font-size:12px; -fx-text-fill:" + WHITE_70 + ";");

            HBox barRow = new HBox(10, namaBar, bar, pctBar);
            barRow.setAlignment(Pos.CENTER_LEFT);
            distribusiList.getChildren().add(barRow);
        }

        VBox distribusiSection = new VBox(10, distribusiLabel, distribusiList);
        distribusiSection.setStyle(
            "-fx-background-color:" + CARD_SECTION + ";" +
            "-fx-background-radius:20;" +
            "-fx-padding:20;"
        );

        // ── Separator styling ──
        Separator sep1 = styledSep();
        Separator sep2 = styledSep();
        Separator sep3 = styledSep();

        page.getChildren().addAll(
            headerBox,
            sep1,
            metricRow,
            sep2,
            mainRow,
            sep3,
            distribusiSection
        );

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle(
            "-fx-background:transparent;" +
            "-fx-background-color:transparent;"
        );

        VBox wrapper = new VBox(scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        wrapper.setStyle("-fx-background-color:" + MIDNIGHT_MIRAGE + ";");
        return wrapper;
    }

    // ══════════════════════════════════════════════
    //  CARD: Tambah Wishlist (+ icon)
    // ══════════════════════════════════════════════
    private static VBox makeTambahCard() {
        Label iconLbl = new Label("+");
        iconLbl.setStyle(
            "-fx-font-size:36px; -fx-font-weight:900;" +
            "-fx-text-fill:" + FIRST_OF_SPRING + ";"
        );
        Label textLbl = new Label("Tambah\nWishlist");
        textLbl.setStyle(
            "-fx-font-size:18px; -fx-font-weight:900;" +
            "-fx-text-fill:" + FIRST_OF_SPRING + ";" +
            "-fx-text-alignment:center;"
        );
        textLbl.setAlignment(Pos.CENTER);

        VBox card = new VBox(6, iconLbl, textLbl);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(18));
        card.setPrefHeight(130);
        card.setPrefWidth(170);
        card.setStyle(
            "-fx-background-color:" + CARD_SECTION + ";" +
            "-fx-background-radius:24;" +
            "-fx-cursor:hand;"
        );
        return card;
    }

    // ══════════════════════════════════════════════
    //  CARD: Metric 3-baris
    // ══════════════════════════════════════════════
    private static VBox makeMetricCard(String title, String value, String sub) {
        Label titleLbl = new Label(title);
        titleLbl.setStyle(
            "-fx-font-size:13px; -fx-text-fill:" + WHITE_70 + ";"
        );
        Label valueLbl = new Label(value);
        valueLbl.setStyle(
            "-fx-font-size:20px; -fx-font-weight:bold;" +
            "-fx-text-fill:" + WHITE + ";"
        );
        Label subLbl = new Label(sub);
        subLbl.setStyle("-fx-font-size:12px; -fx-text-fill:" + WHITE_70 + ";");

        VBox card = new VBox(8, titleLbl, valueLbl, subLbl);
        card.setPadding(new Insets(18));
        card.setPrefHeight(130);
        card.setStyle(
            "-fx-background-color:" + CARD_SECTION + ";" +
            "-fx-background-radius:24;"
        );
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    // ══════════════════════════════════════════════
    //  CARD: Item wishlist aktif
    // ══════════════════════════════════════════════
    private static VBox buildWishlistItemCard(Wishlist w, int userId,
            WishlistDAO wishlistDAO, double terkumpul, double sisaKapasitasPct) {

        double pct = w.getTargetAmount() > 0
            ? Math.min(1.0, w.getSavedAmount() / w.getTargetAmount()) : 0;

        Label namaLabel = new Label(w.getTitle());
        namaLabel.setStyle(
            "-fx-font-weight:bold; -fx-font-size:13px;" +
            "-fx-text-fill:" + WHITE + ";"
        );
        Label alokasiLabel = new Label((int) w.getMaxLimit() + "%");
        alokasiLabel.setStyle(
            "-fx-font-size:12px; -fx-text-fill:" + FIRST_OF_SPRING + ";" +
            "-fx-font-weight:bold;"
        );
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        HBox namaRow = new HBox(namaLabel, sp, alokasiLabel);
        namaRow.setAlignment(Pos.CENTER_LEFT);

        ProgressBar bar = new ProgressBar(pct);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setPrefHeight(10);
        bar.setStyle("-fx-accent:" + FIRST_OF_SPRING + ";");

        Label savedLabel  = new Label("Terkumpul " + formatRupiah(w.getSavedAmount()));
        savedLabel.setStyle("-fx-font-size:11px; -fx-text-fill:" + WHITE_70 + ";");
        Label targetLabel = new Label(formatRupiah(w.getTargetAmount()));
        targetLabel.setStyle("-fx-font-size:11px; -fx-text-fill:" + WHITE_70 + ";");
        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);
        HBox progRow = new HBox(savedLabel, sp2, targetLabel);

        // Tombol aksi
        Button editBtn  = makeActionBtn("Edit Alokasi", false);
        Button hapusBtn = makeActionBtn("Hapus Item", true);

        editBtn.setOnAction(e ->
            showEditAlokasiDialog(w, userId, sisaKapasitasPct, wishlistDAO));
        hapusBtn.setOnAction(e ->
            showHapusDialog(w, wishlistDAO));

        HBox btnRow = new HBox(8, editBtn, hapusBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        VBox card = new VBox(6, namaRow, bar, progRow, btnRow);
        card.setPadding(new Insets(12));
        card.setStyle(
            "-fx-background-color:" + CARD_ITEM + ";" +
            "-fx-background-radius:14;"
        );
        return card;
    }

    // ══════════════════════════════════════════════
    //  CARD: Riwayat tercapai
    // ══════════════════════════════════════════════
    private static VBox buildRiwayatCard(Wishlist w) {
        Label namaLabel = new Label(w.getTitle());
        namaLabel.setStyle(
            "-fx-font-weight:bold; -fx-font-size:13px;" +
            "-fx-text-fill:" + WHITE + ";"
        );

        Label tercapaiTag = new Label("✓ Tercapai");
        tercapaiTag.setStyle(
            "-fx-font-size:11px;" +
            "-fx-font-weight:bold;" +
            "-fx-text-fill:" + MIDNIGHT_MIRAGE + ";" +
            "-fx-background-color:" + FIRST_OF_SPRING + ";" +
            "-fx-background-radius:8;" +
            "-fx-padding:3 10;"
        );
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        HBox namaRow = new HBox(namaLabel, sp, tercapaiTag);
        namaRow.setAlignment(Pos.CENTER_LEFT);

        ProgressBar bar = new ProgressBar(1.0);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setMinHeight(8);
        bar.setPrefHeight(10);
        bar.setStyle("-fx-accent:" + FIRST_OF_SPRING + ";");

        Label amtLabel = new Label(
            formatRupiah(w.getSavedAmount()) + " / " + formatRupiah(w.getTargetAmount())
        );
        amtLabel.setStyle("-fx-font-size:11px; -fx-text-fill:" + WHITE_70 + ";");

        VBox card = new VBox(6, namaRow, bar, amtLabel);
        card.setPadding(new Insets(12));
        card.setStyle(
            "-fx-background-color:" + CARD_ITEM + ";" +
            "-fx-background-radius:14;"
        );
        return card;
    }

    // ══════════════════════════════════════════════
    //  DIALOG: Tambah Wishlist
    // ══════════════════════════════════════════════
    private static void showTambahWishlistDialog(int userId, double sisaKapasitasPct,
            double terkumpul, WishlistDAO wishlistDAO) {

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("");
        dialog.getDialogPane().setMinWidth(420);
        dialog.getDialogPane().setPrefWidth(440);
        dialog.getDialogPane().setStyle(
            "-fx-background-color:" + NAVY_SURFACE + ";" +
            "-fx-background-radius:16; -fx-border-radius:16; -fx-padding:0;"
        );

        // Judul dialog (chip kuning)
        Label titleLbl = new Label("Tambah Wishlist");
        titleLbl.setStyle(
            "-fx-background-color:" + FIRST_OF_SPRING + ";" +
            "-fx-text-fill:" + MIDNIGHT_MIRAGE + ";" +
            "-fx-font-size:18px; -fx-font-weight:900;" +
            "-fx-padding:8 20; -fx-background-radius:8;"
        );

        Label sisaLbl = new Label(
            "Sisa kapasitas alokasi: " + (int) sisaKapasitasPct + "%"
        );
        sisaLbl.setStyle(
            "-fx-font-size:13px; -fx-font-weight:bold;" +
            "-fx-text-fill:" + WHITE_70 + ";"
        );

        // Fields
        Label namaLbl = new Label("Nama Wishlist:");
        namaLbl.setStyle("-fx-font-size:13px; -fx-text-fill:" + WHITE_70 + ";");
        TextField namaField = styledTextField("Nama wishlist");

        Label targetLbl = new Label("Target harga:");
        targetLbl.setStyle("-fx-font-size:13px; -fx-text-fill:" + WHITE_70 + ";");
        TextField targetField = styledTextField("Target harga (Rp)");

        Label alokasiLbl = new Label("Alokasi (%):");
        alokasiLbl.setStyle("-fx-font-size:13px; -fx-text-fill:" + WHITE_70 + ";");
        TextField alokasiField = styledTextField("Alokasi dari tabungan (%)");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill:#FF6B6B; -fx-font-size:12px;");
        errorLabel.setWrapText(true);

        dialog.getDialogPane().setHeader(null);
        dialog.getDialogPane().setGraphic(null);

        VBox content = new VBox(10,
            titleLbl, sisaLbl,
            namaLbl, namaField,
            targetLbl, targetField,
            alokasiLbl, alokasiField,
            errorLabel
        );
        content.setStyle("-fx-padding: 0 20 10 20;");
        dialog.getDialogPane().setContent(content);

        ButtonType batalType  = new ButtonType("Keluar",  ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType simpanType = new ButtonType("Simpan", ButtonBar.ButtonData.NEXT_FORWARD);
        dialog.getDialogPane().getButtonTypes().addAll(batalType, simpanType);

        Button batalBtn  = (Button) dialog.getDialogPane().lookupButton(batalType);
        Button simpanBtn = (Button) dialog.getDialogPane().lookupButton(simpanType);

        styleDialogBtn(simpanBtn, true);
        styleDialogBtn(batalBtn,  false);

        simpanBtn.disableProperty().bind(
            namaField.textProperty().isEmpty()
                .or(targetField.textProperty().isEmpty())
                .or(alokasiField.textProperty().isEmpty())
        );

        batalBtn.addEventFilter(ActionEvent.ACTION, e -> dialog.close());

        simpanBtn.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
            try {
                String nama    = namaField.getText().trim();
                double target  = Double.parseDouble(targetField.getText().trim());
                double alokasi = Double.parseDouble(alokasiField.getText().trim());

                if (target < 1_000) {
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
                    0, userId, nama, target, 0, alokasi,
                    "ONGOING", periode, java.time.LocalDateTime.now()
                );

                int newId = wishlistDAO.createWishlist(newWishlist);
                if (newId > 0) {
                    if (terkumpul > 0) {
                        double retro = terkumpul * alokasi / 100;
                        wishlistDAO.addToWishlist(newId, retro);
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

        styleButtonBar(dialog);
        dialog.setResultConverter(btn -> null);
        dialog.showAndWait();
    }

    // ══════════════════════════════════════════════
    //  DIALOG: Edit Alokasi
    // ══════════════════════════════════════════════
    private static void showEditAlokasiDialog(Wishlist w, int userId,
            double sisaKapasitasPct, WishlistDAO wishlistDAO) {

        double kapasitasTersedia = sisaKapasitasPct + w.getMaxLimit();

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("");
        dialog.getDialogPane().setMinWidth(380);
        dialog.getDialogPane().setPrefWidth(400);
        dialog.getDialogPane().setStyle(
            "-fx-background-color:" + NAVY_SURFACE + ";" +
            "-fx-background-radius:16; -fx-border-radius:16; -fx-padding:0;"
        );

        Label titleLbl = new Label("Edit Alokasi — " + w.getTitle());
        titleLbl.setStyle(
            "-fx-background-color:" + FIRST_OF_SPRING + ";" +
            "-fx-text-fill:" + MIDNIGHT_MIRAGE + ";" +
            "-fx-font-size:18px; -fx-font-weight:900;" +
            "-fx-padding:8 20; -fx-background-radius:8;"
        );

        Label subtitleLbl = new Label("Alokasi saat ini: " + (int) w.getMaxLimit() + "%");
        subtitleLbl.setStyle(
            "-fx-font-size:13px; -fx-font-weight:bold;" +
            "-fx-text-fill:" + WHITE_70 + ";"
        );

        Label inputLbl = new Label("Alokasi baru (%):");
        inputLbl.setStyle("-fx-font-size:13px; -fx-text-fill:" + WHITE_70 + ";");

        TextField alokasiField = styledTextField(
            "Maks " + (int) kapasitasTersedia + "%"
        );
        alokasiField.setText(String.valueOf((int) w.getMaxLimit()));

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill:#FF6B6B; -fx-font-size:12px;");

        dialog.getDialogPane().setHeader(null);
        dialog.getDialogPane().setGraphic(null);
        VBox content = new VBox(10, titleLbl, subtitleLbl, inputLbl, alokasiField, errorLabel);
        content.setStyle("-fx-padding: 0 20 10 20;");
        dialog.getDialogPane().setContent(content);

        ButtonType batalType  = new ButtonType("Keluar",  ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType simpanType = new ButtonType("Simpan", ButtonBar.ButtonData.NEXT_FORWARD);
        dialog.getDialogPane().getButtonTypes().addAll(batalType, simpanType);

        Button batalBtn  = (Button) dialog.getDialogPane().lookupButton(batalType);
        Button simpanBtn = (Button) dialog.getDialogPane().lookupButton(simpanType);
        styleDialogBtn(simpanBtn, true);
        styleDialogBtn(batalBtn,  false);
        simpanBtn.disableProperty().bind(alokasiField.textProperty().isEmpty());

        batalBtn.addEventFilter(ActionEvent.ACTION, e -> dialog.close());

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

        styleButtonBar(dialog);
        dialog.setResultConverter(btn -> null);
        dialog.showAndWait();
    }

    // ══════════════════════════════════════════════
    //  DIALOG: Hapus Wishlist
    // ══════════════════════════════════════════════
    private static void showHapusDialog(Wishlist w, WishlistDAO wishlistDAO) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Hapus Wishlist");
        confirm.setHeaderText("Hapus \"" + w.getTitle() + "\"?");
        confirm.setContentText(
            "Wishlist ini akan dihapus dan alokasi " +
            (int) w.getMaxLimit() + "% akan dibebaskan kembali."
        );
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                wishlistDAO.deleteWishlist(w.getId());
                MainScene.refresh();
            }
        });
    }

    // ══════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════
    private static TextField styledTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setMaxWidth(Double.MAX_VALUE);
        tf.setStyle(
            "-fx-background-color:" + NAVY_CARD + ";" +
            "-fx-text-fill:" + FIRST_OF_SPRING + ";" +
            "-fx-prompt-text-fill:" + WHITE_40 + ";" +
            "-fx-font-size:13px; -fx-padding:10 14;" +
            "-fx-background-radius:8; -fx-border-color:transparent;" +
            "-fx-pref-height:40;"
        );
        return tf;
    }

    private static void styleDialogBtn(Button btn, boolean isPrimary) {
        if (isPrimary) {
            String base =
                "-fx-background-color:" + FIRST_OF_SPRING + ";" +
                "-fx-text-fill:" + MIDNIGHT_MIRAGE + ";" +
                "-fx-font-size:13px; -fx-font-weight:bold;" +
                "-fx-padding:10 28; -fx-background-radius:8; -fx-cursor:hand;";
            btn.setStyle(base);
            btn.setOnMouseEntered(e -> btn.setStyle(base.replace(FIRST_OF_SPRING, SPRING_DARK)));
            btn.setOnMouseExited(e -> btn.setStyle(base));
        } else {
            String base =
                "-fx-background-color:transparent;" +
                "-fx-text-fill:" + FIRST_OF_SPRING + ";" +
                "-fx-font-size:13px; -fx-font-weight:bold;" +
                "-fx-padding:10 28; -fx-background-radius:8;" +
                "-fx-border-color:" + WHITE_40 + "; -fx-border-radius:8; -fx-cursor:hand;";
            btn.setStyle(base);
            btn.setOnMouseEntered(e -> btn.setStyle(base.replace("transparent", NAVY_CARD)));
            btn.setOnMouseExited(e -> btn.setStyle(base));
        }
        btn.setMinWidth(100);
    }

    private static void styleButtonBar(Dialog<?> dialog) {
        dialog.getDialogPane().lookup(".button-bar").setStyle(
            "-fx-background-color:" + NAVY_SURFACE + ";" +
            "-fx-padding:10 20 20 20;"
        );
    }

    private static Button makeActionBtn(String text, boolean isDanger) {
        Button btn = new Button(text);
        String color = isDanger
            ? "rgba(255, 100, 100, 0.25)"
            : "rgba(219, 230, 76, 0.15)";
        String textColor = isDanger ? "#FF8080" : FIRST_OF_SPRING;
        btn.setStyle(
            "-fx-background-color:" + color + ";" +
            "-fx-text-fill:" + textColor + ";" +
            "-fx-font-size:11px; -fx-font-weight:bold;" +
            "-fx-padding:5 12; -fx-background-radius:8; -fx-cursor:hand;"
        );
        return btn;
    }

    private static Separator styledSep() {
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.10);");
        return sep;
    }
}