package client.model;

import java.net.InetAddress;

/**
 * Contains chat room info
 * such as name and servers
 * IP-address.
 */
public class Room {
        private final String ROOM_NAME;
        private final InetAddress SERVER_ADDRESS;

    public Room(String name, InetAddress ip) {
        this.ROOM_NAME = name;
        this.SERVER_ADDRESS = ip;
    }

    public String getRoomName() {
        return ROOM_NAME;
    }

    public InetAddress getServerAddress() {
        return SERVER_ADDRESS;
    }
}
