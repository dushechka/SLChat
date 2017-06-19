package server.view.create;

import client.model.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import server.model.Server;

import java.io.IOException;
import java.net.InetAddress;

import static client.model.Client.connectClient;
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
           than 30 symbols, so it can fit in
           server's packet message. */
        } else if (roomName.getText().length() > 30) {
            errorLine.setText("Room name is too long!");
        } else {
            String room = roomName.getText();
            String pass = password.getText();

            /* starting server */
            SLServer = new Server(room, pass);
            SLServer.start();

            /* closing secondary window */
            Stage stage = ((Stage) (openButton.getScene().getWindow()));
            stage.close();

            /* starting client */
            try {
                Client.serverAddress = localAddress;
                /* waiting, while server runs */
                while (!IS_CLIENT_CONNECTOR_RUNNING) {
                    Thread.sleep(100);
                }
                if (connectClient("fucker", pass)) {
                    mainView.changeWindow(CLIENT_GUI_PATH);
                    mainView.bindTextArea();
                } else {
                    System.out.println("Client could not start!");
                }
            } catch (IOException exc) {
                System.out.println("Client can't log on to server!");
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
}