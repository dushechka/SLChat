package client.view.start;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import static main.SLChat.*;
/**
 * Controller that handles main window actions;
 */
public class StartController {
        @FXML private Button startButton;
        @FXML private Button searchButton;
        @FXML private TextField enterName;
        @FXML private Text enterSomething;

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
        mainView.changeWindow("/client/view/search/Search.fxml");
        startClient(getIP());
    }

    @FXML
    protected void handleEnterNameFieldAction(ActionEvent event) {
        if (enterName.getText().length() > 32) {
            mainView.alertWindow("Server name is too long", "Please enter a smaller name.");
        } else if (enterName.getText().isEmpty()) {
            enterSomething.setText("Please enter something!");
        } else {
            String serverName = enterName.getText();
            enterName.setText("");
            // Making an alert, that indicates what's going on;
            startClient(serverName);
            // Client might not already started;
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
            }
            if (!IS_CLIENT_RUNNING) {
                mainView.alertWindow("Wrong", "Server not found!");
            } else {
                // Opening client GUI;
                mainView.changeWindow(clientGUIPath);
            }
        }
    }
}
