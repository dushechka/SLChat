package client.view.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import static client.model.Client.connectClient;

public class LoginController {
    @FXML TextField loginField;
    @FXML TextField passwordField;

    @FXML
    protected void handleConnectButtonAction(ActionEvent event) {
        if (loginField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            System.out.println("Enter something, you dirty hack!");
        } else {
            connectClient(loginField.getText(), passwordField.getText());
            loginField.setText("");
            passwordField.setText("");
        }
    }
}
