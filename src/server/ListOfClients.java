package server;

import client.Client;

import java.util.Vector;

/**
 * Encapsulates list of connected clients
 * and handles group operations on them;
 */
class ListOfClients {
    // List of connected clients;
        private Vector<ClientHandler> clients;

    ListOfClients() {
        clients = new Vector<>(8,8);
    }

    // Add clients to the list;
    void addClient(ClientHandler t) {
        clients.add(t);
        System.out.print("Client added.");
        System.out.println(" The number of clients is: " + clients.size());
    }

    // Remove clients from the list;
    private void removeClient(Thread t) {
        clients.remove(t);
        System.out.print("Client removed.");
        System.out.println(" The number of clients is: " + clients.size());
    }

    void killTheClients() {
        try {
            for (int i = clients.size(); i > 0; i--) {
                    ClientHandler ch = clients.elementAt(i-1);
                if (ch.isAlive()) {
                    System.out.println("Killing the client...");
                    ch.die();
                    ch.join();
                    System.out.println("Client killed;");
                    removeClient(ch);
                }
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}
