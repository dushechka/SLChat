package client.model;

import client.view.start.StartView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import server.model.ServerConstants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalTime;

import static main.SLChat.IS_CLIENT_RUNNING;
import static main.SLChat.SLClient;
import static main.SLChat.history;
import static server.model.ServerConstants.Ports;

/**
 * Connects to server and
 * handles user's messaging.
 */
public class Client extends Thread {
    /** Determines when to stop this thread. */
        private boolean IS_RUNNING;
    /** input and output streams for messaging */
        private final DataInputStream in;
        private final DataOutputStream out;
        private static Socket socket;
        private TextArea textArea = null;
        private TextField textField = null;
        private String nickname;

    public Client(DataInputStream in, DataOutputStream out, Socket socket) {
        super("SLClient");
        this.socket = socket;
        this.in = in;
        this.out = out;
        IS_RUNNING = false;
        IS_CLIENT_RUNNING = false;
    }

    /**
     * Handles messaging by
     * {@link #in} and {@link #out}
     * that was open by initializing
     * static methods.
     *
     * @see #connectClient(InetAddress)
     * @see #logInClient(String, String)
     */
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
                /* service stop message, sent by server */
                if (msg.equals("malaka")) {
                    break;
                } else if (msg.equals("mudak")) {
                    printMessage("I disconnected myself from server.");
                    break;
                } else {
                    textArea.appendText(msg + "\n");
                    /* saving message to history log file*/
                   if (history != null) {
                       history.println(LocalTime.now() + "\t" + msg);
                   }
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

    /**
     * Opens IO streams on {@link #socket}
     * and tries to log on server with
     * given login and password
     * parameters.
     * <p>
     * Run only after {@link #socket}
     * field is initialized by
     * {@link #connectClient(InetAddress)}.
     *
     * @param login   user nickname.
     * @param password  server's room password.
     * @return          true, in case of
     *                  succesful logon.
     * @throws IOException  when cannont connect
     *                      to server by
     *                      {@link #socket}.
     */
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

    /**
     * Tries to connect to
     * server by given address.
     *
     * @param serverAddress server's IP.
     * @return              true, in case
     *                      of succesful connection.
     * @throws IOException  when can't use given address.
     */
    public static boolean connectClient(InetAddress serverAddress) throws IOException {
            int SERVER_FINAL_PORT = Ports.valueOf("SERVER_FINAL_PORT").getPort();
        socket = new Socket(serverAddress, SERVER_FINAL_PORT);
        if (socket != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Stops client.
     */
    public void die() {
        IS_RUNNING = false;
        IS_CLIENT_RUNNING = false;
        if (!socket.isClosed()) {
            sendMessage(out, "malaka");
        }
    }

    /**
     * Releases used resources.
     */
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

    /**
     * Sends message to server.
     * <p>
     * Invoked from static contex, by
     * {@link #logInClient(String, String)}
     * to log on to server, when this
     * class not instantiated yet.
     *
     * @param out   connected socket's
     *              output stream.
     * @param message   message to send.
     * @return      whether message was
     *              sent or not.
     */
    private static boolean sendMessage(DataOutputStream out, String message) {
        try {
            out.writeUTF(message);
            out.flush();
            return true;
        } catch (IOException exc) {
            System.out.println("SLClient: can't send message.");
            exc.printStackTrace();
            return false;
        }
    }

    /**
     * Sends message to connected server.
     * <p>
     * Is used to send chat and service
     * messages to connected server, by
     * {@link #run()} method of this
     * class instance.
     *
     * @param message   A message to send.
     * @throws IOException  when cannot send
     *                      message by {@link #out},
     *                      or it is not available.
     */
    public void sendMessage(String message) throws IOException {
        out.writeUTF(message);
        out.flush();
    }

    private void printMessage(String message) {
        System.out.println(getName() + "(" + nickname + "): " + message);
    }

    /**
     * Sets the {@link #textArea} to
     * append received messages from
     * other clients, sent to this
     * object by server.
     * <p>
     * Is invoked by objects' methods,
     * through {@link StartView#bindTextArea()}
     * method before they run client's GUI.
     *
     * @see StartView#bindTextArea()
     * @param textArea  Link for a {@link TextArea}
     *                  instance to append received
     *                  messages' text.
     */
    public void setTextArea(TextArea textArea) {
        this.textArea = textArea;
    }

    public void setTextField(TextField textField) {
        this.textField = textField;
    }

    public TextField getTextField() {
        return textField;
    }
}
