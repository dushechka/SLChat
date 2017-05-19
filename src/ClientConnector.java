import java.net.*;
import java.io.*;

/**
 * Listens for incoming client connections
 * and pass them to ClientHandler;
 */
public class ClientConnector extends Thread {
        public final static int SERVER_PORT = 4488;
        private boolean IS_RUNNING;
        private ServerSocket server = null;

    public ClientConnector() {
        super("ClientConnector");
        IS_RUNNING = true;
        try {
            server = new ServerSocket(SERVER_PORT);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        start();
    }

    @Override
    public void run() {
        try {
            while (IS_RUNNING) {
                Socket clientSocket = server.accept();
                System.out.printf("Клиент подключился.");
                System.out.println("Адрес: " + clientSocket.getInetAddress());
                System.out.println("Номер порта: " + clientSocket.getPort());
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }


    protected void die() {
        IS_RUNNING = false;
    }

    @Override
    protected void finalize() {
        try {
            server.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}
