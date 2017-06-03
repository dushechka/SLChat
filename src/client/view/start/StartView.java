package client.view.start;

import javafx.application.Application;
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

        // Killing server on exit?;
        stage.setOnCloseRequest(e -> {
            if (IS_SERVER_RUNNING) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Closing SLChat:");
                alert.setHeaderText("Your chat room is still open.");
                alert.setContentText("Do you want ot close it?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    SLServer.close();
                    Alert serverClosed = new Alert(Alert.AlertType.INFORMATION);
                    serverClosed.setTitle("Exiting");
                    serverClosed.setHeaderText(null);
                    serverClosed.setContentText("Your SLChat room has been closed.");
                    serverClosed.showAndWait();
                }
            }
        });

        stage.setTitle("SLChat");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
