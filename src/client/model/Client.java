package client.model;

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
        private String nickname;

    public Client(DataInputStream in, DataOutputStream out, Socket socket) {
        super("SLClient");
        this.socket = socket;
        this.in = in;
        this.out = out;
        IS_RUNNING = false;
        IS_CLIENT_RUNNING = false;
    }

    public void run() {
        try {
            IS_RUNNING = true;
            IS_CLIENT_RUNNING = true;
            printMessage("A " + nickname + "'s client is running...");
            while (IS_RUNNING) {
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
        System.out.println("Client " + login + " is authenticating...");
        /* getting input and output stream for messaging */
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        System.out.println(login + "'s I/O streams initialized.");
        System.out.println("Server address is: " + serverAddress);
        /* sending login and password to server */
        sendMessage(out, password);
        /* reading the answer */
        msg = in.readUTF();
        System.out.println(login + " received message: " + msg);
        if (msg.equals("yes")) {
            sendMessage(out, login);
            SLClient = new Client(in, out, socket);
            SLClient.nickname = login;
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
        printMessage("Closing...");
        try {
            in.close();
            out.close();
            if (!socket.isClosed()) {
                socket.close();
            }
            printMessage("Closed.");
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    private static boolean sendMessage(DataOutputStream out, String message) {
        try {
            out.writeUTF(message);
            out.flush();
            System.out.println("SLClient: Sent message <" + message + ">");
            return true;
        } catch (IOException exc) {
            exc.printStackTrace();
            return false;
        }
    }

    private void printMessage(String message) {
        System.out.println(getName() + ": " + message);
    }
}
