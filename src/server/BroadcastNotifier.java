package server;

import com.sun.org.apache.bcel.internal.classfile.ConstantUtf8;
import com.sun.xml.internal.stream.writers.UTF8OutputStreamWriter;
import sun.text.normalizer.UnicodeSet;

import java.io.IOException;
import java.net.*;

/**
 * Sends broadcast messages to clients
 * in local area network;
 */
public class BroadcastNotifier extends Thread {
        // A group for sending broadcast bait packet;
        private InetAddress group = null;
        private DatagramPacket packet = null;
        private DatagramSocket socket = null;
        private static final long FIVE_SECONDS = 5000;
        // These ports are used to client and server to find each other;
        public final static int CLIENT_PORT = 4445;
        public final static int SERVER_PORT = 4444;
        // Tracking server state value. Shuts this tread when set to false;
        private boolean IS_RUNNING;
        // Broadcast message to identify server;
        private byte[] msg = null;

    public BroadcastNotifier(String message) {
        super("BroadcastNotifier");
        IS_RUNNING = true;
        try {
            group = InetAddress.getByName("230.0.0.1");
            socket = new DatagramSocket(SERVER_PORT);
            msg = new byte[message.length()];
            Server.toByte(message, this.msg);
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
