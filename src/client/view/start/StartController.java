package client.view.start;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller that handles main window actions;
 */
public class StartController {
        @FXML private Button startButton;
        @FXML private Button searchButton;

    // Switching to start server window;
    @FXML
    protected void handleStartButtonAction(ActionEvent event) {
            Parent root = new GridPane();
            Stage stage;
        try {
            root = FXMLLoader.load(getClass().getResource("/server/view/create/Create.fxml"));
        } catch (IOException exc) {
            System.out.println("Exception thrown while switching main window to create window.");
            exc.printStackTrace();
        }
        stage = (Stage) startButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    //Swithing to search server window;
    @FXML
    protected void handleSearchButtonAction(ActionEvent event) {
            Parent pane = new GridPane();
            Stage stage;
        try {
            pane = FXMLLoader.load(getClass().getResource("/client/view/search/Search.fxml"));
        } catch (IOException exc) {
            System.out.println("Exception thrown while switching from main window to search window.");
            exc.printStackTrace();
        }
        stage = (Stage) searchButton.getScene().getWindow();
        stage.setScene(new Scene(pane));
    }
}
