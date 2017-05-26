package server;

import java.net.*;

/**
 * Handles client's connections;
 */
public class ClientHandler extends Thread {
        private Socket socket = null;

    public ClientHandler(Socket socket) {
        super("ClientHandler");
        this.socket = socket;
    }
}
