package server.model;

/**
 * Handle all client's operations.
 * <p>
 * Examines if client supplies
 * correct password, and then,
 * if so, handles messaging
 * operation, until disconnect.
 */
class ClientHandler extends Thread {

    ClientHandler() {
        super("ClientHandler");
    }

    public void run() {

    }
}
