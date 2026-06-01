package mytabungan.scenes;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mytabungan.utils.SessionManager;

public class Sidebar {
    // Color Palette
    private static final String MIDNIGHT_MIRAGE = "#001F3F";
    private static final String NUIT_BLANCHE    = "#1E488F";
    private static final String FIRST_OF_SPRING = "#DBE64C";
    private static final String SPRING_DARK     = "#b8c23a";
    private static final String SPRING_DARKER   = "#9aaa2e";

    public static VBox buildSidebar(BorderPane root, Stage stage) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(210);
        sidebar.setStyle(
            "-fx-background-color: " + MIDNIGHT_MIRAGE + ";" +
            "-fx-background-radius: 0 20 20 0;" +
            "-fx-padding: 0;"
        );

        // Logo
        VBox logoBox = buildLogoBox();

        // Menu Buttons
        Button tabunganButton = createMenuButton("/images/iconmytabungan.png", "MyTabungan");
        Button wishlistButton = createMenuButton("/images/icon_wishlist.png", "MyWishlist");
        Button growthButton   = createMenuButton("/images/icon_growth.png",   "MyGrowth");
        Button[] navButtons = { tabunganButton, wishlistButton, growthButton };

        VBox menuBox = new VBox(8, tabunganButton, wishlistButton, growthButton);
        menuBox.setStyle("-fx-padding: 10 16 10 16;");

