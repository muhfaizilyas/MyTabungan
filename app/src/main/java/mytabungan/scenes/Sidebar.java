package mytabungan.scenes;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
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
        Button tabunganButton = createMenuButton("/images/iconmytabungan.png", "My Tabungan");
        Button wishlistButton = createMenuButton("/images/icon wishlist.png", "WishlistKu");
        Button growthButton   = createMenuButton("/images/icon growth.png",   "Growth");

        VBox menuBox = new VBox(8, tabunganButton, wishlistButton, growthButton);
        menuBox.setStyle("-fx-padding: 10 16 10 16;");

        root.setCenter(TabunganScene.buildPage());
        tabunganButton.setOnAction(e -> root.setCenter(TabunganScene.buildPage()));
        wishlistButton.setOnAction(e -> root.setCenter(WishlistScene.buildPage()));
        growthButton.setOnAction(e -> root.setCenter(GrowthScene.buildPage()));

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // logout
        Button logoutButton = createLogoutButton("/images/logout icon.png", "Logout");
        logoutButton.setOnAction(e -> {
            SessionManager.logout();
            stage.setScene(LoginScene.getLogin(stage));
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

    // Logo
    private static VBox buildLogoBox() {
        var logoFile = Sidebar.class.getResourceAsStream("/images/logo.png");

        if (logoFile == null) {
            throw new RuntimeException("Logo tidak ditemukan! Pastikan ada di src/main/resources/images/logo.png");
        }

        ImageView logo = new ImageView(new Image(logoFile));
        logo.setFitWidth(140);
        logo.setPreserveRatio(true);

        VBox logoBox = new VBox(logo);
        logoBox.setStyle("-fx-padding: 36 0 24 20;");
        return logoBox;
    }

    // Menu 
    private static Button createMenuButton(String iconPath, String text) {
        String baseStyle =
            "-fx-background-color: " + FIRST_OF_SPRING + ";" +
            "-fx-text-fill: " + MIDNIGHT_MIRAGE + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-alignment: CENTER_LEFT;" +
            "-fx-padding: 12 16;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;";
            // "-fx-border-text-gap: 8;";

        ImageView iconView = loadIcon(iconPath);
        StackPane iconWrapper = new StackPane(iconView);
        iconWrapper.setMinWidth(24);
        iconWrapper.setMaxWidth(24);
        iconWrapper.setPrefWidth(24);
        iconWrapper.setAlignment(Pos.CENTER_LEFT);
        // iconWrapper.setStyle("-fx-border-color: red; -fx-border-width: 1;");

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

        button.setOnMouseEntered(e  -> button.setStyle(baseStyle.replace(FIRST_OF_SPRING, SPRING_DARK)));
        button.setOnMousePressed(e  -> button.setStyle(baseStyle.replace(FIRST_OF_SPRING, SPRING_DARKER)));
        button.setOnMouseReleased(e -> button.setStyle(baseStyle.replace(FIRST_OF_SPRING, SPRING_DARK)));
        button.setOnMouseExited(e   -> button.setStyle(baseStyle));

        return button;
    }

    private static Button createLogoutButton(String iconPath, String text) {
        String baseStyle =
            "-fx-background-color: " + FIRST_OF_SPRING + ";" +
            "-fx-text-fill: " + MIDNIGHT_MIRAGE + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-alignment: CENTER_LEFT;" +
            "-fx-padding: 12 16;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;";
            // "-fx-graphic-text-gap: 8;";

        ImageView iconView = loadIcon(iconPath);
        StackPane iconWrapper = new StackPane(iconView);
        iconWrapper.setMinWidth(24);
        iconWrapper.setMaxWidth(24);
        iconWrapper.setPrefWidth(24);
        iconWrapper.setAlignment(Pos.CENTER_LEFT);
        // iconWrapper.setStyle("-fx-border-color: red; -fx-border-width: 1;");

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
        button.setGraphic(content);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle(baseStyle);
        button.setContentDisplay(javafx.scene.control.ContentDisplay.LEFT);

        button.setOnMouseEntered(e  -> button.setStyle(baseStyle.replace(FIRST_OF_SPRING, SPRING_DARK)));
        button.setOnMousePressed(e  -> button.setStyle(baseStyle.replace(FIRST_OF_SPRING, SPRING_DARKER)));
        button.setOnMouseReleased(e -> button.setStyle(baseStyle.replace(FIRST_OF_SPRING, SPRING_DARK)));
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
            System.out.println("⚠️ Icon tidak ditemukan: " + path);
        }
        
        return iconView;
    }
}


