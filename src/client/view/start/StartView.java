package client.view.start;

import client.model.Client;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static main.SLChat.*;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * Main menu view;
 */
public class StartView extends Application {

    /**
     * Creates main window and saves itself
     * in static {@link main.SLChat#mainView} field.
     * Also creates and saves primary stage for
     * whole program in {@link main.SLChat#primaryStage}.
     * Further, loads main window content and sets
     * onClose operation for main window to close
     * client and determine with users respond, whether
     * it should stop server on exit.
     *
     * @param stage  main javafx stage instance
     * @throws IOException  when FXMLLoader can't find
     *                      .fxml file with root <b>Pane</b>
     */
    @Override
    public void start(Stage stage) throws IOException {
        /* Setting static field that all classes can invoke it. */
        mainView = this;
//        stage.initStyle(StageStyle.UNDECORATED);
        /* Setting main stage too. */
        primaryStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource(START_GUI_PATH));
        Scene scene = new Scene(root);

        /* What to do, when closing program? */
        stage.setOnCloseRequest(e -> {
            /* stopping client on exit */

            try {
                if (!IS_SERVER_RUNNING) {
                    primaryStage.hide();
                } else {
                    /* Killing server on exit? */
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//                    alert.initStyle(StageStyle.UTILITY);
                    ObservableList<ButtonType> buttonTypes = alert.getButtonTypes();
                    buttonTypes.setAll(ButtonType.YES, ButtonType.NO);
                    alert.setTitle("Closing SLChat:");
                    alert.setHeaderText("Your chat room is still open.");
                    alert.setContentText("Do you want to close it?");
                    Optional<ButtonType> result = alert.showAndWait();
                    primaryStage.hide();
                    /* If choosed to stop server */
                    if (result.get() == ButtonType.YES) {
                        SLServer.close();
                        SLServer.join();
                        System.out.println("SLServer stopped.");
                    }
                }
                /* KILLING CLIENT THREAD */
                if (IS_CLIENT_RUNNING) {
                    SLClient.die();
                    SLClient.join();
                }
            } catch (InterruptedException ie) {
                System.out.println("Oh no! Client seems not want to stop! :'(");
                ie.printStackTrace();
            }
        });

        /* Creating and showing main menu window. */
        stage.setTitle("SLChat");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Changes scene on window.
     * <p>
     *     Invoked from other view classes,
     *     which needs to change scene at
     *     {@link main.SLChat#mainView}.
     *
     * @param fxmlPath  a path to .fxml file,
     *                  containing root <b>Pane</b>
     */
    public void changeWindow(String fxmlPath) {
            Parent root = new GridPane();
        try {
            root = FXMLLoader.load(getClass().getResource(fxmlPath));
        } catch (IOException exc) {
            System.out.println("Exception thrown while switching to chat window.");
            exc.printStackTrace();
        }
        primaryStage.setScene(new Scene(root));
    }

    /** Gets the link of chat window
     *  {@link TextArea} and saves it
     *  in {@link main.SLChat#SLClient}
     *  instance field.
     */
    public void bindTextArea() {
            ObservableList<Node> nodes = primaryStage.getScene().getRoot().getChildrenUnmodifiable();
        for (Node node : nodes) {
            System.out.println(node.getClass());
            if (node.getClass().toString().contains("TextArea")) {
                SLClient.setTextArea((TextArea) node);
            }
        }
    }

    /**
     * Displays alert window.
     *
     * @param title    title of an alert window
     * @param content  a window to be displayed
     */
    public void alertWindow(String title, String content) {
        Alert nameAlert = new Alert(Alert.AlertType.INFORMATION);
//        nameAlert.initStyle(StageStyle.UTILITY);
        nameAlert.setTitle(title);
        nameAlert.setHeaderText(null);
        nameAlert.setContentText(content);
        nameAlert.showAndWait();
    }

    /**
     * Opens new window above the
     * previous window and handles
     * focus, until closed.
     *
     * @param path  A path to new window's
     *              GUI fxml file.
     * @throws IOException  When cannot find
     *                      GUI fxml file by
     *                      given path param.
     */
    public void openNewVindow(String path) throws IOException {
        Parent root = new GridPane();
        Stage stage = new Stage();
//        stage.initStyle(StageStyle.UNDECORATED);
        /* opening create room window */
            root = FXMLLoader.load(getClass().getResource(path));
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            /* setting new stage to be the stage owner */
            stage.initOwner((Stage) primaryStage.getScene().getWindow());
            /* make this stage frozen, while new stage is open */
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle("SLChat");
            stage.show();
    }

    public void connectClient(String serverAddress) {
        try {
            connectClient(InetAddress.getByName(serverAddress));
        } catch (UnknownHostException exc) {
            mainView.alertWindow("Wrong", "Server not found!");
        }
    }

    public void connectClient(InetAddress serverAddress) {
        try {
            /*
             * Attampting to start client with
             * gained server address. If failed
             * to start, {@link client.model.Client}
             * will throw and handle an exception.
             */
            if (Client.connectClient(serverAddress)) {
                mainView.openNewVindow(LOGIN_GUI_PATH);
            } else {
                mainView.alertWindow("Wrong.", "Server not found!");
            }
        } catch (IOException e) {
            mainView.alertWindow("Oops!", "Sorry... Can't connect to server. :'-(");
            System.out.println("Couldn't open login window.");
            e.printStackTrace();
        }
    }
}
