package client.model;

import main.SLChat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import static main.SLChat.IS_CLIENT_RUNNING;
import static main.SLChat.SLClient;
import static server.model.ServerConstants.SERVER_FINAL_PORT;

/**
 * Chat's client backend.
 */
public class Client extends Thread {
    /** Determines when to stop this thread. */
        private boolean IS_RUNNING;
    /** input and output streams for messaging */
        private final DataInputStream in;
        private final DataOutputStream out;
        private final Socket socket;
        public static InetAddress serverAddress;

    public Client(DataInputStream in, DataOutputStream out, Socket socket) {
        this.socket = socket;
        this.in = in;
        this.out = out;
        IS_RUNNING = true;
        IS_CLIENT_RUNNING = true;
    }

    public void run() {
        try {
            while (IS_RUNNING) {
                System.out.println("Client is running...");
                Thread.sleep(1000);
            }
            close();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    public static boolean connectClient(String login, String password) throws IOException {
        /* string to read from connection */
            String msg;
        /* establishing connection with server */
            Socket socket = new Socket(serverAddress, SERVER_FINAL_PORT);
        System.out.println("Client connected to server.");
        /* getting input and output stream for messaging */
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        System.out.println("I/O streams initialized.");
        System.out.println("Server address is: " + serverAddress);
        /* sending login and password to server */
        out.writeUTF(password);
        /* reading the answer */
        msg = in.readUTF();
        System.out.println("Client received message: " + msg);
        if (msg.equals("yes")) {
            SLClient = new Client(in, out, socket);
            SLClient.start();
            return true;
        } else {
            in.close();
            out.close();
            socket.close();
            return false;
        }
    }

    public void die() {
        IS_RUNNING = false;
        IS_CLIENT_RUNNING = false;
    }

    private void close() {
        System.out.println("Closing client...");
        try {
            in.close();
            out.close();
            if (!socket.isClosed()) {
                socket.close();
            }
            System.out.println("Client is closed.");
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}
