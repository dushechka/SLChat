package server;

import java.io.IOException;
import java.net.ServerSocket;

import static server.ServerConstants.SERVER_FINAL_PORT;
import static server.ServerConstants.SERVER_STRING;


public class Server extends Thread {
        private BroadcastNotifier broadcastNotifier = null;
        private ClientConnector clientConnector = null;
    // An identifying string, which is being sending to clients;
        private String serverName = null;
        private final String PASSWORD;


    public Server(String serverName, String password) {
        super("SLServer");
        this.serverName = serverName;
        this.PASSWORD = password;
        start();
    }

    @Override
    public void run() {
        broadcastNotifier = new BroadcastNotifier(SERVER_STRING);
        clientConnector = new ClientConnector();
        System.out.println("The server has been started. Waiting for connection...");
        try {
            sleep(10000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        close();
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerName() {
        return serverName;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    private void close() {
        try {
            clientConnector.die();
            clientConnector.join();
            broadcastNotifier.die();
            broadcastNotifier.join();
//  TODO: |Identify whether server shuts only incoming|
//  TODO: |connections, or established connections to?|
        } catch (InterruptedException exc) {
            exc.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server("SRV","TalkDirtyToMe");
    }

}
