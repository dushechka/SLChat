public class Server extends Thread {
        private BroadcastNotifier broadcastNotifier = null;
        private ClientConnector clientConnector = null;
        public final static String SERVER_STRING = "SLChat";
        private volatile String CURRENT_ROOM_NAME;
        private final String PASSWORD;

    public Server(String roomName, String password) {
        super("SLServer");
        this.PASSWORD = password;
        CURRENT_ROOM_NAME = roomName;
        start();
    }

    @Override
    public void run() {
            BroadcastNotifier bn = null;
        broadcastNotifier = new BroadcastNotifier(SERVER_STRING);
        clientConnector = new ClientConnector(CURRENT_ROOM_NAME, PASSWORD);
        System.out.println("The server has been started. Waiting for connection...");
        close();
    }

    public static void main(String[] args) {
        new Server("Sick Fucks", "12345");
    }

    private void close() {
//        broadcastNotifier.die();
//        clientConnector.die();
        try {
            broadcastNotifier.join();
            clientConnector.join();
        } catch (InterruptedException exc) {
            exc.printStackTrace();
        }
    }

    public static void toByte(byte[] msg, String message) {
            int i = 0;
        for (char m : message.toCharArray()) {
            msg[i] = (byte) m;
            i++;
        }
    }
}
