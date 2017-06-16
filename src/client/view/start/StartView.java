package client.view.start;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import static main.SLChat.*;

import java.io.IOException;
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
        /* Setting this field too. */
        primaryStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("Start.fxml"));
        Scene scene = new Scene(root);

        /* What to do, when closing program? */
        stage.setOnCloseRequest(e -> {
            if (!IS_SERVER_RUNNING) {
                primaryStage.hide();
            }
            try {
                /* Killing client on exit. */
                if (IS_CLIENT_RUNNING) {
                    SLClient.die();
                }
                /* Killing server on exit? */
                if (IS_SERVER_RUNNING) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
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
                if (SLClient != null && SLClient.isAlive()) {
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

    /**
     * Displays alert window.
     *
     * @param title    title of an alert window
     * @param content  a window to be displayed
     */
    public void alertWindow(String title, String content) {
        Alert nameAlert = new Alert(Alert.AlertType.INFORMATION);
        nameAlert.setTitle(title);
        nameAlert.setHeaderText(null);
        nameAlert.setContentText(content);
        nameAlert.showAndWait();
    }
}
