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
        try {
            root = FXMLLoader.load(getClass().getResource("/server/view/create/Create.fxml"));
        } catch (IOException exc) {
            System.out.println("Exception thrown while switching main window to create window.");
            exc.printStackTrace();
        }
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        /** Setting new stage to be the stage owner */
        stage.initOwner((Stage) startButton.getScene().getWindow());
        /** Make this stage frozen, while new stage is open */
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("SLChat");
        stage.show();
    }

    /**
     * Swithes to search server window, handled by
     * {@link client.view.search.SearchController}.
     */
    @FXML
    protected void handleSearchButtonAction(ActionEvent event) {
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
     * {@link main.SLChat#clientGUIPath}.
     *
     * @param event  User pressed enter in this window.
     */
    @FXML
    protected void handleEnterNameFieldAction(ActionEvent event) {
        if (enterName.getText().length() > 32) {
            mainView.alertWindow("Server name is too long", "Please enter a smaller name.");
        } else if (enterName.getText().isEmpty()) {
            enterSomething.setText("Please enter something!");
        } else {
            String serverName = enterName.getText();
            enterName.setText("");

            /*
             * Attampting to start client with
             * gained server address. If failed
             * to start, {@link client.model.Client}
             * will throw and handle an exception.
             */
            try {
                startClient(serverName, "");
                /* Client might not already started. */
                Thread.sleep(1000);
            } catch (InterruptedException exc) {
                exc.printStackTrace();
            }
            /* If client could not start, assume that address is wrong. */
            if (!IS_CLIENT_RUNNING) {
                mainView.alertWindow("Wrong", "Server not found!");
            } else {
                /* Switchin to client GUI. */
                mainView.changeWindow(clientGUIPath);
            }
        }
    }
}
