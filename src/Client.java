import java.io.*;
import java.net.*;

public class Client extends Thread {
        private final String NICKNAME;
        private MulticastSocket mSocket = null;
        private Socket socket = null;
        private InetAddress group = null;
        private DatagramPacket packet = null;
        private byte[] packetData;

    public Client(String nickName) {
        super("SLClient");
        this.NICKNAME = nickName;
        try {
            mSocket = new MulticastSocket(BroadcastNotifier.CLIENT_PORT);
            group = InetAddress.getByName("230.0.0.1");
            mSocket.joinGroup(group);
            packetData = new byte[256];
            packet = new DatagramPacket(packetData, packetData.length);
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        start();
    }

    @Override
    public void run() {
        try {
                this.mSocket.receive(packet);
                String serverString = new String(packetData, 0, packetData.length);
                this.socket = new Socket(packet.getAddress(), ClientConnector.SERVER_PORT);
                System.out.println("Connections has been established.");
                System.out.println("Inet adress of the server is: " + socket.getInetAddress());
                System.out.println("Port is: " + socket.getPort());
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client("Vasya");
    }

    @Override
    public void finalize() {
        try {
            this.mSocket.close();
            this.socket.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}
