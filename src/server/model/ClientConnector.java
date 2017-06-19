package server.model;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

import static server.model.ServerConstants.*;
import static main.SLChat.*;

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
    /** An addition number to {@link ClientHandler} threads names */
        private int handlerNumber;

    ClientConnector(String password) {
        super("ClientConnector");
        handlerNumber = 0;
        this.clients = new ArrayList<>();
        this.password = password;
        try {
            serverSocket = new ServerSocket(SERVER_FINAL_PORT);
//            serverSocket = new ServerSocket(SERVER_FINAL_PORT, 10, getInterface("eth3"));
        } catch (IOException exc) {
            printMessage("Exception thrown while opening ServerSocket.");
            exc.printStackTrace();
        }
    }

    public void run() {
        IS_RUNNING = true;
        IS_CLIENT_CONNECTOR_RUNNING = true;
        printMessage("Started.");
        try {
            while (IS_RUNNING) {
                /* Establishing connection with client. */
                clientSocket = serverSocket.accept();
                clientHandler = new ClientHandler(clientSocket, password, this);
                handlerNumber++;
                clients.add(clientHandler);
                clientHandler.start();
                printMessage("Client connected.");
            }
        } catch (IOException e) {
            printMessage("Exception thrown in run() method.");
        }
        close();
    }

    /**
     * Kills this thread.
     */
    void die() {
        IS_RUNNING = false;
        IS_CLIENT_CONNECTOR_RUNNING = false;
        try ( Socket dummy = new Socket("localhost", SERVER_FINAL_PORT); ) {
            /* Socket made to overcome "while" cycle in run() method */
        } catch (IOException e) {
            printMessage("Exception thrown while trying to kill thread.");
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
            printMessage("Exception thrown while trying to release resources.");
        }
        System.out.println("ClientConnector stopped.");
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        printMessage("Client <" + client.getNickname() + "> removed.");
    }

    public boolean isNicknameUsed(String nickname) {
        for (ClientHandler ch : clients ) {
            if (ch.getNickname().equals(nickname)) {
                return true;
            }
        }
        return false;
    }

    private void printMessage(String message) {
        System.out.println(getName() + ": " + message);
    }

    int getHandlerNumber() {
        return handlerNumber;
    }
}
