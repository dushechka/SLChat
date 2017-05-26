package server;

import java.net.*;
import java.io.*;

/**
 * Listens for incoming client connections
 * and pass them to ClientHandler;
 */
public class ClientConnector extends Thread {
        // A server port for establishing final connection with client;
        public final static int SERVER_PORT = 4488;
        // Tracking server state value. Shuts this tread when set to false;
        private boolean IS_RUNNING;
        // A server socket for establishing final connection with client;
        private ServerSocket server = null;

    public ClientConnector() {
        super("ClientConnector");
        IS_RUNNING = true;
        try {
            server = new ServerSocket(SERVER_PORT);
        } catch (IOException exc) {
            System.out.println("Exception thrown, while instating ServerSocket;");
            exc.printStackTrace();
        }
        start();
    }

    @Override
    public void run() {
        try {
            while (IS_RUNNING) {
                Socket clientSocket = server.accept();
                new ClientHandler(clientSocket);
            }
        } catch (IOException exc) {
            System.out.println("Exception thrown, while connecting client to ServerSocket;");
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
