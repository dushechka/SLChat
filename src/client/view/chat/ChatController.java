package client.view.chat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.LocalTime;

import static main.SLChat.SLClient;
import static main.SLChat.IS_CLIENT_RUNNING;
import static main.SLChat.history;

/**
 * Handles chat window operations.
 */
public class ChatController {
    @FXML TextField textField;
    @FXML TextArea textArea;

    @FXML
    protected void handleSendAction(ActionEvent event) {
        textField.requestFocus();
        try {
            String msg = "";
            msg = textField.getText();
            if (IS_CLIENT_RUNNING && !msg.isEmpty()) {
                textField.setText("");
                textArea.appendText(msg + "\n");
                SLClient.sendMessage(msg);
                /* saving message to history log file*/
                if (history != null) {
                    history.println(LocalTime.now() + "\t" + msg);
                }
            }
        } catch (IOException exc) {
            System.out.println("Can't send message!");
            exc.printStackTrace();
        }
    }
}
