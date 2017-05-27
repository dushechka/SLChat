package client;

import java.io.*;
import java.net.*;

import static server.ServerConstants.*;

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
            mSocket = new MulticastSocket(CLIENT_PORT);
            group = InetAddress.getByName(GROUP_ADDRESS);
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
            System.out.println("Waiting for server to connect...");
            this.mSocket.receive(packet);
            String str = byteToString(packetData);
            if (str.equals(SERVER_STRING)) {
                this.socket = new Socket(packet.getAddress(), SERVER_FINAL_PORT);
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
            if ((mSocket != null) && (!mSocket.isClosed())) {
                this.mSocket.close();
            }
            if ((socket != null) && (!socket.isClosed())) {
                this.socket.close();
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}
