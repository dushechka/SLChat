package server.model;

import static java.lang.Thread.*;

/**
 * Chat's server class;
 */
public class Server {
        ClientNotifier cn = null;

    Server() {
        startClientNotifier();
        close();
    }

    private void startClientNotifier() {
        cn = new ClientNotifier();
        cn.start();
    }

    private void close() {
        try {
            cn.die();
            cn.join();
        } catch (InterruptedException e) {
            System.out.println("Exception thrown while server was closing secondary threads.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
