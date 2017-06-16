package client.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static main.SLChat.*;
import static server.model.ServerConstants.SERVER_FINAL_PORT;

/**
 * Chat's client backend.
 */
public class Client extends Thread {
    /** Determines when to stop this thread. */
        private boolean IS_RUNNING;
    /** server's IP address */
        public static InetAddress serverAddress = null;
    /** input and output streams for messaging */
        private final DataInputStream in;
        private final DataOutputStream out;

    public Client(DataInputStream in, DataOutputStream out) {
        super("SLClient");
        this.in = in;
        this.out = out;
        IS_RUNNING = false;
        IS_CLIENT_RUNNING = false;
    }

    /**
     * Establishes connection with
     * server, until signal to stop
     * is received from outside by
     * invoking {@link #die()}.
     */
    public void run() {
        IS_RUNNING = true;
        IS_CLIENT_RUNNING = true;
        try {
            while (IS_RUNNING) {
                sleep(1000);
                System.out.println("Client is working.");
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    public void die() {
        IS_RUNNING = false;
        close();
    }

    private void close() {
        System.out.println("Closing client;");
    }

    public static void connectClient(String login, String password) {
        /* string to read from connection */
            String msg = null;
        /* establishing connection with server */
        System.out.println("Server address is: " + serverAddress);
        try (Socket socket = new Socket(serverAddress, SERVER_FINAL_PORT);) {
            System.out.println("Connection established.");
            /* getting input and output stream for messaging */
            try (DataInputStream in = new DataInputStream(socket.getInputStream());
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
                out.writeUTF(login + password);
                in.readUTF();
                if (msg.equals("yes")) {
                    SLClient = new Client(in, out);
                    mainView.changeWindow(CLIENT_GUI_PATH);
                }
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        } catch (IOException exc) {
            System.out.println("Server not found!");
            exc.printStackTrace();
        } finally {
            System.out.println("Client stopped.");
        }
    }
}
