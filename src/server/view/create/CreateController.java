package server.view.create;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import server.model.Server;

import java.io.IOException;
import java.net.InetAddress;

import static main.SLChat.*;

public class CreateController {
    /* Invokes creation process */
        @FXML private Button openButton;
    /* Shows up if text fields were not filled properly */
        @FXML private Text errorLine;
    /* Field for input a room name of server to be created */
        @FXML private TextField roomName;
    /* Field for server's password, which is used to authenticate users */
        @FXML private TextField password;

    /**
     * Starts server backend thread
     * {@link server.model.Server}.
     * <p>
     * Takes data, entered by user
     * in fields {@link #roomName}
     * and {@link #password}, then
     * starts server backend thread
     * {@link server.model.Server}
     * if entered data is correct.
     *
     * @param event  User hits openButton.
     */
    @FXML
    protected void handleOpenButtonAction(ActionEvent event) {
        if (roomName.getText().isEmpty()) {
            errorLine.setText("Please enter your room name!");
        /* Server name needs to be not more
         * than 26 symbols, so it can fit in
         * server's packet message (array of
         * byte with length of 32).
         */
        } else if (roomName.getText().length() > 30) {
            errorLine.setText("Room name is too long!");
        } else {
            /* starting server */
            Server server = new Server(roomName.getText().trim(), password.getText().trim());
            server.start();

            /* Saving server's thread link
             * in public static field to
             * gain access anytime it needs.
             */
            SLServer = server;

            /* opening client GUI */
            mainView.changeWindow(clientGUIPath);
            /* closing secondary window */
            Stage stage = (Stage) openButton.getScene().getWindow();
            stage.close();
            try {
                startClient(InetAddress.getByName("localhost"), "");
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        }
    }
}
