package client.view.search;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

import static main.SLChat.START_GUI_PATH;
import static main.SLChat.rooms;

/**
 * Controller, responsible for
 * users searching for server
 * operations.
 */
public class SearchController {
        @FXML private Button getBackButton;
        @FXML private Button connectButton;
        @FXML private ListView<String> roomsList;

    /**
     * Switches GUI to main menu.
     * @param event User pushed "Back" button.
     */
    @FXML
    private void handleBackButtonAction(ActionEvent event) {
            Parent root = new GridPane();
        try {
            root = FXMLLoader.load(getClass().getResource(START_GUI_PATH));
        } catch (IOException exc) {
            System.out.println("Exception thrown while switching to main window from search.");
            exc.printStackTrace();
        }
        Stage stage = (Stage) getBackButton.getScene().getWindow();
        stage.setTitle("SLChat");
        stage.setScene(new Scene(root));
    }

    /**
     * Connects to the room, chosen
     * by user from {@link #roomsList}.
     *
     * @param event User pushed this button.
     */
    @FXML
    private void handleConnectButtonAction(ActionEvent event) {

    }
}
