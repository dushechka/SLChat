package server;

import java.io.IOException;
import java.net.*;
import static server.ServerConstants.*;

/**
 * Sends broadcast messages to clients
 * in local area network;
 */
class BroadcastNotifier extends Thread {
        // A group for sending broadcast bait packet;
        private InetAddress group = null;
        private DatagramPacket packet = null;
        private DatagramSocket socket = null;
        // Tracking server state value. Shuts this tread when set to false;
        private boolean IS_RUNNING;
        // Broadcast message to identify server;
        private byte[] msg = null;

    BroadcastNotifier(String message) {
        super("BroadcastNotifier");
        IS_RUNNING = true;
        try {
            group = InetAddress.getByName("230.0.0.1");
            socket = new DatagramSocket(SERVER_PORT);
            msg = new byte[message.length()];
            stringToByte(message, this.msg);
            packet = new DatagramPacket(msg, msg.length, group, CLIENT_PORT);
        } catch (IOException exc) {
            System.out.println("An IOException thrown in BroadcastNotifier constructor;");
            exc.printStackTrace();
        }
        start();
    }

    @Override
    public void run() {
        try {
            while(IS_RUNNING) {
                // Sending broadcast bait packet;
                socket.send(packet);
                try {
                    sleep((long) Math.random() * FIVE_SECONDS);
                } catch (InterruptedException exc) {
                    exc.printStackTrace();
                }
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        } finally {
            close();
        }
    }

    protected void die() {
        IS_RUNNING = false;
    }

    private void close() {
        socket.close();
    }
}
