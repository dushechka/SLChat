package client.model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static main.SLChat.*;

import static server.model.ServerConstants.SERVER_FINAL_PORT;

/**
 * Chat's client backend;
 */
public class Client extends Thread {
    // Socket to establish connection with server;
    private final String serverAddress;
    // Determines whether this thread is alive;
    private boolean IS_RUNNING;

    public Client(String serverAddress) {
        super("SLClient");
        IS_RUNNING = false;
        IS_CLIENT_RUNNING = false;
        this.serverAddress = serverAddress;
    }

    public void run() {
        try (Socket socket = new Socket(InetAddress.getByName(serverAddress), SERVER_FINAL_PORT);) {
            IS_RUNNING = true;
            IS_CLIENT_RUNNING = true;
            System.out.println("Connection established.");
            while (IS_RUNNING) {
                try {
                    sleep(1000);
                    System.out.println("Client is working.");
                } catch (InterruptedException exc) {
                    exc.printStackTrace();
                }
            }
        } catch (IOException exc) {
            System.out.println("Server not found!");
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
