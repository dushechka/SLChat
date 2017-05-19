import java.io.*;
import java.net.*;

public class Client extends Thread {
        private final String NICKNAME;
        private MulticastSocket socket = null;
        private InetAddress group = null;
        private DatagramPacket packet = null;
        private byte[] packetData;

    public Client(String nickName) {
        super("SLClient");
        this.NICKNAME = nickName;
        try {
            socket = new MulticastSocket(4445);
            group = InetAddress.getByName("230.0.0.1");
            socket.joinGroup(group);
            packetData = new byte[256];
            packet = new DatagramPacket(packetData, packetData.length);
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        start();
    }

    public void run() {
        try {
                socket.receive(packet);
                String serverString = new String(packetData, 0, packetData.length);
                System.out.println("Server address is: " + packet.getAddress());
                System.out.println("Server string is: " + serverString);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client("Vasya");
    }
}
