package client.model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import static main.SLChat.IS_CLIENT_RUNNING;
import static server.model.ServerConstants.SERVER_FINAL_PORT;

/**
 * Chat's client backend.
 */
public class Client extends Thread {
    /* Server's address to establish connection with. */
    private final String serverAddress;
    // Determines when to stop this thread. */
    private boolean IS_RUNNING;

    public Client(String serverAddress) {
        super("SLClient");
        IS_RUNNING = false;
        IS_CLIENT_RUNNING = false;
        this.serverAddress = serverAddress;
    }

    /**
     * Establishes connection with
     * server, until signal to stop
     * is received from outside by
     * invoking {@link #die()}.
     */
    public void run() {
        /* Establishing connection with server */
        try (Socket socket = new Socket(InetAddress.getByName(serverAddress), SERVER_FINAL_PORT);) {
            IS_RUNNING = true;
            IS_CLIENT_RUNNING = true;
            System.out.println("Connection established.");
            while (IS_RUNNING) {
                try {
                    sleep(1000);
                    System.out.println("Client is working.");
                } catch (InterruptedException exc) {
                    exc.printStackTrace();
                }
            }
        } catch (IOException exc) {
            System.out.println("Server not found!");
        } finally {
            System.out.println("Client stopped.");
        }
    }

    public void die() {
        IS_RUNNING = false;
        close();
    }

    private void close() {
        System.out.println("Closing client;");
    }
}
