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
    private DatagramSocket datagramSocket = null;
    private DatagramPacket datagramPacket = null;
    // Broadcast message to identify client;
    private byte[] msg = null;

    Seeker() {
        super("Seeker");
        try {
            group = InetAddress.getByName(GROUP_ADDRESS);
            datagramSocket = new DatagramSocket(CLIENT_MULTICAST_PORT);
            msg = new byte[SERVER_STRING.length()];
            stringToByte(SERVER_STRING, msg);
            datagramPacket = new DatagramPacket(msg, msg.length, group, SERVER_MULTI_PORT);
        } catch (UnknownHostException e) {
            System.out.println("Exception thrown while trying to achieve multicast group.");
            System.out.println("Thread " + getName());
            e.printStackTrace();
        } catch (SocketException e) {
            System.out.println("Exception thrown while trying to open datagram socket.");
            System.out.println("Thread " + getName());
            e.printStackTrace();
        }
    }

    public void run() {
        IS_RUNNING = true;
        System.out.println("Seeker started.");
        distribute();
    }

    // Sends multicast packet's on LAN;
    private void distribute() {
        try {
            while(IS_RUNNING) {
                // Sending broadcast packet;
                datagramSocket.send(datagramPacket);
                    sleep((long) Math.random() * FIVE_SECONDS);
                }
        } catch (IOException | InterruptedException e) {
            System.out.println("Exception thrown while sending packets in run()");
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

    // Releasing occupied resources;
    private void close() {
        if ((datagramSocket != null) && (!datagramSocket.isClosed())) {
            datagramSocket.close();
        }
        System.out.println("Seeker stoped.");
    }
}