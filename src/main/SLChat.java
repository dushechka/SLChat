package main;

import client.model.Client;
import client.model.Seeker;
import client.view.start.StartView;
import javafx.stage.Stage;
import server.model.Server;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

import static server.model.ServerConstants.*;

/**
 * Main entrance to the whole programm;
 */
public class SLChat {
        public static boolean IS_SERVER_RUNNING = false;
        public static boolean IS_CLIENT_RUNNING = false;
    // Server thread;
        public static Server SLServer = null;
    // Client thread;
        public static Client SLClient = null;
    // Main stage;
        public static Stage primaryStage = null;
    // Main view;
        public static StartView mainView = null;
    // Client's GUI fxml file path;
        public static String clientGUIPath = "/client/view/main/Main.fxml";

    public static void main(String[] args) {
        // Starting StartView instance Thread;
        new Thread() {
            @Override
            public void run() {
                javafx.application.Application.launch(StartView.class);
            }
        }.start();
    }

    /**
     * Starts client's backend thread;
     * @param serverAddress - found server's IPv4 address in String format;
     */
    public static void startClient(String serverAddress) {
        SLClient = new Client(serverAddress);
        SLClient.start();
    }

    // Search for server in LAN;
    public static String getIP() {
            String msg = null;
        // Multicast packets sender;
        Seeker seeker= null;
        DatagramPacket packet = null;
        byte[] packetData = null;

        try (DatagramSocket dSocket = new DatagramSocket(CLIENT_PORT, Seeker.getInterface());) {
            //Getting server's IP;
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
            } else {
                System.out.println("Server hadn't responsed for given time (5 sec).");
            }
        } catch (IOException ie) {
            System.out.println("Exception thrown while trying to find server.;");
            ie.printStackTrace();
        }
        return (msg + packet.getAddress().toString());
    }
}