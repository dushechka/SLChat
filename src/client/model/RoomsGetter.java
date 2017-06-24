package client.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Hashtable;

import static server.model.ServerConstants.*;

/**
 * Gets the server's answers
 * for multicast packets and
 * saves their room names
 * and inet addresses.
 */
public class RoomsGetter extends Thread {
    /** A list of rooms, found on LAN */
        private Hashtable<String, InetAddress> rooms;
    /** Determines, whether this thread is listening for incoming packets */
        private boolean IS_RUNNING;
    /** Network interface address to work with */
        private final InetAddress iFaceAddress;
    /** {@link Seeker} which works
     *  with the same nework interface.
     *
     *  @see #iFaceAddress
     */
        private Seeker seeker;
        private final int PORT;

    public RoomsGetter(InetAddress address, int number) {
        super("RoomsGetter_" + number);
        PORT = CLIENT_PORT + number;
        printMessage("Port number is: " + PORT);
        rooms = new Hashtable<>();
        this.iFaceAddress = address;
        printMessage("Gained inet address: " + iFaceAddress);
        seeker = new Seeker(iFaceAddress, number);
        seeker.setDaemon(true);
        IS_RUNNING = false;
    }

    public void run() {
            String msg;
            DatagramPacket packet;
            byte[] packetData;

        seeker.start();
        printMessage("Started.");
        try (DatagramSocket dSocket = new DatagramSocket(PORT, iFaceAddress)) {
            IS_RUNNING = true;
            while (IS_RUNNING) {
                /* stop listening, when time is out */
                packetData = new byte[64];
                packet = new DatagramPacket(packetData, packetData.length);
                dSocket.receive(packet);
                printMessage("Server's back packet recieved.");
                printMessage("Server's address: " + packet.getAddress());
                printMessage("Recieved message: " + byteToString(packetData));
                msg = byteToString(packetData);
                if (msg.contains(byteToString(SERVER_STRING))) {
                    String roomName = msg.substring(6).trim();
                    printMessage("Room name is: <" + roomName + ">");
                    rooms.put(roomName, packet.getAddress());
                }
                if (msg.contains(byteToString(TIME_HAS_EXPIRED))) {
                    IS_RUNNING = false;
                    break;
                }
            }
            printMessage("Stopped.");
        } catch (IOException ie) {
            printMessage("Exception thrown while trying to find server.;");
            ie.printStackTrace();
        }
    }

    public Hashtable<String, InetAddress> getRooms() {
        return rooms;
    }

    /**
     * Sends back packets, if
     * server is not responding,
     * until {@link #run()}
     * method will stop listening.
     * <p>
     * Spare method, that is invoked, if
     * {@link server.model.ClientNotifier}
     * does not respond on client's multicast
     * packets, and exception thrown in
     * {@link client.model.Seeker} Thread,
     * probably because it can't open socket.
     */
    public void sendBackPacket() {
        printMessage("Sending dummy back packet.");
        try (DatagramSocket socket = new DatagramSocket()) {
            DatagramPacket packet = new DatagramPacket(TIME_HAS_EXPIRED, TIME_HAS_EXPIRED.length,
                                                                        iFaceAddress , PORT);
            while (IS_RUNNING) {
                socket.send(packet);
                Thread.sleep(100);
            }
        } catch (SocketException se) {
            printMessage("getIP() method can't open socket to send back packet.");
            se.printStackTrace();
        } catch (IOException exc) {
            printMessage("getIP() method can't send back packet.");
            exc.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        printMessage("Stop sending back packets.");
    }

    public void die() {
        seeker.die();
        sendBackPacket();
    }

    private void printMessage(String msg) {
        System.out.println(getName() + ": " + msg);
    }
}
