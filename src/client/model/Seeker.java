package client.model;

import java.io.IOException;
import java.net.*;

import static server.model.ServerConstants.*;

/**
 * Sends Multicast packets in order to
 * find server's IP address;
 */
public class Seeker extends Thread {
    /* Determines when to stop this thread. */
    private boolean IS_RUNNING;
    private InetAddress group = null;
    private DatagramPacket datagramPacket = null;
    /* Interface's address, from which to send packets. */
    private InetAddress socketAddress = null;
    /* Broadcast message to identify client. */
    private byte[] msg = null;

    public Seeker() {
        super("Seeker");
        try {
            /* Getting multicast gtoup's IP-address */
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

    /**
     * Sending multicast packets on LAN
     * in order to find server.
     * <p>
     * Starts sending multicast packets
     * at given group addres
     * {@link server.model.ServerConstants#GROUP_ADDRESS}
     * every second, until it gets a signal
     * to stop from outside, by invoking
     * {@link #die()} method.
     */
    public void run() {
            int i = 0;
        IS_RUNNING = true;
        System.out.println("Seeker started.");
        /* Sending multicast packet's on LAN */
        try (DatagramSocket datagramSocket = new DatagramSocket(CLIENT_MULTICAST_PORT, socketAddress)){
            while(IS_RUNNING) {
                i++;
                /* Sending broadcast packet */
                datagramSocket.send(datagramPacket);
                System.out.println("Packet send from: " + socketAddress);
                sleep(1000);
                /* Stop if server's not responding */
                if (i == 3) {
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

    /**
     * Gets interface address, from
     * which to send packets.
     *
     * @return  {@link InetAddress} from
     *          which to send packets.
     * @throws SocketException  When cannot get
     *                          address by name.
     */
    public static InetAddress getInterface() throws SocketException {
        NetworkInterface nif = NetworkInterface.getByName("eth3");
        InetAddress socketAddress = nif.getInetAddresses().nextElement();
        return socketAddress;
    }

    /**
     * Kills it's thread.
     */
    public void die() {
        IS_RUNNING = false;
    }
}