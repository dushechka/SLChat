import java.io.IOException;
import java.net.*;
import java.util.Collection;

public class Server extends Thread {
        private InetAddress group = null;
        private DatagramPacket packet = null;
        private DatagramSocket socket = null;
        private static final long FIVE_SECONDS = 5000;
        private byte[] msg = null;

    public Server(String message) {
        super("SLServer");
        this.msg = message.getBytes();
        try {
            group = InetAddress.getByName("230.0.0.1");
            socket = new DatagramSocket(4444);
            packet = new DatagramPacket(msg, msg.length, group, 4445);
        } catch (UnknownHostException | SocketException exc) {
            exc.printStackTrace();
        }

        start();
    }

    public void run() {
        try {
            while(true) {
                socket.send(packet);
                try{
                    sleep((long) Math.random() * FIVE_SECONDS);
                } catch (InterruptedException exc) {
                    exc.printStackTrace();
                }
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    @Override
    protected void finalize() {
        socket.close();
    }

    public static void main(String[] args) {
        new Server("SLChat");
    }
}
