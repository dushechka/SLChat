package client.view.start;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import static main.SLChat.*;

import java.io.IOException;
import java.util.Optional;

/**
 * Main menu view;
 */
public class StartView extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("Start.fxml"));
        Scene scene = new Scene(root);

        // What to do, when closing program?
        stage.setOnCloseRequest(e -> {
            if (!IS_SERVER_RUNNING) {
                primaryStage.hide();
            }
            try {
                // Killing client on exit;
                if (IS_CLIENT_RUNNING) {
                    SLClient.die();
                }
                // Killing server on exit?;
                if (IS_SERVER_RUNNING) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    ObservableList<ButtonType> buttonTypes = alert.getButtonTypes();
                    buttonTypes.setAll(ButtonType.YES, ButtonType.NO);
                    alert.setTitle("Closing SLChat:");
                    alert.setHeaderText("Your chat room is still open.");
                    alert.setContentText("Do you want to close it?");
                    Optional<ButtonType> result = alert.showAndWait();
                    primaryStage.hide();
                    // If choosed to stop server;
                    if (result.get() == ButtonType.YES) {
                        SLServer.close();
                        SLServer.join();
                        System.out.println("SLServer stopped.");
//                        Alert serverClosed = new Alert(Alert.AlertType.INFORMATION);
//                        serverClosed.setTitle("Exiting");
//                        serverClosed.setHeaderText(null);
//                        serverClosed.setContentText("Your SLChat room has been closed.");
//                        serverClosed.showAndWait();
                    }
                }
                if (SLClient != null && SLClient.isAlive()) {
                    SLClient.join();
                }
            } catch (InterruptedException ie) {
                System.out.println("Oh no! Client seem doesn't whant to stop! :'(");
                ie.printStackTrace();
            }
        });

        stage.setTitle("SLChat");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
