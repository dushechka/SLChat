package client.view.start;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;

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
        @FXML private TextField enterName;
        /* Text, intended to warn user, if he/she entered wrong values. */
        @FXML private Text enterSomething;

    /**
     * Opens new window for
     * server creation, handled by
     * {@link server.view.create.CreateController}.
     *
     * @param event  User pressed "Start" button.
     */
    @FXML
    protected void handleStartButtonAction(ActionEvent event) {
        try {
            mainView.openNewWindow(CREATE_GUI_PATH);
        } catch (IOException exc) {
            System.out.println("Exception thrown while switching main window to create window.");
            exc.printStackTrace();
        }
    }

    /**
     * Swithes to search server window, handled by
     * {@link client.view.search.SearchController}.
     *
     * @param event     User pushed the "search" button.
     */
    @FXML
    protected void handleSearchButtonAction(ActionEvent event) {
            ListView<String> roomsList = null;
            ObservableList<String> roomNames = FXCollections.observableArrayList();
        getIP();
        mainView.changeWindow(SEARCH_GUI_PATH);
        /* getting a link for the ListView node on just loaded root */
        for (Node n : primaryStage.getScene().getRoot().getChildrenUnmodifiable()) {
            if(n.getClass().toString().contains("ListView")) {
                roomsList = (ListView<String>) n;
            }
        }
        /* checking, whether we got a link to ListView */
        if (roomsList != null) {
            /* checking, whether open rooms were found on LAN */
            if (rooms.size() > 0) {
                /* setting found rooms names in ListView */
                for (String name : rooms.keySet()) {
                    System.out.println("Found room: " + name);
                    roomNames.add(name);
                    roomsList.setItems(roomNames);
                }
            } else {
                /* if none of them were found */
                roomsList.getItems().setAll("No open rooms were found.");
            }
        }
    }

    /**
     * Switches GUI to search window,
     * where further search for server
     * operation is handled.
     *
     * @see StartController
     * @param event  User pressed enter in this field.
     */
    @FXML
    protected void handleEnterNameFieldAction(ActionEvent event) {
        String serverAddress = enterName.getText();
        enterName.setText("");
        if (serverAddress.length() > 30) {
            mainView.alertWindow("Server name is too long", "Please enter a smaller name.");
        } else if (serverAddress.isEmpty()) {
            enterSomething.setText("Please enter something!");
        } else {
            connectClient(serverAddress);
        }
    }

}
