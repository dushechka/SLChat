package server.model;

import static main.SLChat.*;

/**
 * Chat's server class.
 * <p>
 * Handles all server's
 * backend functional.
 */
public class Server extends Thread {
        ClientNotifier clientNotifier = null;
        ClientConnector clientConnector = null;
        private final String roomName;
        private final String password;

    public Server(String roomName, String password) {
        super("SLServer");
        this.roomName = roomName;
        this.password = password;
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
        clientConnector = new ClientConnector();
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
