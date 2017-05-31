package server.model;

import java.io.IOException;
import java.net.*;
import static server.model.ServerConstants.*;

/**
 * Listens for incoming client connections
 * and pass them to ClientHandler;
 */
public class ClientConnector extends Thread {
    // Tracking server state value. Shuts this tread when set to false;
    private boolean IS_RUNNING;
    // A server socket for establishing final connection with client;
    private ServerSocket serverSocket = null;

    ClientConnector() {
        super("ClientConnector");
        try {
            serverSocket = new ServerSocket(SERVER_FINAL_PORT);
        } catch (IOException exc) {
            System.out.println("Exception thrown while opening ServerSocket.");
            System.out.println("Thread: " + getName());
            exc.printStackTrace();
        }
    }

    public void run() {
        IS_RUNNING = true;
        System.out.println("ClientConnector started.");
        try {
            while (IS_RUNNING) {
                serverSocket.accept();
                System.out.println("Client connected.");
            }
        } catch (IOException e) {
            System.out.println("Exception thrown in run() method.");
            System.out.println("Thread " + getName());
        }
        close();
    }

    // Killing this thread;
    void die() {
        IS_RUNNING = false;
        try ( Socket dummy = new Socket("localhost", SERVER_FINAL_PORT); ) {
            // Socket made to overcome while cycle in run() method;
        } catch (IOException e) {
            System.out.println("Exception thrown while trying to kill thread.");
            System.out.println("Thread " + getName());
            e.printStackTrace();
        }
    }

    // Release used resources;
    private void close() {
        try {
            if ((serverSocket != null) && (!serverSocket.isClosed())) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Exception thrown while trying to release resources.");
            System.out.println("Thread " + getName());
        }
        System.out.println("ClientConnector sopped.");
    }
}
