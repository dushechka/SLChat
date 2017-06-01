package client.view.start;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import client.view.search.*;

/**
 * Main menu implementation;
 */
public class Start extends Application {
        @FXML private Button startServer;
        @FXML private Button searchRoom;

    @FXML
    protected void handleStartButtonAction(ActionEvent event) {
        System.out.println("Start button pressed.");
    }

    @FXML
    protected void handleSearchButtonAction(ActionEvent event) {
        Thread searchMenu = new Thread(new Search());
        searchMenu.start();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Start.fxml"));

        Scene scene = new Scene(root,280,280);

        stage.setTitle("SLChat");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
