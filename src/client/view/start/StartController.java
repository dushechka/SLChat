package client.view.start;

import client.model.Client;
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
import java.net.InetAddress;
import java.net.UnknownHostException;

import static main.SLChat.*;
/**
 * Controller that handles main window actions;
 *
 * Serves for changing frontend windows and
 * running backend threads when needed.
 *
 * @since 0.3
 */
public class StartController {
        @FXML private Button startButton;
        @FXML private Button searchButton;
        @FXML private TextField enterName;
        /* Text, intended to warn user, if he/she entered wrong values. */
        @FXML private Text enterSomething;

    /**
     * Opens new window for
     * server creation, handled by
     * {@link server.view.create.CreateController}.
     *
     * @param event  User pressed this button.
     */
    @FXML
    protected void handleStartButtonAction(ActionEvent event) {
            Parent root = new GridPane();
            Stage stage = new Stage();
        /* opening create room window */
        try {
            root = FXMLLoader.load(getClass().getResource("/server/view/create/Create.fxml"));
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            /* setting new stage to be the stage owner */
            mainScene = startButton.getScene();
            stage.initOwner((Stage) mainScene.getWindow());
            /* make this stage frozen, while new stage is open */
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle("SLChat");
            stage.show();
        } catch (IOException exc) {
            System.out.println("Exception thrown while switching main window to create window.");
            exc.printStackTrace();
        }
    }

    /**
     * Swithes to search server window, handled by
     * {@link client.view.search.SearchController}.
     *
     * @param event     User hit the button.
     */
    @FXML
    protected void handleSearchButtonAction(ActionEvent event) {
        mainScene = searchButton.getScene();
        mainView.changeWindow("/client/view/search/Search.fxml");
        getIP();
    }

    /**
     * Searches for server on address,
     * gained from users input.
     * <p>
     * If found one, runs client backend
     * ({@link client.model.Client})
     * and switches window to chat
     * window, contained in
     * {@link main.SLChat#CLIENT_GUI_PATH}.
     *
     * @param event  User pressed enter in this window.
     */
    @FXML
    protected void handleEnterNameFieldAction(ActionEvent event) {
        mainScene = enterName.getScene();
        if (enterName.getText().length() > 30) {
            mainView.alertWindow("Server name is too long", "Please enter a smaller name.");
        } else if (enterName.getText().isEmpty()) {
            enterSomething.setText("Please enter something!");
        } else {
            try {
                /*
                 * Attampting to start client with
                 * gained server address. If failed
                 * to start, {@link client.model.Client}
                 * will throw and handle an exception.
                 */
                Client.serverAddress = InetAddress.getByName(enterName.getText());
                enterName.setText("");
                getCredentials();
                /* Switching to client GUI. */
            } catch (UnknownHostException exc) {
                mainView.alertWindow("Wrong", "Server not found!");
            }
        }
    }

    private void getCredentials() {
            Parent root = new GridPane();
            Stage stage = new Stage();
        try {
            root = FXMLLoader.load(getClass().getResource(LOGIN_GUI_PATH));
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            /* setting new stage to be the stage owner */
            stage.initOwner((Stage) mainScene.getWindow());
            /* Make this stage frozen, while new stage is open. */
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle("SLChat");
            stage.show();
        } catch (IOException exc) {
            System.out.println("Exception thrown while switching main window to create window.");
            exc.printStackTrace();
        }
    }
}
