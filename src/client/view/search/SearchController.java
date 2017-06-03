package client.view.search;


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
 * Search server menu controller;
 */
public class SearchController {
        @FXML private Button getBackButton;
        @FXML private Button connectButton;

    // Switching to main menu;
    @FXML
    private void handleBackButtonAction(ActionEvent event) {
            Parent root = new GridPane();
        try {
            root = FXMLLoader.load(getClass().getResource("/client/view/start/Start.fxml"));
        } catch (IOException exc) {
            System.out.println("Exception thrown while switching to main window from search.");
            exc.printStackTrace();
        }
        Stage stage = (Stage) getBackButton.getScene().getWindow();
        stage.setTitle("SLChat");
        stage.setScene(new Scene(root));
    }
}
