package main;

import client.model.Client;
import client.model.Seeker;
import client.view.start.StartView;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import server.model.Server;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import static java.lang.System.currentTimeMillis;
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
    /** A list of system's running network interfaces */
        private static ArrayList<NetworkInterface> liveNICs;
    /** Preffered network interface, to run client from. */
        private static NetworkInterface prefferedInterface;
    /** A list of system rinning network interfaces' IPv4 addresses */
        public static ArrayList<InetAddress> inetAddresses;
    /** A list of system rinning network interfaces' IPv4 addresses, that support multicasting */
    public static ArrayList<InetAddress> mCastAddresses;
    /** {@link #prefferedInterface} address, with wich program will work. */
        public static InetAddress prefferedAddress;
    /** Interface, which is used to make local connections. */
        public static InetAddress localAddress;
    /** A list of rooms, found on LAN */
        public static Hashtable<String, InetAddress> rooms;
    /** Determines, whether {@link #getIP()} is listening for incoming packets */
        private static boolean isListening = false;

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
            liveNICs = getLiveNICs();
            prefferedInterface = chooseInterface(liveNICs);
            System.out.println("Preffered interface: " + prefferedInterface.getName());
            System.out.println("Does interface support multicasting: " + prefferedInterface.supportsMulticast());
            /* getting a list of all available running interfaces IPv4 addresses */
            inetAddresses = new ArrayList<>();
            mCastAddresses = new ArrayList<>();
            getIfacesAddresses(liveNICs, inetAddresses, false);
            getIfacesAddresses(liveNICs, mCastAddresses, true);
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

            rooms = new Hashtable<>();

        /* Creates instance of client.model.Seeker class thread, that sends multicast
           packets on LAN, in order, to server.model.ClientNotifier catch them and
           response with packet, containing it's IP. Then, creates DatagramSocket
           to catch server's back packet extracts server's IP and message from it */
        try (DatagramSocket dSocket = new DatagramSocket(CLIENT_PORT, prefferedAddress)) {
            seeker = new Seeker();
            seeker.start();
            Long startTime = System.currentTimeMillis();
            while (seeker.getStatus()) {
                isListening = true;
                /* stop listening, when time is out */
                if ((System.currentTimeMillis() - startTime) > 3000L) break;
                packetData = new byte[64];
                packet = new DatagramPacket(packetData, packetData.length);
                dSocket.receive(packet);
                System.out.println("Server's back packet recieved.");
                System.out.println("Server's address: " + packet.getAddress());
                System.out.println("Recieved message: " + byteToString(packetData));
                msg = byteToString(packetData);
                if (msg.contains(byteToString(SERVER_STRING))) {
                    String roomName = msg.substring(6).trim();
                    System.out.println("Room name is: <" + roomName + ">");
                    rooms.put(roomName, packet.getAddress());
                }
            }
            isListening = false;
            System.out.println("GetIP stopped.");
        } catch (IOException ie) {
            System.out.println("Exception thrown while trying to find server.;");
            ie.printStackTrace();
        }
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
        System.out.println("Sending dummy back packet.");
        try (DatagramSocket socket = new DatagramSocket()) {
            DatagramPacket packet = new DatagramPacket(TIME_HAS_EXPIRED, TIME_HAS_EXPIRED.length,
                                                                    prefferedAddress, CLIENT_PORT);
            while (isListening) {
                socket.send(packet);
                Thread.sleep(100);
            }

        } catch (SocketException se) {
            System.out.println("getIP() method can't open socket to send back packet.");
            se.printStackTrace();
        } catch (IOException exc) {
            System.out.println("getIP() method can't send back packet.");
            exc.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
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