package server;

import java.net.*;
import java.io.*;
import static server.ServerConstants.SERVER_FINAL_PORT;

/**
 * Listens for incoming client connections
 * and pass them to ClientHandler;
 */
class ClientConnector extends Thread {
        // Tracking server state value. Shuts this tread when set to false;
        private boolean IS_RUNNING;
        // A server socket for establishing final connection with client;
        private ServerSocket serverSocket = null;
        private ListOfClients clients;
        private ConnectionAcceptor connectionAcceptor = null;

    ClientConnector(ServerSocket serverSocket) {
        super("ClientConnector");
        IS_RUNNING = true;
        clients = new ListOfClients();
        this.serverSocket = serverSocket;
        start();
    }

    @Override
    public void run() {
        connectionAcceptor = new ConnectionAcceptor(serverSocket, clients);
        try {
            while (IS_RUNNING) {
                    sleep(100);
            }
        } catch (InterruptedException exc) {
            exc.printStackTrace();
        } finally {
            close();
        }
    }


    protected void die() {
        IS_RUNNING = false;
    }

    private void close(){
//  TODO: |Identify whether server shuts only incoming|
//  TODO: |connections, or established connections to?|
            connectionAcceptor.interrupt();
            clients.killTheClients();
            System.out.println("ClientConnector stopped.");
    }
}
