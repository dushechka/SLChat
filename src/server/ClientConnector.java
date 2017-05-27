package server;

import java.net.*;
import java.io.*;
import static server.ServerConstants.SERVER_FINAL_PORT;

/**
 * Listens for incoming client connections
 * and pass them to ClientHandler;
 */
class ClientConnector extends Thread {
        // Tracking server state value. Shuts this tread when set to false;
        private boolean IS_RUNNING;
        // A server socket for establishing final connection with client;
        private ServerSocket server = null;

    ClientConnector() {
        super("ClientConnector");
        IS_RUNNING = true;
        try {
            server = new ServerSocket(SERVER_FINAL_PORT);
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
