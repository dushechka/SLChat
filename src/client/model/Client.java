package client.model;

import javafx.scene.control.TextArea;

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
        private static Socket socket;
        private TextArea textArea = null;
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
            /* waiting until mainView will set textArea
             field, so this could harmlessly with it */
            while (textArea == null) {
                Thread.sleep(100);
            }
            String msg = "";
            IS_RUNNING = true;
            IS_CLIENT_RUNNING = true;
            printMessage(nickname + "'s client is running...");
            while (IS_RUNNING) {
                msg = in.readUTF();
                /* A way to ecological breaking connection */
                if (msg.equals("malaka")) {
                    sendMessage(out, "mudak");
                    printMessage("Server broke connection.");
                    break;
                } else if (msg.equals("mudak")) {
                    printMessage("I disconnected myself from server.");
                    break;
                } else {
                    textArea.appendText(msg + "\n");
                }
                Thread.sleep(100);
            }
        }catch (IOException ie) {
            printMessage("Server became unavailable unexpectingly.");
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } finally {
            close();
        }
    }

    public static boolean logInClient(String login, String password) throws IOException {
        /* string to read from connection */
            String msg;
        /* establishing connection with server */
        System.out.println("Client " + login + " is authenticating...");
        /* getting input and output stream for messaging */
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        System.out.println(login + "'s I/O streams initialized.");
        System.out.println("Server address is: " + socket.getInetAddress());
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

    public static boolean connectClient(InetAddress serverAddress) throws IOException {
        socket = new Socket(serverAddress, SERVER_FINAL_PORT);
        if (socket != null) {
            return true;
        } else {
            return false;
        }
    }

    public void die() {
        IS_RUNNING = false;
        IS_CLIENT_RUNNING = false;
        sendMessage(out, "malaka");
    }

    private void close() {
        printMessage("Closing...");
        try {
            in.close();
            out.close();
            if (!socket.isClosed()) socket.close();
            printMessage("Closed.");
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    private static boolean sendMessage(DataOutputStream out, String message) {
        try {
            out.writeUTF(message);
            out.flush();
            return true;
        } catch (IOException exc) {
            System.out.println("SLClient: can't sent message.");
            exc.printStackTrace();
            return false;
        }
    }

    public void sendMessage(String message) throws IOException {
        out.writeUTF(message);
        out.flush();
    }

    private void printMessage(String message) {
        System.out.println(getName() + "(" + nickname + "): " + message);
    }

    public void setTextArea(TextArea textArea) {
        this.textArea = textArea;
    }
}
