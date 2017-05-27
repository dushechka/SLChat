package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Listens for incoming client connections
 * and accepts them, until will be interrupted;
 */
class ConnectionAcceptor extends Thread {
        private ServerSocket serverSocket = null;
        private ListOfClients clients = null;

    ConnectionAcceptor(ServerSocket socket, ListOfClients clients) {
        this.serverSocket = socket;
        this.clients = clients;
        start();
    }

    public void run() {
        Socket clientSocket = null;
        try {
            while (true) {
                clientSocket = serverSocket.accept();
                clients.addClient(new ClientHandler(clientSocket, clients));
            }
        } catch (IOException exc) {
//            exc.printStackTrace();
        }
    }
}
