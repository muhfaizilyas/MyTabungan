package mytabungan.scenes;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class AuthLayout {
    public static VBox buildPanel(){
        final double CONTENT_W = 260;

        VBox panel = new VBox(32);
        panel.setAlignment(Pos.CENTER);
        panel.setPrefWidth(460);
        panel.setMinWidth(460);
        panel.setMaxWidth(Double.MAX_VALUE);
        panel.setStyle("-fx-background-color: #001F3F; -fx-padding: 50 40 50 40;");

        var logoApp = LoginScene.class.getResourceAsStream("/images/tabunginAja.png");
        if (logoApp == null) {
            throw new RuntimeException("Logo tidak ditemukan.");
        }

        ImageView logoView = new ImageView(new Image(logoApp));
        logoView.setFitWidth(CONTENT_W);
        // logoView.setFitHeight(120);
        logoView.setPreserveRatio(true);

        Line topLine = new Line(0, 0, 40, 0);
        topLine.setStroke(Color.web("#DBE64C"));
        topLine.setStrokeWidth(2.5);
        HBox topLineBox = new HBox(topLine);
        // topLineBox.setPrefWidth(CONTENT_W);
        topLineBox.setAlignment(Pos.CENTER);

        // Tagline
        Text t1 = new Text("\u201cWarga desa gak pakai ");
        Text t1Bold = new Text("dolar");
        Text t1End   = new Text("\u201d\n");
        Text t2 = new Text("\nDollar naik, kita tetap senyum\nkarena rupiah kita ");
        Text t2Bold = new Text("ditabung");
        Text t2End = new Text(",\nbukan dihabisin duluan.");

        t1.setFill(javafx.scene.paint.Color.WHITE);
        t1Bold.setStyle("-fx-fill: #DBE64C;");
        t1End.setFill(javafx.scene.paint.Color.WHITE);
        t2.setFill(javafx.scene.paint.Color.WHITE);
        t2Bold.setStyle("-fx-fill: #DBE64C; -fx-underline: true;");
        t2End.setFill(javafx.scene.paint.Color.WHITE);

        TextFlow tagline = new TextFlow(t1, t1Bold, t1End, t2, t2Bold, t2End);
        tagline.setPrefWidth(CONTENT_W);
        tagline.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-line-spacing: 3;");
        tagline.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Line bottomLine = new Line(0, 0, 40, 0);
        bottomLine.setStroke(Color.web("#DBE64C"));
        bottomLine.setStrokeWidth(2.5);
        HBox bottomLineBox = new HBox(bottomLine);
        bottomLineBox.setAlignment(Pos.CENTER);

        // Wrapper tagline
        VBox textBox = new VBox(10, topLineBox, tagline, bottomLineBox);
        textBox.setPrefWidth(CONTENT_W);
        textBox.setAlignment(Pos.CENTER);

        panel.getChildren().addAll(logoView, textBox);
        return panel;
    }
}
