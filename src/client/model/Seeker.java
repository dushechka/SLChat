package client.model;

import main.SLChat;

import java.io.IOException;
import java.net.*;

import static main.SLChat.*;
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
    /** Represents amount of times this object sends
        multicast packets on LAN, interrupting for
        100 miliseconds after each time. */
    private final int WORKING_TIME = 1;
    private InetAddress group = null;
    private DatagramPacket datagramPacket = null;

    public Seeker() {
        super("Seeker");
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
     *
     * @see #WORKING_TIME
     */
    public void run() {
            int i = 0;
        printMessage("Started.");
        /* Sending multicast packet's on LAN */
        try (DatagramSocket datagramSocket = new DatagramSocket(CLIENT_MULTICAST_PORT, prefferedAddress)) {
            while (IS_RUNNING) {
                i++;
                /* Sending broadcast packet */
                datagramSocket.send(datagramPacket);
                printMessage("Packet sent from: " + prefferedAddress);
                sleep(100);
                /* stop if server's not responding */
                if (i == WORKING_TIME) {
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            /* Sending back packet, that program can stop
               tyring to receive packet from server, if
               something went wrong */
            SLChat.sendBackPacket();
            printMessage("Exception thrown while sending packets in run()");
            e.printStackTrace();
        } finally {
            IS_RUNNING = false;
            SLChat.sendBackPacket();
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