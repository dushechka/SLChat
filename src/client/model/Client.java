package client.model;

import static java.lang.Thread.*;

/**
 * Chat's client handling class;
 */
public class Client {
        // Multicast packets sender;
        MulticastInterviewer mi = null;

    Client () {
        establishConnection();
    }

    private void establishConnection() {
        try {
            mi = new MulticastInterviewer();
            mi.start();
            sleep(10000);
            mi.die();
            mi.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
