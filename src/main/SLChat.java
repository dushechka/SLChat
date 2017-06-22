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
    /** Is used to stop {@link server.model.Server} thread. */
        public static boolean IS_SERVER_RUNNING;
    /** Is used to stop {@link client.model.Client} thread. */
        public static boolean IS_CLIENT_RUNNING;
    /** Is used, to determine, if
     *  {@link server.model.ClientConnector} already running, so
     *  the {@link client.model.Client} can connect to server */
        public static boolean IS_CLIENT_CONNECTOR_RUNNING;
    /** Determines, whether {@link #getIP()} method received answer from server */
        public static boolean IS_BACK_PACKED_RECEIVED;
    /** Server backend thread. */
        public static Server SLServer;
    /** Client backend thread. */
        public static Client SLClient;
    /** Main GUI window javafx stage. */
        public static Stage primaryStage;
    /** Main menu window backend instance. */
        public static StartView mainView;
    /** Client's GUI fxml file path. */
        public static final String CLIENT_GUI_PATH = "/client/view/chat/Chat.fxml";
    /** Login GUI fxml file path. */
        public static final String LOGIN_GUI_PATH = "/client/view/login/Login.fxml";
    /** Search GUI fxml file path. */
        public static final String SEARCH_GUI_PATH = "/client/view/search/Search.fxml";
    /** Create room GUI fxml file path. */
        public static final String CREATE_GUI_PATH = "/server/view/create/Create.fxml";
    /** Main window GUI fxml file path. */
        public static final String START_GUI_PATH = "/client/view/start/Start.fxml";
    /** Preffered network interface, to run client from. */
        private static NetworkInterface prefferedInterface;
    /** {@link #prefferedInterface} address, with wich program will work. */
        public static InetAddress prefferedAddress;
    /** Interface, which is used to make local connections. */
        public static InetAddress localAddress;

    /**
     * Main program entry.
     */
    public SLChat() {
        try {
            IS_SERVER_RUNNING = false;
            IS_CLIENT_RUNNING = false;
            IS_CLIENT_CONNECTOR_RUNNING = false;
            localAddress = InetAddress.getByName("localhost");
            /* choosing live network interface to work with */
            prefferedInterface = chooseInterface();
            System.out.println("Preffered interface: " + prefferedInterface.getName());
            System.out.println("Does interface support multicasting: " + prefferedInterface.supportsMulticast());
            prefferedAddress = getIfaceAddress(prefferedInterface);
            System.out.println("Preffered address is: " + prefferedAddress);
            /* starting program GUI */
            new Thread() {
                @Override
                public void run() {
                    javafx.application.Application.launch(StartView.class);
                }
            }.start();
        } catch (SocketException se) {
            System.out.println("Looks like program can't retrieve IPv4 address from interface.");
            se.printStackTrace();
        } catch (Error err) {
            System.out.println("It looks like the network subsystem is unavailable.");
            err.printStackTrace();
        } catch (IndexOutOfBoundsException exc) {
            System.out.println("It looks, like no live network interfaces is available.");
        } catch (UnknownHostException e){}
    }

    /**
     * Starts main GUI window.
     * <p>
     * Instantiates new thread of class
     * {@link client.view.start.StartView Startview}.
     *
     * @param args  never used.
     */
    public static void main(String[] args) {
        new SLChat();
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

        /* Creates instance of client.model.Seeker class thread, that sends multicast
           packets on LAN, in order, to server.model.ClientNotifier catch them and
           response with packet, containing it's IP. Then, creates DatagramSocket
           to catch server's back packet extracts server's IP and message from it */
        try (DatagramSocket dSocket = new DatagramSocket(CLIENT_PORT, prefferedAddress)) {
            IS_BACK_PACKED_RECEIVED = false;
            seeker = new Seeker();
            seeker.start();
            packetData = new byte[64];
            packet = new DatagramPacket(packetData, packetData.length);
            dSocket.receive(packet);
            IS_BACK_PACKED_RECEIVED = true;
            System.out.println("Server's back packet recieved.");
            System.out.println("Server's address: " + packet.getAddress());
            System.out.println("Recieved message: " + byteToString(packetData));
            if (seeker.isAlive()) {
                seeker.die();
            }
            msg = byteToString(packetData);
            if (msg.contains(byteToString(SERVER_STRING))) {
                System.out.println("Room name is: <" + msg.substring(6).trim() + ">");
                connectClient(packet.getAddress());
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
     * @param nif   A network interface, for
     *              which to get an address.
     * @return      {@link InetAddress} from
     *              which to send packets.
     * @throws SocketException  When cannot get
     *                          address by name.
     */
    private static InetAddress getIfaceAddress(NetworkInterface nif) throws SocketException {
            InetAddress address = null;

        for (InetAddress addr : Collections.list(nif.getInetAddresses())) {
            /* selects an IPv4 address, assigned to interface */
            if (addr.toString().length() <= 16) {
                address = addr;
            }
        }
        return address;
    }

    /**
     * Sends back packets, if
     * server is not responding,
     * until {@link #getIP()}
     * method will stop listening.
     * <p>
     * Spare method, that is invoked, if
     * {@link server.model.ClientNotifier}
     * does not respond on client's multicast
     * packets, and exception thrown in
     * {@link client.model.Seeker} Thread,
     * probably because it can't open socket.
     */
    public static void sendBackPacket() {
        System.out.println("Start sending back packets.");
        try (DatagramSocket socket = new DatagramSocket()) {
            DatagramPacket packet = new DatagramPacket(TIME_HAS_EXPIRED, TIME_HAS_EXPIRED.length,
                                                                    prefferedAddress, CLIENT_PORT);
            while (!IS_BACK_PACKED_RECEIVED) {
                socket.send(packet);
            }

        } catch (SocketException se) {
            System.out.println("getIP() method can't open socket to send back packet.");
            se.printStackTrace();
        } catch (IOException exc) {
            System.out.println("getIP() method can't send back packet.");
            exc.printStackTrace();
        }
        System.out.println("Stop sending back packets.");
    }

    /**
     * Invokes {@link #connectClient(InetAddress)}
     * method and supplies serverAddress field
     * to it in {@link InetAddress} format.
     *
     * @param serverAddress server's address
     *                      to connect.
     */
    public static void connectClient(String serverAddress) {
        try {
            connectClient(InetAddress.getByName(serverAddress));
        } catch (UnknownHostException exc) {
            mainView.alertWindow("Wrong", "Server not found!");
        }
    }

    /**
     * Connects to server and
     * changes view to login
     * window if success.
     *
     * @see client.view.login.LoginController
     * @param serverAddress server's address
     *                      to connect.
     */
    public static void connectClient(InetAddress serverAddress) {
        try {
            /*
             * Attempting to start client with
             * gained server address. If failed
             * to start, {@link client.model.Client}
             * will throw an exception.
             */
            if (Client.connectClient(serverAddress)) {
                mainView.openNewWindow(LOGIN_GUI_PATH);
            } else {
                mainView.alertWindow("Wrong.", "Server not found!");
            }
        } catch (IOException e) {
            mainView.alertWindow("Oops!", "Sorry... Can't connect to server. :'-(");
            System.out.println("Couldn't open login window.");
        }
    }
}