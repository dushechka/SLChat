package client.view.start;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import static main.SLChat.*;
import java.io.IOException;

/**
 * Controller that handles main window actions;
 */
public class StartController {
        @FXML private Button startButton;
        @FXML private Button searchButton;
        @FXML private TextField enterName;

    // Switching to start server window;
    @FXML
    protected void handleStartButtonAction(ActionEvent event) {
            Parent root = new GridPane();
            Stage stage = new Stage();
        try {
            root = FXMLLoader.load(getClass().getResource("/server/view/create/Create.fxml"));
        } catch (IOException exc) {
            System.out.println("Exception thrown while switching main window to create window.");
            exc.printStackTrace();
        }
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        // Setting this stage to be the new stage owner;
        stage.initOwner((Stage) startButton.getScene().getWindow());
        // Make this stage frozen, while new stage is open;
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("SLChat");
        stage.show();
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
        stage.setTitle("SLChat");
        stage.setScene(new Scene(pane));
    }

    @FXML
    protected void handleEnterNameFieldAction(ActionEvent event) {
        String serverName = enterName.getText();
        enterName.setText("");
        // Making an alert, that indicates what's going on;
        startClient(serverName);
        // Client might not already started;
        try {
            Thread.sleep(10);
        } catch (InterruptedException ie) {}
        if (!IS_CLIENT_RUNNING) {
            Alert nameAlert = new Alert(Alert.AlertType.INFORMATION);
            nameAlert.setTitle("Wrong.");
            nameAlert.setHeaderText(null);
            nameAlert.setContentText("Server not found!");
            nameAlert.showAndWait();
        } else {
            // Opening client GUI;
            mainView.openChatWindow(clientGUIPath);
        }
    }
}
