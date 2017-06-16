package server.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import static server.model.Server.*;

/**
 * Handle all client's operations.
 * <p>
 * Examines if client supplies
 * correct password, and then,
 * if so, handles messaging
 * operation, until disconnect.
 */
class ClientHandler extends Thread {
    /** A socket, to which client is connected */
        private final Socket socket;
        private String nickname;
        private final String password;
        private final ClientConnector owner;
        private boolean IS_RUNNING;

    ClientHandler(Socket socket, String password, ClientConnector owner) {
        super("ClientHandler_" + owner.getHandlerNumber());
        this.password = password;
        this.socket = socket;
        this.owner = owner;
        this.nickname = "";
        IS_RUNNING = false;
    }

    public void run() {
        IS_RUNNING = true;
        /* Opening input and output streams to exchange messages with client. */
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
                    /* string for saving incoming messages */
                    String msg;
            msg = in.readUTF();
            if (msg.equals(password)) {
                out.writeUTF("yes");
                out.flush();
                printMessage("Client connection accepted.");
                msg = in.readUTF();
                printMessage("Received nickname <" + msg + ">");
                setNickname(msg);
            } else {
                out.writeUTF("\"You can suck my balls. Wrong password!\"");
                out.flush();
                printMessage("Client's connection rejected (wrong password).");
            }
            while (IS_RUNNING) {
                msg = in.readUTF();
                if (msg.equals("stop")){
                    die();
                }
                Thread.sleep(100);
            }
        } catch (IOException exc) {
            printMessage("Unexpected closing I/O data streams.");
        } catch (InterruptedException ie) {
            printMessage("Thread was interrupted, while sleeping.");
            ie.printStackTrace();
        }
        close();
    }

    public String getNickname() {
        return nickname;
    }

    private void die() {
        IS_RUNNING = false;
    }

    private void close() {
        owner.removeClient(this);
    }

    private void printMessage(String message) {
        System.out.println(getName() + "(" + nickname + "): " + message);
    }

    /**
     * Establishes client's nickname
     * field
     * ({@link #nickname}) and prevents
     * client's nicknames from repeating.
     * <p>
     * If client name is already used,
     * adds number from 1 and higher
     * to this client's nickname.
     *
     * @param msg   nickname, received
     *              from client;
     */
    private void setNickname (String msg) {
            String nickname = new String(msg);
        for (int i = 1; owner.isNicknameUsed(nickname.toString()); i++) {
            nickname = new String(msg + "_" + i);
        }
        this.nickname = nickname;
    }
}
