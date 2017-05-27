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
        private ServerSocket serverSocket = null;


    public Server(String serverName, String password) {
        super("SLServer");
        this.serverName = serverName;
        this.PASSWORD = password;
        try {
            serverSocket = new ServerSocket(SERVER_FINAL_PORT);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        start();
    }

    @Override
    public void run() {
        broadcastNotifier = new BroadcastNotifier(SERVER_STRING);
        clientConnector = new ClientConnector(serverSocket);
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
        broadcastNotifier.die();
        clientConnector.die();
        try {
            broadcastNotifier.join();
            clientConnector.join();
            serverSocket.close();
        } catch (InterruptedException exc) {
            exc.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server("SRV","TalkDirtyToMe");
    }

}
