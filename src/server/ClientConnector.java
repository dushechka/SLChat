package server;

import java.io.IOException;
import java.net.*;

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

    ClientConnector() {
        super("ClientConnector");
        IS_RUNNING = true;
        clients = new ListOfClients();
        try {
            serverSocket = new ServerSocket(SERVER_FINAL_PORT);

        } catch (IOException exc) {
            exc.printStackTrace();
        }
        start();
    }

    @Override
    public void run() {
            Socket clientSocket = null;
        try {
            while (IS_RUNNING) {
                clientSocket = serverSocket.accept();
                clients.addClient(new ClientHandler(clientSocket, clients));
                sleep(1000);
            }
        } catch (InterruptedException exc) {
            exc.printStackTrace();
        } catch (IOException exc) {
            exc.printStackTrace();
        } finally {
            close();
        }
    }

    protected void die() {
            Socket socket = null;
        IS_RUNNING = false;
        try {
            // This is need to get out from serverSocket.accept() cycle in run() method;
            socket = new Socket("localhost", SERVER_FINAL_PORT);
        } catch (IOException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if ((socket != null) && (!socket.isClosed())) {
                    socket.close();
                }
            } catch (IOException e) {}
        }
    }

    private void close(){
        try {
            clients.killTheClients();
            System.out.println("ClientConnector stopped.");
            serverSocket.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}
