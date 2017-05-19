public class Server extends Thread {
        private BroadcastNotifier broadcastNotifier = null;
        private ClientConnector clientConnector = null;

    public Server() {
        super("SLServer");
        start();
    }

    @Override
    public void run() {
            BroadcastNotifier bn = null;

        broadcastNotifier = new BroadcastNotifier("SLChat");
        clientConnector = new ClientConnector();
        try {
            broadcastNotifier.join();
            clientConnector.join();
        } catch (InterruptedException exc) {
            exc.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server();
    }

    @Override
    protected void finalize() {
    }
}
