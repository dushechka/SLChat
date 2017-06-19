package server.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import static main.SLChat.localAddress;
import static server.model.ServerConstants.*;

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
    /** Determines when to stop this thread. */
        private boolean IS_RUNNING;
    /** Socket which, recieves clients multicast packet. */
        private MulticastSocket mSocket = null;
    /** Multicast group in which client sends packets. */
        private InetAddress group = null;
        private DatagramPacket packet = null;
        private byte[] packetData = null;
        private final String roomName;


    /**
     *
     * @param roomName  The name of server's room, which
     *                  is represented to client.
     */
    ClientNotifier(String roomName) {
        super("ClientNotifier");
        this.roomName = roomName;
        try {
            printMessage("Starting...");
//            inetAddress = getInterface("eth3");
//            mSocket = new MulticastSocket(new InetSocketAddress(prefferedAddress, SERVER_MULTI_PORT));
            mSocket = new MulticastSocket(SERVER_MULTI_PORT);
            group = InetAddress.getByName(GROUP_ADDRESS);
            mSocket.joinGroup(group);
        } catch (IOException exc) {
            printMessage("Exception thrown while initializing resources in constructor.");
            exc.printStackTrace();
        }
    }

    public void run() {
        IS_RUNNING = true;
        printMessage("Started.");
        try {
            while(IS_RUNNING) {
                /* Receiving multicast packets from clients. */
                packetData = new byte[64];
                packet = new DatagramPacket(packetData, packetData.length);
                mSocket.receive(packet);
                packetData = packet.getData();
                printMessage("Received packet <" + byteToString(packetData) + ">");
                if (byteToString(packetData).contains(byteToString(SERVER_STRING))) {
                    printMessage("Multicast packet recieved from client.");
                    printMessage("Clients address <" + packet.getAddress() + ">");
                    /* Making a packet, containing room name and server address info */
                    String msg = new String(byteToString(SERVER_STRING) + roomName);
                    packetData = stringToByte(msg);
                    packet = new DatagramPacket(packetData, packetData.length, packet.getAddress(), CLIENT_PORT);
                    /* Sending packet to corresponding client's method (main.SLChat#getIP()). */
                    mSocket.send(packet);
                    printMessage("Back datagram packet sent to client.");
                } else {
                    printMessage("Unknown packet intercepted.");
                    printMessage("Sender's address <" + packet.getAddress() + ">");
                    printMessage("Packet data <" + byteToString(packetData) + ">");
                }
                sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            printMessage("Exception thrown in run() method.");
            e.printStackTrace();
        } finally {
            close();
        }
    }

    /* Killing this thread */
    void die() {
        IS_RUNNING = false;
        /* Sending dummy packet to overcome "while" cycle in method run() */
        printMessage(": Stopping...");
        try ( DatagramSocket dummySocket = new DatagramSocket(4455); ) {
            DatagramPacket dummyPacket = new DatagramPacket(packetData, packetData.length,
                                                            localAddress, SERVER_MULTI_PORT);
            dummySocket.send(dummyPacket);
        } catch (IOException exc) {
            printMessage(": Exception thrown while trying to release kill the thread.");
            exc.printStackTrace();
        }
    }

    /* Releasing used resources */
    private void close() {
        if ((mSocket != null) && (!mSocket.isClosed())) {
            this.mSocket.close();
        }
        printMessage(": Stopped.");
    }

    private void printMessage(String message) {
        System.out.println(getName() + ": " + message);
    }
}
