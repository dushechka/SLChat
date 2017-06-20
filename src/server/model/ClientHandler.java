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
        DataInputStream in;
        DataOutputStream out;

    ClientHandler(Socket socket, String password, ClientConnector owner) {
        super("ClientHandler_" + owner.getHandlerNumber());
        this.password = password;
        this.socket = socket;
        this.owner = owner;
        this.nickname = "";
        IS_RUNNING = false;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            printMessage("Exception thrown while instantiating.");
            e.printStackTrace();
            close();
        }
    }

    public void run() {
        IS_RUNNING = true;
        /* Opening input and output streams to exchange messages with client. */
        try {
                /* string for saving incoming messages */
                String msg;
            msg = in.readUTF();
            if (msg.equals(password)) {
                sendMessage("yes");
                printMessage("Client connection accepted.");
                msg = in.readUTF();
                printMessage("Received nickname <" + msg + ">");
                setNickname(msg);
            } else {
                sendMessage("\"You can suck my balls. Wrong password!\"");
                printMessage("Client's connection rejected (wrong password).");
                IS_RUNNING = false;
            }
            while (IS_RUNNING) {
                msg = in.readUTF();
                printMessage("Received <" + msg + ">");
                /* A way to ecological breaking connection */
                if (msg.equals("malaka")) {
                    sendMessage("mudak");
                    printMessage("Client disconnected.");
                    break;
                } else if (msg.equals("mudak")) {
                    printMessage("Broke connection with client " + nickname);
                    break;
                } else {
                    owner.sendToAll(msg, this);
                }
                Thread.sleep(100);
            }
        } catch (IOException exc) {
            printMessage("Unexpected closing I/O data streams.");
        } catch (InterruptedException ie) {
            printMessage("Thread was interrupted, while sleeping.");
            ie.printStackTrace();
        } finally {
            close();
        }
    }

    public String getNickname() {
        return nickname;
    }

    void die() {
        try {
            sendMessage("malaka");
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        IS_RUNNING = false;
    }

    private void close() {
        printMessage("Closing...");
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (!socket.isClosed()) socket.close();
            owner.removeClient(this);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        printMessage("Closed.");
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
            printMessage("Nickname <" + nickname + "> is used.");
            nickname = new String(msg + "_" + i);
        }
        this.nickname = nickname;
        owner.sendToAllAnon(("<" + nickname + "> joined this room."), this);
        printMessage("Nickname <" + nickname + "> associated to this client.");
    }

    void sendMessage(String message) throws IOException {
        out.writeUTF(message);
        out.flush();
    }
}