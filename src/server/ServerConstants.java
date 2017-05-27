package server;

/**
 * Contains program final fields
 * and methods;
 */
public class ServerConstants {
        public final static String SERVER_STRING = "SLChat";
        public final static int SERVER_FINAL_PORT = 4488;
        public final static long FIVE_SECONDS = 5000;
        // These ports are used to client and server to find each other;
        public final static int CLIENT_PORT = 4445;
        public final static int SERVER_PORT = 4444;


        // Convert String to an array of bytes;
        public static void stringToByte(String input, byte[] output) {
                int i = 0;
                for (char m : input.toCharArray()) {
                        output[i] = (byte) m;
                        i++;
                }
        }

        // Convert an array of bytes to String;
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
