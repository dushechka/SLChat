package server.view.create;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateController {
    @FXML private Button openButton;
    @FXML private Text errorLine;

    @FXML
    protected void handleOpenButtonAction(ActionEvent event) {
        errorLine.setText("Please fill the fields above!");
    }
}
