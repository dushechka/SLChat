package server;

import java.net.*;
import java.io.*;

/**
 * Listens for incoming client connections
 * and pass them to ClientHandler;
 */
public class ClientConnector extends Thread {
        public final static int SERVER_PORT = 4488;
        private boolean IS_RUNNING;
        private ServerSocket server = null;
        private final String ROOM_NAME;
        private final String PASSWORD;

    public ClientConnector(String roomName, String password) {
        super("ClientConnector");
        IS_RUNNING = true;
        this.ROOM_NAME = roomName;
        this.PASSWORD = password;
        try {
            server = new ServerSocket(SERVER_PORT);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        start();
    }

    @Override
    public void run() {
        try {
            while (IS_RUNNING) {
                Socket clientSocket = server.accept();
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        } finally {
            close();
        }
    }


    protected void die() {
        IS_RUNNING = false;
    }

    private void close(){
        try {
            server.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}
