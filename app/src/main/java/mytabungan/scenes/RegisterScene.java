package mytabungan.scenes;
 
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import mytabungan.dao.UserDAO;
import mytabungan.models.User;
import mytabungan.utils.ValidationUtil;

public class RegisterScene {

    public static Scene getRegist(Stage stage) {
        // === Left Side: form card ===
        VBox leftSide = buildFormPanel(stage);
        leftSide.setPrefWidth(320);
        leftSide.setMaxWidth(320);
        leftSide.setMinWidth(320);

        // === Right Side: AuthLayout ===
        VBox rightSide = AuthLayout.buildPanel();
        rightSide.setPrefWidth(320);
        rightSide.setMaxWidth(320);
        rightSide.setMinWidth(320);

        // === Root: single navy background ===
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
        VBox wrapper = new VBox();
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setStyle("-fx-background-color: transparent;");

        // === Card ===
        VBox card = new VBox(0);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(340);
        card.setPrefWidth(340);
        card.setStyle("""
            -fx-background-color: #001F3F;
            -fx-background-radius: 22;
            -fx-padding: 32 36 28 36;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 28, 0, 0, 6);
        """);

        // === Title ===
        Label title = new Label("Signup");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");

        Rectangle underline = new Rectangle(54, 3);
        underline.setFill(Color.web("#FFFFFF"));
        underline.setArcWidth(2);
        underline.setArcHeight(2);

        VBox titleBlock = new VBox(6, title, underline);
        titleBlock.setAlignment(Pos.CENTER);

        Region gap1 = new Region(); gap1.setPrefHeight(22);

        // === Email Field ===
        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email...");
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

        // === Username Field ===
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter Username...");
        usernameField.setMaxWidth(Double.MAX_VALUE);
        usernameField.setStyle("""
            -fx-background-color: #C8CBCC;
            -fx-border-color: transparent;
            -fx-border-radius: 20;
            -fx-background-radius: 20;
            -fx-padding: 11 16 11 16;
            -fx-font-size: 13px;
            -fx-text-fill: #222;
            -fx-prompt-text-fill: #555;
        """);

        Region gap3 = new Region(); gap3.setPrefHeight(14);

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

        Region gap4 = new Region(); gap4.setPrefHeight(48);

        // === Register Button ===
        Button registerBtn = new Button("REGISTER");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
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
        registerBtn.setStyle(btnStyle);
        registerBtn.setOnMouseEntered(e -> registerBtn.setStyle(btnHover));
        registerBtn.setOnMouseExited(e -> registerBtn.setStyle(btnStyle));

        // === Message Label ===
        Label message = new Label();
        message.setWrapText(true);
        message.setMaxWidth(280);
        message.setAlignment(Pos.CENTER);

        Region gap5 = new Region(); gap5.setPrefHeight(4);

        // === Login Link ===
        Label desc = new Label("Already have an account? ");
        desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #AAAAAA;");

        Label loginHere = new Label("Login here");
        loginHere.setStyle("""
            -fx-font-size: 13px;
            -fx-text-fill: #FFFFFF;
            -fx-underline: true;
            -fx-font-weight: bold;
            -fx-cursor: hand;
        """);

        HBox loginBox = new HBox(3, desc, loginHere);
        loginBox.setAlignment(Pos.CENTER);
        Region gap6 = new Region(); gap6.setPrefHeight(12);

        card.getChildren().addAll(
            titleBlock, gap1,
            emailField, gap2,
            usernameField, gap3,
            passwordField, gap4,
            registerBtn,
            gap5, message, gap6,
            loginBox
        );

        wrapper.getChildren().add(card);

        // === Navigation ===
        loginHere.setOnMouseClicked(e -> {
            stage.setScene(LoginScene.getLogin(stage));
        });

        // === Action Register ===
        registerBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            UserDAO userDAO = new UserDAO();

            if (ValidationUtil.isEmpty(username) || ValidationUtil.isEmpty(email) || ValidationUtil.isEmpty(password)) {
                message.setText("Inputan tidak boleh kosong!");
                message.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 11px;");
                return;
            }
            if (!ValidationUtil.isValidEmail(email)) {
                message.setText("Format email tidak valid!");
                message.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 11px;");
                return;
            }
            if (!ValidationUtil.isValidUsername(username)) {
                message.setText("Username harus min. 3 karakter dan max. 20 karakter!");
                message.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 11px;");
                return;
            }
            if (!ValidationUtil.isValidPassword(password)) {
                message.setText("Password minimal 8 karakter!");
                message.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 11px;");
                return;
            }
            if (userDAO.isUsernameExists(username)) {
                message.setText("Username sudah digunakan.");
                message.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 11px;");
                return;
            }

            if (userDAO.isEmailExists(email)) {
                message.setText("Email sudah terdaftar!");
                message.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 11px;");
                return;
            }

            message.setText("Processing registration...");
            message.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 11px;");

            User user = new User(username, email, password);
            boolean success = new UserDAO().register(user);
            if (success) {
                message.setText("Registrasi berhasil! Silakan login.");
                message.setStyle("-fx-text-fill: #C8D92A; -fx-font-weight: bold; -fx-font-size: 11px;");

                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(event -> {
                    stage.setScene(LoginScene.getLogin(stage));
                });
                delay.play();
            } else {
                message.setText("Terjadi kesalahan. Registrasi Anda gagal!");
                message.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 11px;");
            }
        });

        return wrapper;
    }
}