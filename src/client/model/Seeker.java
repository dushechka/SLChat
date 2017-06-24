package client.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static server.model.ServerConstants.*;

/**
 * Sends Multicast packets to
 * specific multicast group
 * so, server could recognize,
 * at which IP the client is located.
 *
 * @see server.model.ServerConstants#GROUP_ADDRESS
 */
public class Seeker extends Thread {
    /** Determines when to stop this thread. */
    private boolean IS_RUNNING;
    /** Determines on which network interface to send packets */
    private InetAddress iFaceAddress;
    private InetAddress group = null;
    private DatagramPacket datagramPacket = null;
    private final int PORT;

    public Seeker(InetAddress iFaceAddress, int number) {
        super("Seeker_" + number);
        PORT = CLIENT_MULTICAST_PORT + number;
        printMessage("Taken port is: " + PORT);
        this.iFaceAddress = iFaceAddress;
        IS_RUNNING = true;
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
     * every second, for a given
     * period of time will pass.
     */
    public void run() {
            int i = 0;
        printMessage("Started.");
        /* Sending multicast packet's on LAN */
        try (DatagramSocket datagramSocket = new DatagramSocket(PORT, iFaceAddress)) {
            while (IS_RUNNING) {
                i++;
                /* Sending broadcast packet */
                datagramSocket.send(datagramPacket);
                printMessage("Packet sent from: " + iFaceAddress);
                sleep(100);
                /* stop if server's not responding */
            }
        } catch (IOException | InterruptedException e) {
            /* Sending back packet, that program can stop
               tyring to receive packet from server, if
               something went wrong */
            printMessage("Exception thrown while sending packets in run()");
            e.printStackTrace();
        } finally {
            printMessage("Stopped.");
        }
    }

    /**
     * Kills this class instance thread.
     */
    public void die() {
        IS_RUNNING = false;
    }

    private void printMessage(String message) {
        System.out.println(getName() + ": " + message);
    }

    public boolean getStatus() {
        return IS_RUNNING;
    }
}