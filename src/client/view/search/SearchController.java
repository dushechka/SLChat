package client.view.search;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import static main.SLChat.START_GUI_PATH;
import java.io.IOException;

/**
 * Controller, responsible for
 * users searching for server
 * operations.
 */
public class SearchController {
        @FXML private Button getBackButton;
        @FXML private Button connectButton;
        @FXML static Text roomName;

    // Switching to main menu;
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
}
