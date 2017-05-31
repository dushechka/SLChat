package server.model;

import static java.lang.Thread.*;

/**
 * Chat's server class;
 */
public class Server {
        ClientNotifier clientNotifier = null;
        ClientConnector clientConnector = null;

    Server() {
        startListening();
        try {
            sleep(10000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        close();
    }

    // Starts threads, which listens and handles clients connections;
    private void startListening() {
        clientNotifier = new ClientNotifier();
        clientConnector = new ClientConnector();
        clientNotifier.start();
        clientConnector.start();
    }

    private void close() {
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
        new Server();
    }
}
