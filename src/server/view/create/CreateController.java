package server.view.create;

import client.model.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import server.model.Server;
import static main.SLChat.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
            IS_SERVER_RUNNING = true;

            // Opening client GUI;
            Parent root = new GridPane();
            try {
                root = FXMLLoader.load(getClass().getResource("/client/view/main/Main.fxml"));
            } catch (IOException exc) {
                System.out.println("Exception thrown while switching create window to chat window.");
                exc.printStackTrace();
            }
            primaryStage.setScene(new Scene(root));

            // Closing secondary window;
            Stage stage = (Stage) openButton.getScene().getWindow();
            stage.close();

            // Starting client;
            try {
                SLClient = new Client(InetAddress.getByName("localhost"));
                IS_CLIENT_RUNNING = true;
                SLClient.start();
            } catch (UnknownHostException exc) {
                System.out.println("Exception thrown, while CreateController attempted to start client.");
                IS_CLIENT_RUNNING = false;
                exc.printStackTrace();
            }
        }
    }
}
