package server.model;

import java.util.ArrayList;

import static main.SLChat.*;

/**
 * Chat's server class.
 * <p>
 * Handles all server's
 * backend functional.
 */
public class Server extends Thread {
        private ClientNotifier clientNotifier;
        private ClientConnector clientConnector;
        private final String roomName;
        private final String password;
        private ArrayList<ClientHandler> clients;

    public Server(String roomName, String password) {
        super("SLServer");
        this.roomName = roomName;
        this.password = password;
        clients = new ArrayList<>();
    }

    public void run() {
        startListening();
        IS_SERVER_RUNNING = true;
        System.out.println("Server started.");
        System.out.println("Room name is: " + roomName);
    }

    /**
     * Starts threads, which notify
     * clients about server's presence,
     * connect clients and handle those
     * connections.
     *
     * @see server.model.ClientNotifier
     * @see server.model.ClientConnector
     */
    private void startListening() {
        clientNotifier = new ClientNotifier(roomName);
        clientConnector = new ClientConnector(clients, password);
        clientNotifier.start();
        clientConnector.start();
    }

    /**
     * Closes ancillary threads
     * and waits, until they close.
     */
    public void close() {
        try {
            clientNotifier.die();
            clientConnector.die();
            clientNotifier.join();
            clientConnector.join();
            IS_SERVER_RUNNING = false;
        } catch (InterruptedException e) {
            System.out.println("Exception thrown while server was closing secondary threads.");
            e.printStackTrace();
        }
    }
}
