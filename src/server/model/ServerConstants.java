package server.model;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Contains program final fields and methods;
 */
public class ServerConstants {
        // String, that server is broadcasting to clients for server identification;
                public final static String SERVER_STRING = "SLChat";
        // Searching for server in da LAN time has expired message;
                public final static String TIME_HAS_EXPIRED = "TimeHasExpired";
        // This port is used for client to connect to server;
                public final static int SERVER_FINAL_PORT = 4488;
        // Broadcast group address;
                public final static String GROUP_ADDRESS = "230.0.0.1";
        // These ports are used to client and server to find each other;
                public final static int SERVER_MULTI_PORT = 4444;
                public final static int CLIENT_MULTICAST_PORT = 4445;
                public final static int SERVER_PORT = 4446;
                public final static int CLIENT_PORT = 4447;
        // Plain five seconds;
                public final static long FIVE_SECONDS = 5000;


        // Convert String to an array of bytes;
        public static void stringToByte(String input, byte[] output) {
                int i = 0;
                for (char m : input.toCharArray()) {
                        output[i] = (byte) m;
                        i++;
                }
        }

        // Convert an array of bytes to a String;
        public static String byteToString(byte[] input) {
                StringBuilder sb = new StringBuilder();
                for (byte b : input) {
                        if (b != 0) {
                                sb.append((char) b);
                        }
                }
                String output = new String(sb);
                return output;
        }


    private static void print (String str, boolean isSub) {
        if (isSub) System.out.print("\t");
        System.out.println(str);
    }

    private static void getNicInfo(NetworkInterface nic, boolean isSub) {
        if (isSub) System.out.print("\t");
        print(nic.getName(), isSub);
        Enumeration<InetAddress> nicAddresses = nic.getInetAddresses();
        while (nicAddresses.hasMoreElements()) {
            print(("\t" + nicAddresses.nextElement()), isSub);
        }
    }

    private static void getNicInfo(Enumeration<NetworkInterface> nics, boolean isSub) throws SocketException {
        while (nics.hasMoreElements()) {
            NetworkInterface nic = nics.nextElement();
            if (nic.isUp()) {
                // If nic is subinterface (virtual), then print tab;
                getNicInfo(nic, isSub);
                Enumeration<NetworkInterface> subNics = nic.getSubInterfaces();
                if (subNics.hasMoreElements()) {
                    getNicInfo(subNics, true);
                }
            }
        }
    }
}
