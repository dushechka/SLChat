package server.view.create;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import server.model.Server;
import static main.SLChat.*;

public class CreateController {
    @FXML private Button openButton;
    @FXML private Text errorLine;
    @FXML private TextField roomName;
    @FXML private TextField password;

    @FXML
    protected void handleOpenButtonAction(ActionEvent event) {
        if (roomName.getText() == null || roomName.getText().isEmpty()) {
            errorLine.setText("Please enter your room name!");
        } else {
            // Starting server;
            Server server = new Server(roomName.getText(), password.getText());
            server.start();
            SLServer = server;

            // Opening client GUI;
            mainView.openChatWindow(clientGUIPath);
            // Closing secondary window;
            Stage stage = (Stage) openButton.getScene().getWindow();
            stage.close();
            startClient("localhost");
        }
    }
}
