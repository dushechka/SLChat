package client.model;

import java.io.IOException;
import java.net.*;

import static java.lang.Thread.*;
import static server.model.ServerConstants.*;

/**
 * Client connecting handle class;
 * Gets server's IP address;
 */
public class IPGetter {

    public static boolean getIP() {
        // Multicast packets sender;
            Seeker seeker= null;
        // from server, which contains it's IP;
            DatagramPacket packet = null;
            byte[] packetData = null;
        // Client's thread for return;
            Client client = null;

        try (DatagramSocket dSocket = new DatagramSocket(CLIENT_PORT);){
            //Getting server's IP;
            seeker = new Seeker();
            seeker.start();
            packetData = new byte[8];
            packet = new DatagramPacket(packetData, packetData.length);
            dSocket.receive(packet);
            System.out.println("Server's back packet recieved.");
            System.out.println("Server's address: " + packet.getAddress());
            seeker.die();
            // Create new client;
            client = new Client(packet.getAddress().toString());
            System.out.println("Client started.");
        } catch (IOException ie) {
            System.out.println("Exception thrown while client tried to establish connection with server;");
            ie.printStackTrace();
        } finally {
            System.out.println("IPGetter stoped.");
        }

        if (client != null) {
            return true;
        } else {
            System.out.println("Client couldn't start!");
            return false;
        }
    }
}
