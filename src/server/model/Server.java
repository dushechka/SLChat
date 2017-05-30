package server.model;


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
        broadcastNotifier = new BroadcastNotifier(ServerConstants.SERVER_STRING);
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
            broadcastNotifier.die();
            broadcastNotifier.join();
            clientConnector.die();
            clientConnector.join();
//  TODO: |Identify whether server shuts only incoming|
//  TODO: |connections, or established connections to?|
        } catch (InterruptedException exc) {
            exc.printStackTrace();
        }
    }
}
