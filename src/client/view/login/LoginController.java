package client.view.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

import static client.model.Client.logInClient;
import static main.SLChat.CLIENT_GUI_PATH;
import static main.SLChat.mainView;
import static main.SLChat.primaryStage;

public class LoginController {
    @FXML TextField loginField;
    @FXML PasswordField passwordField;
    @FXML Text actionTarget;

    /**
     * Login in client to server,
     * and opens chat GUI, if success.
     *
     * @param event User pushed "Connect"
     *              button on Login.fxml GUI.
     */
    @FXML
    protected void handleConnectButtonAction(ActionEvent event) {
        if (loginField.getText().isEmpty()) {
            actionTarget.setText("Please enter your login!");
        } else {
            String login = loginField.getText();
            String password = passwordField.getText();
            passwordField.setText("");

            /* closing secondary window */
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.close();

            try {
                /* logging client in */
                if (logInClient(login, password)) {
                    /* opening chat window if success */
                    mainView.changeOnChat();
                    /* Client object should know, where to append received messages. */
                    mainView.bindTextArea();
                } else {
                    mainView.alertWindow("Wrong.", "Wrong password!");
                }
            } catch (IOException exc) {
                System.out.println("Client can't log on to server!");
                exc.printStackTrace();
            }
        }
    }
}
