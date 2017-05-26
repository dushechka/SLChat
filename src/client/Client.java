package client;

import server.*;
import java.io.*;
import java.net.*;

public class Client extends Thread {
        private final String NICKNAME;
        private MulticastSocket mSocket = null; // Socket to find server;
        private Socket socket = null;   // Socket to establish connection with server;
        private InetAddress group = null;   // Multicast group to reach server's packet with IP;
        private DatagramPacket packet = null;
        private byte[] packetData = null;


    public Client(String nickName) {
        super("SLClient");
        this.NICKNAME = nickName;
        try {
            mSocket = new MulticastSocket(BroadcastNotifier.CLIENT_PORT);
            group = InetAddress.getByName("230.0.0.1");
            mSocket.joinGroup(group);
            packetData = new byte[8];
            packet = new DatagramPacket(packetData, packetData.length);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        start();
    }

    @Override
    public void run() {
        initConnection();
    }

    public static void main(String[] args) {
        new Client("Vasya");
    }

    private void initConnection() {
        try {
            this.mSocket.receive(packet);
            String str = Server.toString(packetData);
            if (str.equals(Server.SERVER_STRING)) {
                this.socket = new Socket(packet.getAddress(), ClientConnector.SERVER_PORT);
                System.out.println("Connection has been established.");
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        } finally {
            close();
        }
    }

    private void close() {
        try {
            if (mSocket != null) this.mSocket.close();
            if (socket != null) this.socket.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}
