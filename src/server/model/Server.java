package server.model;

import static main.SLChat.*;

/**
 * Chat's server class;
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
    }

    // Starts threads, which listens and handles clients connections;
    private void startListening() {
        clientNotifier = new ClientNotifier();
        clientConnector = new ClientConnector();
        clientNotifier.start();
        clientConnector.start();
    }

    public void close() {
        try {
            clientNotifier.die();
            clientConnector.die();
            clientNotifier.join();
            clientConnector.join();
        } catch (InterruptedException e) {
            System.out.println("Exception thrown while server was closing secondary threads.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server("SRV", "TalkDirtyToMe!");
    }
}
