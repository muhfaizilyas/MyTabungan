package mytabungan.scenes;
 
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import mytabungan.dao.SavingDAO;
import mytabungan.dao.UserDAO;
import mytabungan.models.MonthlySaving;
import mytabungan.models.User;
import mytabungan.utils.SessionManager;
import mytabungan.utils.ValidationUtil;
 
public class LoginScene {
    private Stage stage;
    private UserDAO userDAO = new UserDAO();
    public LoginScene(Stage stage) {
        this.stage = stage;
    }

    public static Scene getLogin(Stage stage) {
        // === Left Side ===
        VBox leftSide = AuthLayout.buildPanel();
        leftSide.setPrefWidth(320);
        leftSide.setMaxWidth(320);
        leftSide.setMinWidth(320);

        // === Right Side ===
        VBox rightSide = buildFormPanel(stage);
        rightSide.setPrefWidth(320);
        rightSide.setMaxWidth(320);
        rightSide.setMinWidth(320);

        // === Root: single navy background, centered with padding ===
        HBox root = new HBox(60, leftSide, rightSide);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #0A2D5A;");

        Scene scene = new Scene(root, 960, 600);
        scene.getStylesheets().add(
            LoginScene.class.getResource("/style.css").toExternalForm()
        );
        return scene;
    }

    private static VBox buildFormPanel(Stage stage) {
        // Right side: transparent, centers the card both vertically & horizontally
        VBox rightSide = new VBox();
        rightSide.setAlignment(Pos.CENTER);
        rightSide.setStyle("-fx-background-color: transparent;");

        // === Card — sized only to its content, NOT stretched ===
        VBox card = new VBox(0);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(340);
        card.setPrefWidth(340);
        // Do NOT set prefHeight — let content determine height
        card.setStyle("""
            -fx-background-color: #001F3F;
            -fx-background-radius: 22;
            -fx-padding: 32 36 28 36;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 28, 0, 0, 6);
        """);

        // === Title ===
        Label title = new Label("Login");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");

        Rectangle underline = new Rectangle(42, 3);
        underline.setFill(Color.web("#FFFFFF"));
        underline.setArcWidth(2);
        underline.setArcHeight(2);

        VBox titleBlock = new VBox(6, title, underline);
        titleBlock.setAlignment(Pos.CENTER);
        Region gap1 = new Region(); gap1.setPrefHeight(24);

        // === Email Field ===
        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email or Username...");
        emailField.setMaxWidth(Double.MAX_VALUE);
        emailField.setStyle("""
            -fx-background-color: #C8CBCC;
            -fx-border-color: transparent;
            -fx-border-radius: 20;
            -fx-background-radius: 20;
            -fx-padding: 11 16 11 16;
            -fx-font-size: 13px;
            -fx-text-fill: #222;
            -fx-prompt-text-fill: #555;
        """);

        Region gap2 = new Region(); gap2.setPrefHeight(14);
        // === Password Field ===
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password...");
        passwordField.setMaxWidth(Double.MAX_VALUE);
        passwordField.setStyle("""
            -fx-background-color: #C8CBCC;
            -fx-border-color: transparent;
            -fx-border-radius: 20;
            -fx-background-radius: 20;
            -fx-padding: 11 16 11 16;
            -fx-font-size: 13px;
            -fx-text-fill: #222;
            -fx-prompt-text-fill: #555;
        """);

        Region gap3 = new Region(); gap3.setPrefHeight(48);
        // === Login Button ===
        Button loginBtn = new Button("LOGIN");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        String btnStyle = """
            -fx-background-color: #C8D92A;
            -fx-text-fill: #1A1A1A;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-background-radius: 22;
            -fx-padding: 12 0 12 0;
            -fx-cursor: hand;
        """;
        String btnHover = """
            -fx-background-color: #b5c420;
            -fx-text-fill: #1A1A1A;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-background-radius: 22;
            -fx-padding: 12 0 12 0;
            -fx-cursor: hand;
        """;
        loginBtn.setStyle(btnStyle);
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle(btnHover));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle(btnStyle));

        // === Message Label ===
        Label message = new Label();
        message.setWrapText(true);
        message.setMaxWidth(280);
        message.setAlignment(Pos.CENTER);

        Region gap4 = new Region(); gap4.setPrefHeight(4);

        // === Register Link ===
        Label desc = new Label("Don't have an account? ");
        desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #AAAAAA;");

        Label registerNow = new Label("Register now");
        registerNow.setStyle("""
            -fx-font-size: 13px;
            -fx-text-fill: #FFFFFF;
            -fx-underline: true;
            -fx-font-weight: bold;
            -fx-cursor: hand;
        """);

        HBox registerBox = new HBox(3, desc, registerNow);
        registerBox.setAlignment(Pos.CENTER);
        Region gap5 = new Region(); gap5.setPrefHeight(12);

        card.getChildren().addAll(
            titleBlock, gap1,
            emailField, gap2,
            passwordField, gap3,
            loginBtn,
            gap4, message, gap5,
            registerBox
        );
        rightSide.getChildren().add(card);

        // === Navigation ===
        registerNow.setOnMouseClicked(e -> {
            stage.setScene(RegisterScene.getRegist(stage));
        });

        // === Action Login ===
        loginBtn.setOnAction(e -> {
            String user = emailField.getText();
            String password = passwordField.getText();

            if (ValidationUtil.isEmpty(user) || ValidationUtil.isEmpty(password)) {
                message.setText("Inputan tidak boleh kosong!");
                message.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 11px;");
                return;
            }

            if (!ValidationUtil.isValidPassword(password)) {
                message.setText("Password minimal 8 karakter!");
                message.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 11px;");
                return;
            }

            message.setText("Processing login...");
            message.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 11px;");
            User loggedInUser = new UserDAO().login(user, password);

            if (loggedInUser != null) {
                SessionManager.login(loggedInUser);
                message.setText("Login berhasil!");
                message.setStyle("-fx-text-fill: #C8D92A; -fx-font-weight: bold; -fx-font-size: 11px;");

                MainScene utama = new MainScene();
                PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
                delay.setOnFinished(event -> {
                    stage.setScene(utama.getMain(stage));
                    Platform.runLater(() -> {
                        SavingDAO savingDAO = new SavingDAO();
                        MonthlySaving saving = savingDAO.getSavingByUserId(loggedInUser.getId());
                        if (saving == null) {
                            MonthlySaving created = TabunganScene.showCreateSavingDialog(
                                loggedInUser.getId(), savingDAO
                            );
                            if (created != null) {
                                MainScene.refresh();
                            }
                        }
                    });
                });
                delay.play();
            } else {
                message.setText("Username / email atau password salah!");
                message.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 11px;");
            }
        });
        return rightSide;
    }
}
