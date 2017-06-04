package main;

import client.model.Client;
import client.view.start.StartView;
import javafx.stage.Stage;
import server.model.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Main entrance to the whole programm;
 */
public class SLChat {
        public static boolean IS_SERVER_RUNNING;
        public static boolean IS_CLIENT_RUNNING;
    // Server thread;
        public static Server SLServer = null;
    // Client thread;
        public static Client SLClient = null;
    // Main stage;
        public static Stage primaryStage = null;
    // Main view;
        public static StartView mainView = null;
    // Client's GUI fxml file path;
        public static String clientGUIPath = "/client/view/main/Main.fxml";

    SLChat() {
        IS_SERVER_RUNNING = false;
    }

    public static void main(String[] args) {
        // Opening start window;
        new Thread() {
            @Override
            public void run() {
                javafx.application.Application.launch(StartView.class);
            }
        }.start();
    }

    public static void startClient(String serverAddress) {
        // Starting client;
            SLClient = new Client(serverAddress);
            SLClient.start();
    }
}
