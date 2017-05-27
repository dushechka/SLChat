package server;

import java.net.InetAddress;

/**
 * Contains program final fields
 * and methods;
 */
public class ServerConstants {
        // String, that server is broadcasting to clients for server identification;
                public final static String SERVER_STRING = "SLChat";
        // This port is used for client to connect to server;
                public final static int SERVER_FINAL_PORT = 4488;
        // Broadcast group address;
                public final static String GROUP_ADDRESS = "230.0.0.1";
        // These ports are used to client and server to find each other;
                public final static int SERVER_PORT = 4444;
                public final static int CLIENT_PORT = 4445;
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
}
