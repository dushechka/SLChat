package server.model;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

import static server.model.ServerConstants.*;

/**
 * Listens for incoming client
 * connections and pass them to
 * ClientHandler if connection
 * was established.
 */
public class ClientConnector extends Thread {
    /** Determines, when to stop this thread */
        private boolean IS_RUNNING;
    /** A server socket for establishing final connection with client */
        private ServerSocket serverSocket;
    /** A socket to transmit new client's connections to {@link ClientHandler} */
        private Socket clientSocket;
    /** A list of connected clients to work with */
        private ArrayList<ClientHandler> clients;
    /** A new connected client to add to {@link #clients} */
        private ClientHandler clientHandler;
        private final String password;

    ClientConnector(ArrayList<ClientHandler> clients, String password) {
        super("ClientConnector");
        this.password = password;
        this.clients = clients;
        try {
            serverSocket = new ServerSocket(SERVER_FINAL_PORT);
//            serverSocket = new ServerSocket(SERVER_FINAL_PORT, 10, getInterface("eth3"));
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
                /* Establishing connection with client. */
                clientSocket = serverSocket.accept();
                clientHandler = new ClientHandler(clientSocket, password);
                clients.add(clientHandler);
                clientHandler.start();
                System.out.println("Client connected.");
            }
        } catch (IOException e) {
            System.out.println("Exception thrown in run() method.");
            System.out.println("Thread " + getName());
        }
        close();
    }

    /**
     * Kills this thread.
     */
    void die() {
        IS_RUNNING = false;
        try ( Socket dummy = new Socket("localhost", SERVER_FINAL_PORT); ) {
            /* Socket made to overcome "while" cycle in run() method */
        } catch (IOException e) {
            System.out.println("Exception thrown while trying to kill thread.");
            System.out.println("Thread " + getName());
            e.printStackTrace();
        }
    }

    /**
     * Releases used resources.
     */
    private void close() {
        try {
            if ((serverSocket != null) && (!serverSocket.isClosed())) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Exception thrown while trying to release resources.");
            System.out.println("Thread " + getName());
        }
        System.out.println("ClientConnector stopped.");
    }
}
