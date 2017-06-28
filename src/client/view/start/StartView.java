package client.view.start;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import static main.SLChat.*;

/**
 * Main menu view.
 */
public class StartView extends Application {

    /**
     * Opens main GUI window.
     * <p>
     * Creates main window and saves itself
     * in static {@link main.SLChat#mainView} field.
     * Also creates and saves primary stage for
     * whole program in {@link main.SLChat#primaryStage}.
     * Further, loads main window content and sets
     * onClose operation for main window to close
     * client and determines from user respond, whether
     * it should stop server on exit, if it's running.
     *
     * @param stage  main javafx stage instance.
     * @throws IOException  when FXMLLoader can't find
     *                      fxml file with root <b>Pane</b>.
     */
    @Override
    public void start(Stage stage) throws IOException {
        /* setting static field that all classes can invoke it */
        mainView = this;
//        stage.initStyle(StageStyle.UNDECORATED);
        /* setting main stage too */
        primaryStage = stage;
        /* loading GUI window */
        Parent root = FXMLLoader.load(getClass().getResource(START_GUI_PATH));
        Scene scene = new Scene(root);

        /* What to do, when closing program? */
        stage.setOnCloseRequest(e -> {
            try {
                if (!IS_SERVER_RUNNING) {
                    primaryStage.hide();
                    /* killing client thread */
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
                if (IS_CLIENT_RUNNING) {
                        SLClient.die();
                        SLClient.join();
                }
                Properties properties = getProperties();
                properties.setProperty("isRunning", "false");
                storeProperties(properties);
            } catch (InterruptedException ie) {
                System.out.println("Oh no! Client seems not want to stop! :'(");
                ie.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        });

        /* creating and showing main menu wiwndow */
        stage.setTitle("SLChat");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Changes scene on {@link main.SLChat#primaryStage}.
     * <p>
     * Is invoked from other view classes,
     * which need to change scene at
     * {@link main.SLChat#mainView}.
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
     * Changes GUI to chat
     * window.
     *
     * @see main.SLChat#CLIENT_GUI_PATH
     */
    public void changeOnChat() {
        changeWindow(CLIENT_GUI_PATH);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(240);
        primaryStage.setMinHeight(478);
    }

    /** Gets the link of chat window
     *  {@link TextArea} and saves it
     *  in {@link main.SLChat#SLClient}
     *  instance field.
     */
    public void bindTextArea() {
            ObservableList<Node> nodes = primaryStage.getScene().getRoot().getChildrenUnmodifiable();
        for (Node node : nodes) {
            if (node.getClass().toString().contains("TextArea")) {
                SLClient.setTextArea((TextArea) node);
            } else if (node.getClass().toString().contains("GridPane")) {
                GridPane gridPane = (GridPane) node;
                Node nd = gridPane.getChildren().get(1);
                if (nd.getClass().toString().contains("TextField")) {
                    SLClient.setTextField((TextField) nd);
                    SLClient.getTextField().requestFocus();
                }
            }
        }
    }

    /**
     * Displays alert window.
     *
     * @param title    title of an alert window
     * @param content  a string to be displayed
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
     * previous window and holds
     * focus, until closed.
     *
     * @param path  A path to new window's
     *              GUI fxml file.
     * @throws IOException  When cannot find
     *                      GUI fxml file by
     *                      given path param.
     */
    public void openNewWindow(String path) throws IOException {
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

}
