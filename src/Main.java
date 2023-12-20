import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("gui/main.fxml"));
        primaryStage.setTitle("MyTunes");
        primaryStage.setScene(new Scene(root, 768, 432));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}