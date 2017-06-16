package client.view.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

import static client.model.Client.connectClient;
import static main.SLChat.CLIENT_GUI_PATH;
import static main.SLChat.mainView;

public class LoginController {
    @FXML TextField loginField;
    @FXML TextField passwordField;
    @FXML Text actionTarget;

    @FXML
    protected void handleConnectButtonAction(ActionEvent event) {
        if (loginField.getText().isEmpty()) {
            actionTarget.setText("Please enter your login!");
        } else {
            String login = loginField.getText();
            String password = passwordField.getText();
            loginField.setText("");
            passwordField.setText("");

            /* closing secondary window */
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.close();

            /* connectin client */
            try {
                if (connectClient(login, password)) {
                    mainView.changeWindow(CLIENT_GUI_PATH);
                } else {
                    System.out.println("Client could not start!");
                }
            } catch (IOException exc) {
                System.out.println("Client can't log on to server!");
            }
        }
    }
}
