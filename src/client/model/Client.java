package client.model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import static server.model.ServerConstants.SERVER_FINAL_PORT;

/**
 * Chat's client backend;
 */
public class Client extends Thread{
    // Socket to establish connection with server;
        private final InetAddress serverAddress;
    // Determines whether this thread is alive;
        private boolean IS_RUNNING;

    public Client(InetAddress serverAddress) {
        IS_RUNNING = false;
        this.serverAddress = serverAddress;
    }

    public void run() {
        IS_RUNNING = true;
        try (Socket socket = new Socket(serverAddress, SERVER_FINAL_PORT);) {
            System.out.println("Connection established.");
            while(IS_RUNNING) {
            try {
                sleep(1000);
                System.out.println("Client is working.");
            } catch (InterruptedException exc) {
                exc.printStackTrace();
            }
        }
        } catch (IOException exc) {
            System.out.println("Exception thrown, while trying to connect to server.");
            System.out.println("Thread: " + getName());
            System.out.println("Client couldn't establish connection with server.");
            exc.printStackTrace();
        } finally {
            System.out.println("Client stopped.");
        }
    }

    public void die() {
        IS_RUNNING = false;
        close();
    }
    private void close() {
//        try {
//            if ((socket != null) && (!socket.isClosed())) {
//                socket.close();
//            }
//    } catch (IOException e) {
//            System.out.println("Exception thrown while client tied to release resources.");
//            e.printStackTrace();
//        }
        System.out.println("Closing client;");
    }
}
