package main;

import client.view.start.StartView;
import javafx.stage.Stage;
import server.model.Server;

/**
 * Main entrance to the whole programm;
 */
public class SLChat {
        public static boolean IS_SERVER_RUNNING;
        public static Server SLServer = null;
        public static Stage primaryStage = null;

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
}
