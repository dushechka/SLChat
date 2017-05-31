package server.model;

import static java.lang.Thread.*;

/**
 * Chat's server class;
 */
public class Server {
        ClientNotifier cn = null;

    Server() {
        try {
            cn = new ClientNotifier();
            cn.start();
            cn.die();
            cn.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
