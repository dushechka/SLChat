package server.model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import static main.SLChat.IS_CLIENT_CONNECTOR_RUNNING;
import static server.model.ServerConstants.SERVER_FINAL_PORT;

/**
 * Listens for incoming client
 * connections and pass them to
 * ClientHandler if connection
 * was established.
 */
public class ClientConnector extends Thread {
    /** Determines, when to stop this thread */
        private boolean IS_RUNNING;
    /** A server socket for establishing final connection with client */
        private ServerSocket serverSocket;
    /** A socket to transmit new client's connections to {@link ClientHandler} */
        private Socket clientSocket;
    /** A list of connected clients to work with */
        private Vector<ClientHandler> clients;
    /** A new connected client to add to {@link #clients} */
        private ClientHandler clientHandler;
        private final String password;
    /** An addition number to {@link ClientHandler} threads names */
        private int handlerNumber;

    ClientConnector(String password) {
        super("ClientConnector");
        handlerNumber = 0;
        this.clients = new Vector<>();
        this.password = password;
        try {
            serverSocket = new ServerSocket(SERVER_FINAL_PORT);
//            serverSocket = new ServerSocket(SERVER_FINAL_PORT, 10, getInterface("eth3"));
        } catch (IOException exc) {
            printMessage("Exception thrown while opening ServerSocket.");
            exc.printStackTrace();
        }
    }

    public void run() {
        IS_RUNNING = true;
        IS_CLIENT_CONNECTOR_RUNNING = true;
        printMessage("Started.");
        try {
            while (IS_RUNNING) {
                /* Establishing connection with client. */
                clientSocket = serverSocket.accept();
                if (!IS_RUNNING) break;
                clientHandler = new ClientHandler(clientSocket, password, this);
                handlerNumber++;
                /* saving ClientHandler to list */
                clients.add(clientHandler);
                clientHandler.start();

                printMessage("New client connected.");
                printListOfClients();
            }
        } catch (IOException e) {
            printMessage("Exception thrown in run() method.");
        } finally {
            close();
        }
    }

    /**
     * Kills this thread.
     */
    void die() {
        IS_RUNNING = false;
        IS_CLIENT_CONNECTOR_RUNNING = false;
        try ( Socket dummy = new Socket("localhost", SERVER_FINAL_PORT); ) {
            /* Socket made to overcome "while" cycle in run() method */
        } catch (IOException e) {
            printMessage("Exception thrown while trying to kill thread.");
            e.printStackTrace();
        }
    }

    /**
     * Releases used resources.
     */
    private void close() {
        try {
            /* deleting clients from server */
            emptyClients();
            if ((serverSocket != null) && (!serverSocket.isClosed())) {
                serverSocket.close();
            }
        } catch (InterruptedException exc) {
            printMessage("Couldn't wait, until all clients will be disconnected.");
        } catch (IOException e) {
            printMessage("Exception thrown while trying to release resources.");
        }
        System.out.println("ClientConnector stopped.");
    }

    /**
     * Stops all running ClientHandlers,
     * and removes them from list.
     *
     * @throws InterruptedException never thrown.
     */
    private void emptyClients() throws InterruptedException {
        for (ClientHandler ch : clients) {
            ch.die();
        }
        /* waiting, until all clients will be disconnected */
        for (int i = 1; i < 11; i++) {
            if (clients.size() > 0) {
                Thread.sleep(100 * i);
            }
        }
    }

    /**
     * Remove {@link ClientHandler}
     * from {@link #clients} list.
     *
     * @param client    client to remove.
     */
    public void removeClient(ClientHandler client) {
        clients.remove(client);
        printMessage("Client <" + client.getNickname() + "> removed.");
        sendToAllAnon(("<" + client.getNickname() + "> left this room."), client);
        printListOfClients();
    }

    public void printListOfClients() {
        printMessage("List of connected clients:");
        for (ClientHandler ch : clients) {
            printMessage(ch.getName() + "(" + ch.getNickname() + ")");
        }
    }

    /**
     * Checks, if client with supplied
     * nickname is already connected.
     *
     * @param nickname  nickname, which client
     *                  wants to use.
     * @return          true, if client with such
     *                  nickname exists.
     */
    public boolean isNicknameUsed(String nickname) {
        for (ClientHandler ch : clients ) {
            if (ch.getNickname().equals(nickname)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sends client's message to
     * all connected clients, except
     * the sender.
     *
     * @param message   a message to send.
     * @param sender    client, that sent message.
     */
    void sendToAll(String message, ClientHandler sender) {
        for (ClientHandler ch : clients) {
            if (ch != sender) {
                try {
                    ch.sendMessage(sender.getNickname() + ": " + message);
                } catch (IOException e) {
                    printMessage(ch.getName() + " could not send message to " +
                                                "it's client " + ch.getNickname());
                }
            }
        }
    }

    /**
     * Sends server's messages to
     * all connected clients.
     *
     * @param message   message to send.
     * @param sender    client, information
     *                  about whom to send.
     */
    void sendToAllAnon(String message, ClientHandler sender) {
        for (ClientHandler ch : clients) {
            if (ch != sender) {
                try {
                    ch.sendMessage(message);
                } catch (IOException e) {
                    printMessage(ch.getName() + " could not send service " +
                                "message to it's client <" + ch.getNickname() + ">");
                }
            }
        }
    }

    private void printMessage(String message) {
        System.out.println(getName() + ": " + message);
    }

    int getHandlerNumber() {
        return handlerNumber;
    }
}
