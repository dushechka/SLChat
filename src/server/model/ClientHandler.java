package server.model;

import java.io.IOException;
import java.net.*;

/**
 * Handles client's connections;
 */
class ClientHandler extends Thread {
    // Tracking server state value. Shuts this tread when set to false;
        private boolean IS_RUNNING;
        private Socket socket = null;
    // A particular server (ClientHandler thread) name;
        private String CURRENT_ROOM_NAME;
    // Password for particular server;
        private String PASSWORD;
        private ListOfClients clients = null;

    ClientHandler(Socket socket, ListOfClients clients) {
        super("ClientHandler");
        this.IS_RUNNING = true;
        this.clients = clients;
        start();
    }

    @Override
    public void run() {
        try {
            this.socket = socket;
            System.out.println("Connection established.");
            while(IS_RUNNING) {
                Thread.sleep(100);
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } finally {
            close();
        }
    }

    private void close() {
        try {
            if ((socket != null) && (!socket.isClosed())) {
                this.socket.close();
            }
            System.out.println("Client disconnected.");
        } catch (IOException exc) {
            System.out.println("Exception thrown while closing socket in ClientHandler;");
            exc.printStackTrace();
        }
    }

    void die() {
        IS_RUNNING = false;
    }
}
