package server;

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
//        broadcastNotifier.die();
//        clientConnector.die();
        try {
            broadcastNotifier.join();
            clientConnector.join();
        } catch (InterruptedException exc) {
            System.out.println("An error occured while join threads.");
            exc.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server("SRV","TalkDirtyToMe");
    }

}
