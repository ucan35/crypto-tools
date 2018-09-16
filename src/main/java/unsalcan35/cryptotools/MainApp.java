package unsalcan35.cryptotools;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Parent rootNode = loader.load(System.class.getResourceAsStream("/fxml/main.fxml"));
        Scene scene = new Scene(rootNode, 650, 400);
        stage.setResizable(false);
        stage.setTitle("Crypto Tools");
        stage.setScene(scene);
        stage.show();
    }
}
