package server.view.create;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateController {
    @FXML private Button openButton;
    @FXML private Button backButton;

    @FXML
    protected void handleOpenButtonAction(ActionEvent event) {
//        new Server("SRV","TalkDirtyToMe");
    }

    // Switching to main menu;
    @FXML
    private void handleBackButtonAction(ActionEvent event) {
            Parent root = new GridPane();
        try {
            root = FXMLLoader.load(getClass().getResource("/client/view/start/Start.fxml"));
        } catch (IOException exc) {
            System.out.println("Exception thrown while switching to main window from create.");
            exc.printStackTrace();
        }
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}
