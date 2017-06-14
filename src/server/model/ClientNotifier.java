package server.model;

import static main.SLChat.localAddress;
import static server.model.ServerConstants.*;

import java.io.IOException;
import java.net.*;

/**
 * Notifies client about server's
 * presence on LAN.
 * <p>
 * Receives multicast packets
 * from client and then sends
 * datagram packet back in order
 * for client to know server's address;
 */
public class ClientNotifier extends Thread {
        /* Determines when to stop this thread. */
        private boolean IS_RUNNING;
        /* Socket which, recieves clients multicast packet. */
        private MulticastSocket mSocket = null;
        /* Multicast group in which client sends packets. */
        private InetAddress group = null;
        private DatagramPacket packet = null;
        private byte[] packetData = null;
        private final String roomName;
        /* An interface's IP address, from which
           to listen and send packets. */
        private InetAddress inetAddress = null;

    /**
     *
     * @param roomName  The name of server's room, which
     *                  is represented to client.
     */
    ClientNotifier(String roomName) {
        super("ClientNotifier");
        this.roomName = roomName;
        try {
            System.out.println("Starting ClientNotifier");
//            inetAddress = getInterface("eth3");
//            mSocket = new MulticastSocket(new InetSocketAddress(inetAddress, SERVER_MULTI_PORT));
            mSocket = new MulticastSocket(SERVER_MULTI_PORT);
            group = InetAddress.getByName(GROUP_ADDRESS);
            mSocket.joinGroup(group);
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
            while(IS_RUNNING) {
                /* Receiving multicast packets from clients. */
                packetData = new byte[32];
                packet = new DatagramPacket(packetData, packetData.length);
                mSocket.receive(packet);
                packetData = packet.getData();
                if (byteToString(packetData).equals(SERVER_STRING)) {
                    System.out.println("Multicast packet recieved from client.");
                    System.out.println("Clients address: " + packet.getAddress());
                    /* Making a packet, containing room name and server address info */
                    stringToByte((SERVER_STRING + roomName), packetData);
                    packet = new DatagramPacket(packetData, packetData.length, packet.getAddress(), CLIENT_PORT);
                    /* Sending packet to corresponding client's method (main.SLChat#getIP()). */
                    mSocket.send(packet);
                    System.out.println("Back datagram packet sent to client;");
                } else {
                    System.out.println("Unknown packet intercepted.");
                    System.out.println("Sender's address: " + packet.getAddress());
                    System.out.println("Packet data: " + byteToString(packetData));
                }
                sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Exception thrown in run() method.");
            System.out.println("Thread " + getName());
            e.printStackTrace();
        } finally {
            close();
        }
    }

    /* Killing this thread */
    void die() {
        IS_RUNNING = false;
        /* Sending dummy packet to overcome "while" cycle in method run() */
        System.out.println("Stopping ClientNotifier;");
        try ( DatagramSocket dummySocket = new DatagramSocket(4455); ) {
            DatagramPacket dummyPacket = new DatagramPacket(packetData, packetData.length,
                                                            localAddress, SERVER_MULTI_PORT);
            dummySocket.send(dummyPacket);
        } catch (IOException exc) {
            System.out.println("Exception thrown while trying to release kill the thread.");
            System.out.println("Thread " + getName());
            exc.printStackTrace();
        }
    }

    /* Releasing used resources */
    private void close() {
        if ((mSocket != null) && (!mSocket.isClosed())) {
            this.mSocket.close();
        }
        System.out.println("ClientNotifier stoped.");
    }
}
