package mytabungan.scenes;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainScene {
    private static BorderPane root;

    public static Scene getMain(Stage stage) {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #0A2D5A");
        root.setLeft(
            Sidebar.buildSidebar(root, stage)
        );
        root.setCenter(
            TabunganScene.buildPage()
        );

        Scene scene = new Scene(root, 960, 600); // Patokan lebar dan tinggi
        scene.getStylesheets().add(
            LoginScene.class.getResource("/style.css").toExternalForm()
        );
        return scene;
    }

    public static void refresh() {
        root.setCenter(TabunganScene.buildPage());
    }
}