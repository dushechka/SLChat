package client.model;

import java.io.IOException;
import java.net.*;

import static server.model.ServerConstants.*;

/**
 * Sends Multicast packets in order to
 * find server's IP address;
 */
public class Seeker extends Thread {
    // Determines whether this thread is alive;
    private boolean IS_RUNNING;
    // A group for sending broadcast packet;
    private InetAddress group = null;
    private DatagramPacket datagramPacket = null;
    // Interface's address, from which send packets;
    private InetAddress socketAddress = null;
    // Broadcast message to identify client;
    private byte[] msg = null;

    public Seeker() {
        super("Seeker");
        try {
            group = InetAddress.getByName(GROUP_ADDRESS);
            msg = new byte[SERVER_STRING.length()];
            stringToByte(SERVER_STRING, msg);
            datagramPacket = new DatagramPacket(msg, msg.length, group, SERVER_MULTI_PORT);
            socketAddress = getInterface();
        } catch (UnknownHostException | SocketException e) {
            System.out.println("Exception thrown while trying to achieve multicast group and IF address.");
            System.out.println("Thread " + getName());
            e.printStackTrace();
        }
    }

    public void run() {
            int i = 0;
        IS_RUNNING = true;
        System.out.println("Seeker started.");
        // Sends multicast packet's on LAN;
        try (DatagramSocket datagramSocket = new DatagramSocket(CLIENT_MULTICAST_PORT, socketAddress);){
            while(IS_RUNNING) {
                i++;
                // Sending broadcast packet;
                datagramSocket.send(datagramPacket);
                System.out.println("Packet send from: " + socketAddress);
                sleep(1000);
                // Stop if server's not responding;
                if (i == 5) {
                    msg = new byte[TIME_HAS_EXPIRED.length()];
                    stringToByte(TIME_HAS_EXPIRED, msg);
                    datagramPacket = new DatagramPacket(msg, msg.length, socketAddress, CLIENT_PORT);
                    datagramSocket.send(datagramPacket);
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Exception thrown while sending packets in run()");
            System.out.println("Thread " + getName());
            e.printStackTrace();
        } finally {
            System.out.println("Seeker stopped.");
        }
    }

    // Killing this thread;
    public void die() {
        IS_RUNNING = false;
    }

    // Get interface address, from which send packets;
    public static InetAddress getInterface() throws SocketException {
        NetworkInterface nif = NetworkInterface.getByName("eth3");
        InetAddress socketAddress = nif.getInetAddresses().nextElement();
        return socketAddress;
    }
}