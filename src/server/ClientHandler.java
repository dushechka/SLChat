package server;

import java.io.IOException;
import java.net.*;

/**
 * Handles client's connections;
 */
class ClientHandler extends Thread {
        private Socket socket = null;
        // A particular server (ClientHandler thread) name;
        private String CURRENT_ROOM_NAME;
        // Password for particular server;
        private String PASSWORD;

    ClientHandler(Socket socket) {
        super("ClientHandler");
        try {
            this.socket = socket;
            System.out.println("Connection established.");
        } finally {
            close();
        }
    }

    private void close() {
        try {
            this.socket.close();
        } catch (IOException exc) {
            System.out.println("Exception thrown while closing socket in ClientHandler;");
            exc.printStackTrace();
        }
    }

}
