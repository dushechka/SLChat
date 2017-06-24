package server.model;

import java.net.InetAddress;
import java.util.ArrayList;

import static main.SLChat.*;

/**
 * Chat's server class.
 * <p>
 * Runs and stops all threads,
 * that handle all server
 * operations and
 * interconnection with clients.
 */
public class Server extends Thread {
        private ClientNotifier clientNotifier;
        private ClientConnector clientConnector;
        private final String roomName;
        private final String password;
        private ArrayList<ClientNotifier> notifiers;

    public Server(String roomName, String password) {
        super("SLServer");
        this.roomName = roomName;
        this.password = password;
        notifiers = new ArrayList<>();
    }

    /** Main server's backend workflow. */
    public void run() {
        startListening();
        IS_SERVER_RUNNING = true;
        printMessage("Started.");
        printMessage("Room name is <" + roomName + ">");
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
        startNotifiers();
        clientConnector = new ClientConnector(password);
        clientConnector.start();
    }

    /**
     * Starting {@link ClientNotifier}s
     * on all system's running interfaces,
     * which support multicasting and saving
     * them to {@link #notifiers}.
     */
    private void startNotifiers() {
        for (InetAddress addr : mCastAddresses) {
            clientNotifier = new ClientNotifier(roomName, addr);
            clientNotifier.setDaemon(true);
            clientNotifier.start();
            notifiers.add(clientNotifier);
        }
    }

    /**
     * Closes ancillary threads
     * and waits, until they close.
     */
    public void close() {
        try {
            printMessage("Stopping...");
            clientConnector.die();
            for (ClientNotifier cn : notifiers) {
                System.out.println("Invoking die() method for " + cn.getIfaceAddress());
                cn.die();
            }
            clientConnector.join();
            IS_SERVER_RUNNING = false;
        } catch (InterruptedException e) {
            printMessage("Exception thrown, while closing secondary threads.");
            e.printStackTrace();
        }
        printMessage("Stopped.");
    }

    private void printMessage(String message) {
        System.out.println(getName() + ": " + message);
    }
}
