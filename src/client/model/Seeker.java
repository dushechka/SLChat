package client.model;

import java.io.IOException;
import java.net.*;

import static main.SLChat.*;
import static server.model.ServerConstants.*;

/**
 * Sends Multicast packets to
 * specific multicast group
 * so, the server could recognize,
 * at which IP the client is located.
 *
 * @see server.model.ServerConstants#GROUP_ADDRESS
 */
public class Seeker extends Thread {
    /* Determines when to stop this thread. */
    private boolean IS_RUNNING;
    private InetAddress group = null;
    private DatagramPacket datagramPacket = null;

    public Seeker() {
        super("Seeker");
        try {
            /* Getting multicast group IP address */
            group = InetAddress.getByName(GROUP_ADDRESS);
            datagramPacket = new DatagramPacket(SERVER_STRING, SERVER_STRING.length, group, SERVER_MULTI_PORT);
        } catch (UnknownHostException e) {
            printMessage("Exception thrown while trying to achieve multicast group and IP address.");
            e.printStackTrace();
        }
    }

    /**
     * Sends multicast packets on LAN
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
        printMessage("Started.");
        /* Sending multicast packet's on LAN */
        try (DatagramSocket datagramSocket = new DatagramSocket(CLIENT_MULTICAST_PORT, prefferedAddress)) {
            while (IS_RUNNING) {
                i++;
                /* Sending broadcast packet */
                datagramSocket.send(datagramPacket);
                printMessage("Packet send from: " + prefferedAddress);
                sleep(1000);
                /* stop if server's not responding */
                if (i == 3) {
                    datagramPacket = new DatagramPacket(TIME_HAS_EXPIRED, TIME_HAS_EXPIRED.length,
                                                                    prefferedAddress, CLIENT_PORT);
                    datagramSocket.send(datagramPacket);
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            printMessage("Exception thrown while sending packets in run()");
            e.printStackTrace();
        }
    }

    /**
     * Kills it's thread.
     */
    public void die() {
        IS_RUNNING = false;
    }

    private void printMessage(String message) {
        System.out.println(getName() + ": " + message);
    }
}