        setActive(tabunganButton, navButtons);
        root.setCenter(TabunganScene.buildPage());
        tabunganButton.setOnAction(e -> { setActive(tabunganButton, navButtons); root.setCenter(TabunganScene.buildPage());});
        wishlistButton.setOnAction(e -> {setActive(wishlistButton, navButtons); root.setCenter(WishlistScene.buildPage());});
        growthButton.setOnAction(e -> { setActive(growthButton, navButtons); root.setCenter(GrowthScene.buildPage());});

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // logout
        Button logoutButton = createLogoutButton("/images/logout icon.png", "Logout");
        logoutButton.setOnAction(e -> {
            Alert confirmLogout = new Alert(Alert.AlertType.CONFIRMATION);

            confirmLogout.setTitle("Logout");
            confirmLogout.setHeaderText("Apakah kamu yakin ingin keluar?");
            confirmLogout.setContentText("Kamu akan keluar dari akun ini.");
            confirmLogout.getDialogPane().setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            confirmLogout.getDialogPane().getStylesheets().add(
                Sidebar.class.getResource("/style.css").toExternalForm()
            );

            Button okButton = (Button) confirmLogout.getDialogPane().lookupButton(ButtonType.OK);
            okButton.getStyleClass().add("buttonOK");

            Button cancelButton = (Button) confirmLogout.getDialogPane().lookupButton(ButtonType.CANCEL);
            cancelButton.getStyleClass().add("buttonCancel");

            confirmLogout.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    SessionManager.logout();
                    stage.setScene(LoginScene.getLogin(stage));
                }
            });
        });

        VBox logoutBox = new VBox(logoutButton);
        logoutBox.setStyle("-fx-padding: 10 16 20 16;");

        sidebar.getChildren().addAll(
            logoBox,
            menuBox,
            spacer,
            logoutBox
        );
        return sidebar;
    }

    private static void setActive(Button active, Button[] allButtons) {
        for (Button btn : allButtons) {
            boolean isActive = btn == active;
            String bg        = isActive ? SPRING_DARKER   : FIRST_OF_SPRING;
            String textColor = isActive ? MIDNIGHT_MIRAGE : MIDNIGHT_MIRAGE;

            String style = buildMenuStyle(bg, textColor);
            btn.setStyle(style);
            
            if (btn.getGraphic() instanceof HBox hbox) {
                hbox.getChildren().stream()
                    .filter(n -> n instanceof Label)
                    .map(n -> (Label) n)
                    .findFirst()
                    .ifPresent(lbl -> lbl.setStyle(
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + textColor + ";"
                    ));
            }

            if (!isActive) {
                btn.setOnMouseEntered(ev -> btn.setStyle(buildMenuStyle(SPRING_DARK,    MIDNIGHT_MIRAGE)));
                btn.setOnMousePressed(ev -> btn.setStyle(buildMenuStyle(SPRING_DARKER,  MIDNIGHT_MIRAGE)));
                btn.setOnMouseReleased(ev-> btn.setStyle(buildMenuStyle(SPRING_DARK,    MIDNIGHT_MIRAGE)));
                btn.setOnMouseExited(ev  -> btn.setStyle(buildMenuStyle(FIRST_OF_SPRING,    MIDNIGHT_MIRAGE)));
            } else {
                btn.setOnMouseEntered(null);
                btn.setOnMousePressed(null);
                btn.setOnMouseReleased(null);
                btn.setOnMouseExited(null);
            }
        }
    }

    private static String buildMenuStyle(String bg, String textColor) {
        return "-fx-background-color: " + bg + ";" +
                "-fx-text-fill: " + textColor + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-alignment: CENTER_LEFT;" +
                "-fx-padding: 12 16;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;";
    }

    // Logo
    private static VBox buildLogoBox() {
        var logoFile = Sidebar.class.getResourceAsStream("/images/tabunginAja.png");

        if (logoFile == null) {
            throw new RuntimeException("Logo tidak ditemukan! Pastikan ada di src/main/resources/images");
        }

        ImageView logo = new ImageView(new Image(logoFile));
        logo.setFitWidth(160);
        logo.setPreserveRatio(true);

        VBox logoBox = new VBox(logo);
        logoBox.setStyle("-fx-padding: 36 0 24 22;");
        return logoBox;
    }

    // Menu
    private static Button createMenuButton(String iconPath, String text) {
        return buildButton(iconPath, text);
    }

    private static Button createLogoutButton(String iconPath, String text) {
        return buildButton(iconPath, text);
    }

    private static Button buildButton(String iconPath, String text) {
        String baseStyle = buildMenuStyle(FIRST_OF_SPRING, MIDNIGHT_MIRAGE);

        ImageView iconView = loadIcon(iconPath);
        StackPane iconWrapper = new StackPane(iconView);
        iconWrapper.setMinWidth(24);
        iconWrapper.setMaxWidth(24);
        iconWrapper.setPrefWidth(24);
        iconWrapper.setAlignment(Pos.CENTER_LEFT);

        Label textLabel = new Label(text);
        textLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + MIDNIGHT_MIRAGE + ";"
        );

        HBox content = new HBox(4, iconWrapper, textLabel);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setStyle("-fx-padding: 0 0 0 4;");

        Button button = new Button();
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle(baseStyle);
        button.setGraphic(content);
        button.setContentDisplay(javafx.scene.control.ContentDisplay.LEFT);

        button.setOnMouseEntered(e  -> button.setStyle(buildMenuStyle(SPRING_DARK,   MIDNIGHT_MIRAGE)));
        button.setOnMousePressed(e  -> button.setStyle(buildMenuStyle(SPRING_DARKER, MIDNIGHT_MIRAGE)));
        button.setOnMouseReleased(e -> button.setStyle(buildMenuStyle(SPRING_DARK,   MIDNIGHT_MIRAGE)));
        button.setOnMouseExited(e   -> button.setStyle(baseStyle));

        return button;
    }
    
    private static ImageView loadIcon(String path) {
        var file = Sidebar.class.getResourceAsStream(path);

        ImageView iconView = new ImageView();
        if (file != null) {
            iconView = new ImageView(new Image(file));
            iconView.setFitWidth(20);
            iconView.setFitHeight(20);
            iconView.setPreserveRatio(false);
        } else {
            iconView = new ImageView();
            iconView.setFitWidth(20);
            iconView.setFitHeight(20);
            iconView.setPreserveRatio(false);
            System.out.println("Icon tidak ditemukan: " + path);
        }
        
        return iconView;
    }
}