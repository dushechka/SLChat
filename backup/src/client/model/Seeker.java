package client.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static main.SLChat.prefferedAddress;
import static server.model.ServerConstants.*;

/**
 * Sends Multicast packets to
 * specific multicast group
 * so, the server could recognize,
 * at which IP the client is located.
 *
 * @see server.model.ServerConstants#GROUP_ADDRESS
 */


public class Seeker {
    private static final int WORK_TIME = 3;

    /**
    * @deprecated   No substitution.
    */
    @Deprecated
    public Seeker(){}

    /**
     * Sends multicast packets on LAN
     * in order to find server.
     * <p>
     * Starts sending multicast packets
     * at given group addres
     * {@link server.model.ServerConstants#GROUP_ADDRESS}
     * every second, for a given amount of time
     * ({@link #WORK_TIME}).
     *
     * @throws IOException  when can't send multicast
     *                      packets for some of reason.
     */
    public static void sendMulticast() throws IOException {

        printMessage("Started.");
        /* Sending multicast packet's on LAN */
        try (DatagramSocket datagramSocket = new DatagramSocket(CLIENT_MULTICAST_PORT, prefferedAddress)) {
                int i = 0;
                /* Getting multicast group IP address */
                InetAddress group = InetAddress.getByName(GROUP_ADDRESS);
                DatagramPacket packet = new DatagramPacket(SERVER_STRING, SERVER_STRING.length, group, SERVER_MULTI_PORT);
            while (true) {
                i++;
                /* Sending broadcast packet */
                datagramSocket.send(packet);
                printMessage("Packet send from " + prefferedAddress);
                Thread.sleep(1000);
                /* stop if server's not responding */
                if (i == WORK_TIME) {
                    packet = new DatagramPacket(TIME_HAS_EXPIRED, TIME_HAS_EXPIRED.length,
                                                                        prefferedAddress, CLIENT_PORT);
                    datagramSocket.send(packet);
                    break;
                }
            }
            printMessage("Stopped.");
        } catch (IOException e) {
            printMessage("Exception thrown while sending packets.");
            e.printStackTrace();
            throw new IOException();
        } catch (InterruptedException ie) {
            printMessage("Interrupted.");
            ie.printStackTrace();
        }
    }

    private static void printMessage(String msg) {
        System.out.println("Seeker: " + msg);
    }
}