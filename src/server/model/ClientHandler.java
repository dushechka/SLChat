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
        private Socket socket;
        private final String password;

    ClientHandler(Socket socket, String password) {
        super("ClientHandler");
        this.password = password;
        this.socket = socket;
    }

    public void run() {
        /* string for saving incoming messages */
            String msg;
        /* Opening input and output streams to exchange messages with client. */
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            msg = in.readUTF();
            if (msg.contains(password)) {
                out.writeUTF("yes");
                out.flush();
            } else {
                out.writeUTF("no");
                out.flush();
            }
            System.out.println("Auth string received: " + msg);
            Thread.sleep(100);
        } catch (IOException exc) {
            exc.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}
