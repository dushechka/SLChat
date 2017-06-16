package main;

import client.model.Client;
import client.model.Seeker;
import client.view.start.StartView;
import javafx.stage.Stage;
import server.model.Server;

import java.io.IOException;
import java.net.*;
import java.util.Collections;

import static server.model.ServerConstants.*;

/**
 * Main entrance to the whole program.
 * <p>
 * This static class Starts the main GUI
 * window and also guides program workflow,
 * serving as intermedium between other
 * objects in program. Contains methods for
 * getting service information ({@link #getIP()}
 * for example).
 *
 * @author  Andrey Fadeev
 * @since 0.2
 */
public class SLChat {
    /** Is used to stop {@link server.model.Server Server} thread. */
        public static boolean IS_SERVER_RUNNING = false;
    /** Is used to stop {@link client.model.Client Client} thread. */
        public static boolean IS_CLIENT_RUNNING = false;
    /** Server backend thread. */
        public static Server SLServer = null;
    /** Client backend thread. */
        public static Client SLClient = null;
    /** Main GUI window javafx stage. */
        public static Stage primaryStage = null;
    /** Main menu window backend instance. */
        public static StartView mainView = null;
    /** Client's GUI fxml file path. */
        public static String clientGUIPath = "/client/view/chat/Chat.fxml";
    /** Preffered network interface, to run client from. */
        private static NetworkInterface prefferedInterface = null;
    /** {@link #prefferedInterface} address, with wich program will work. */
        public static InetAddress prefferedAddress = null;
    /** Interface, which is used to make local connections. */
        public static InetAddress localAddress = null;

    /**
     * Main program entry.
     */
    public SLChat() {
        try {
            localAddress = InetAddress.getByName("localhost");
            prefferedInterface = chooseInterface();
            System.out.println("Preffered interface: " + prefferedInterface.getName());
            System.out.println("Does interface support multicasting: " + prefferedInterface.supportsMulticast());
            prefferedAddress = getIfaceAddress();
            System.out.println("Preffered address is: " + prefferedAddress);
            /* starting program GUI */
            new Thread() {
                @Override
                public void run() {
                    javafx.application.Application.launch(StartView.class);
                }
            }.start();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts main GUI window.
     * <p>
     * Instantiates new thread of class
     * {@link client.view.start.StartView Startview}.
     *
     * @param args  not useful
     */
    public static void main(String[] args) {
        new SLChat();
    }

    /**
     * Starts {@link client.model.Client Client} backend class thread.
     *
     * @param   serverAddress   a string with found server's IPv4 address
     * @param   roomName        a string with received room name;
     */
    public static void startClient(InetAddress serverAddress, String roomName) {
        SLClient = new Client(serverAddress, roomName);
        SLClient.start();
    }

    /**
     * Searches for server's IP on LAN.
     * <p>
     * Starts {@link client.model.Seeker}
     * and then waits for response from
     * server with datagram, from which
     * it retrieves server's address and
     * room name.
     */
    public static void getIP() {
            String msg;
            Seeker seeker;
            DatagramPacket packet;
            byte[] packetData;

        /* Creates instance of client.model.Seeker
           class thread, that sends multicast packets
           on LAN, in order, to server catch them and
           responce with packet, containing it's IP.
           Then, creates DatagramSocket to catch
           server's back packet and extracts server's
           IP and message from it */
        try (DatagramSocket dSocket = new DatagramSocket(CLIENT_PORT, prefferedAddress)) {
            seeker = new Seeker();
            seeker.start();
            packetData = new byte[32];
            packet = new DatagramPacket(packetData, packetData.length);
            dSocket.receive(packet);
            System.out.println("Server's back packet recieved.");
            System.out.println("Server's address: " + packet.getAddress());
            System.out.println("Recieved message: " + byteToString(packetData));
            if (seeker.isAlive()) {
                seeker.die();
            }
            msg = byteToString(packetData);
            if (msg.contains(SERVER_STRING)) {
                System.out.println("Room name is: " + msg.substring(6));
                startClient(packet.getAddress(), msg.substring(6));
            } else {
                System.out.println("Server hadn't responsed for given time (3 sec).");
            }
        } catch (IOException ie) {
            System.out.println("Exception thrown while trying to find server.;");
            ie.printStackTrace();
        }
    }

    /**
     * Gets interface address, from
     * which to send packets.
     *
     * @return  {@link InetAddress} from
     *          which to send packets
     * @throws SocketException  When cannot get
     *                          address by name
     */
    private static InetAddress getIfaceAddress(NetworkInterface nif) throws SocketException {
            InetAddress address = null;

        for (InetAddress addr : Collections.list(nif.getInetAddresses())) {
            if (addr.toString().length() <= 16) {
                address = addr;
            }
        }
        return address;
    }

    /**
     * Gets interface address, from
     * which to send packets.
     * <p>
     * Invokes method of the same name
     * {@link #getIfaceAddress(NetworkInterface)},
     * supplying chosen network interface
     * ({@link #prefferedInterface}) to it.
     *
     * @return  {@link InetAddress} from
     *          which to send packets
     * @throws SocketException  When cannot get
     *                          address by name
     */
    private static InetAddress getIfaceAddress () throws SocketException {
        return getIfaceAddress(prefferedInterface);
    }
}