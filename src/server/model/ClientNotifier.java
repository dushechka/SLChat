package server.model;

import static server.model.ServerConstants.*;

import java.io.IOException;
import java.net.*;

/**
 * Recieves multicast packets from client and then sends
 * datagram packet back in order for client to know server's address;
 */
public class ClientNotifier extends Thread {
        // Determines whether this thread is alive;
        private boolean IS_RUNNING;
        // Socket which recieves clients multicast packet;
        private MulticastSocket mSocket = null;
        // Multicast group in which client sends packets;
        private InetAddress group = null;
        private DatagramPacket packet = null;
        private byte[] packetData = null;

    ClientNotifier() {
        super("ClientNotifier");
        try {
            mSocket = new MulticastSocket(SERVER_PORT);
            group = InetAddress.getByName(GROUP_ADDRESS);
            mSocket.joinGroup(group);
            packetData = new byte[8];
            packet = new DatagramPacket(packetData, packetData.length);
        } catch (IOException exc) {
            System.out.println("Exception thrown while initializing resources in constructor.");
            System.out.println("Thread " + getName());
            exc.printStackTrace();
        }
    }

    public void run() {
        IS_RUNNING = true;
        System.out.println("ClientNotifier started.");
        try {
            mSocket.receive(packet);
            System.out.println("Client's multicast packet recieved.");
        } catch (IOException e) {
            System.out.println("Exception thrown in run() method.");
            System.out.println("Thread " + getName());
            e.printStackTrace();
        } finally {
            close();
        }
    }

    // Killing this thread;
    void die() {
        IS_RUNNING = false;
    }

    private void close() {
        if ((mSocket != null) && (!mSocket.isClosed())) {
            this.mSocket.close();
        }
        System.out.println("ClientNotifier stoped.");
    }
}
