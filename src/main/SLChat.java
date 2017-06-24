package main;

import client.model.Client;
import client.model.RoomsGetter;
import client.view.start.StartView;
import javafx.stage.Stage;
import server.model.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;

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
     */
    public static void getIP() {
            int i = 1;
            ArrayList<RoomsGetter> getters = new ArrayList<>();
            /* time, at which RoomsGetter threads will work */
            final int WORKING_TIME = 300;

        rooms = new Hashtable<>();
        long startTime = System.currentTimeMillis();
        /* Creating a pool of RoomsGetters, to get servers
           InetAddresses from LAN's on all network interfaces. */
        System.out.println("Starting getters and seekers.");
        for (InetAddress iFace : inetAddresses) {
            RoomsGetter getter = new RoomsGetter(iFace, i);
            getter.start();
            getters.add(getter);
            i++;
        }
        System.out.println("Seekers and getters started.");
        /* waiting for a given period of time */
        try {
            while ((System.currentTimeMillis() - startTime) < WORKING_TIME) {
                Thread.sleep(100);
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        System.out.println("Killing seekers and getters.");
        for (RoomsGetter gt : getters) {
            gt.die();
            rooms.putAll(gt.getRooms());
        }
        System.out.println("A list of gained rooms:");
        for (String roomName : rooms.keySet()) {
            System.out.println(roomName);
        }
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