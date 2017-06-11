package server.model;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Static class, containing
 * ancillary fields and methods
 * for use by other program classes.
 * <p>
 *
 * @see #SERVER_STRING
 * @see #TIME_HAS_EXPIRED
 * @see #SERVER_FINAL_PORT
 * @see #SERVER_MULTI_PORT
 * @see #CLIENT_MULTICAST_PORT
 * @see #CLIENT_PORT
 * @see #GROUP_ADDRESS
 * @see #byteToString(byte[])
 * @see #stringToByte(String, byte[])
 * @see #getInterface(String)
 */
public class ServerConstants {
        /* String, that Seeker and ClientNotifier (client's and
         * server's classes respectively) are using to identify,
         * that datagrams, which they receive belongs to each other.
         */
        public final static String SERVER_STRING = "SLChat";
        /* Message, that Seeker sends to itself, to stop
         * listening for server's answer, if it is not
         * responding. */
        public final static String TIME_HAS_EXPIRED = "TimeHasExpired";
        /* This port is used by client and server
         * to establish connection with each other. */
        public final static int SERVER_FINAL_PORT = 4488;
        /* Port, on which client sends multicast packets
         * and server listens for them in order, for them
         * to find each other. */
        public final static int SERVER_MULTI_PORT = 4444;
        public final static int CLIENT_MULTICAST_PORT = 4445;
//                public final static int SERVER_PORT = 4446;
        public final static int CLIENT_PORT = 4447;
        /* Broadcast group address. */
        public final static String GROUP_ADDRESS = "230.0.0.1";


        /* Converts String to an array of bytes. */
        public static void stringToByte(String input, byte[] output) {
                int i = 0;
                for (char m : input.toCharArray()) {
                        output[i] = (byte) m;
                        i++;
                }
        }

        /* Converts an array of bytes to a String. */
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

    /**
     * Prints incoming string with
     * NIC info to standard output.
     *
     * @param str
     *              A string with NIC's info for output.
     * @param isSub
     *              Determines if NIC is sub interface.
     */
    private static void printNICInfo (String str, boolean isSub) {
        /* Prints tab to mark whether it is sub interface */
        if (isSub) System.out.print("\t");
        System.out.println(str);
    }

    /**
     * Prints incoming NIC info,
     * such as name and IP by calling
     * {@link #printNICInfo(String, boolean)}
     * method.
     * <p>
     * This method gives <code>isSub</code> boolean
     * parameter to {@link #printNICInfo(String, boolean)}
     * method, so it can print extra tab for sub interfaces.
     *
     * @param nic
     *              System's running {@link NetworkInterface}
     *              to be printed.
     * @param isSub
     *              Determines if interface is someone's sub interface.
     */
    private static void getNicInfo(NetworkInterface nic, boolean isSub) {
        if (isSub) System.out.print("\t");
        printNICInfo(nic.getName(), isSub);
        Enumeration<InetAddress> nicAddresses = nic.getInetAddresses();
        while (nicAddresses.hasMoreElements()) {
            printNICInfo(("\t" + nicAddresses.nextElement()), isSub);
        }
    }

    /**
     * Prints all systems running NICs'
     * info, such as IP and name
     * to the system standard output, by
     * invoking {@link #getNicInfo(NetworkInterface, boolean)}
     * method on each one of them.
     * <p>
     *
     * @param nics
     *              Enumeration of all systems networks
     *              interfaces, which can be received by
     *              {@link NetworkInterface#getNetworkInterfaces()}
     *              static method.
     * @param isSub
     *              Determines, whether <code>nics</code> param is
     *              {@link Enumeration} of sub interfaces, so the
     *              output of their IPs can be marked by additional tabs.
     *
     * @throws SocketException
     *                          When <code>nics</code> param doesn't
     *                          contain any {@link NetworkInterface}.
     *
     * @see #getNicInfo(NetworkInterface, boolean)
     * @see #printNICInfo(String, boolean)
     */
    private static void getNicInfo(Enumeration<NetworkInterface> nics, boolean isSub) throws SocketException {
        while (nics.hasMoreElements()) {
            NetworkInterface nic = nics.nextElement();
            if (nic.isUp()) {
                /* If nic is subinterface (virtual), then print tab. */
                getNicInfo(nic, isSub);
                Enumeration<NetworkInterface> subNics = nic.getSubInterfaces();
                if (subNics.hasMoreElements()) {
                    getNicInfo(subNics, true);
                }
            }
        }
    }

    /**
     * Gets interface address, from
     * which to send packets.
     *
     * @param iName  The name of interface for
     *               which to get IP-address.
     * @return  {@link InetAddress} from
     *          which to send packets.
     * @throws SocketException  When cannot get
     *                          address by name.
     */
    public static InetAddress getInterface(String iName) throws SocketException {
        NetworkInterface nif = NetworkInterface.getByName("eth3");
        System.out.println("Does interface support multicasting: " + nif.supportsMulticast());
        InetAddress socketAddress = nif.getInetAddresses().nextElement();
        return socketAddress;
    }
}
