package server.model;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Static class, containing
 * ancillary fields and methods
 * for use by other program classes.
 *
 * @see #SERVER_STRING
 * @see #TIME_HAS_EXPIRED
 * @see #SERVER_FINAL_PORT
 * @see #SERVER_MULTI_PORT
 * @see #CLIENT_MULTICAST_PORT
 * @see #CLIENT_PORT
 * @see #GROUP_ADDRESS
 * @see #byteToString(byte[])
 * @see #stringToByte(String)
 */
public class ServerConstants {
        /** String, that {@link client.model.Seeker} and
         * {@link server.model.ClientNotifier}
         *  are using to identify, that datagrams,
         *  which they receive belongs to each other.
         */
        public final static byte[] SERVER_STRING = stringToByte("SLChat");
        /** Message, that {@link client.model.Seeker}
         * sends to itself, to stop listening for server's
         * answer, if it is not responding. */
        public final static byte[] TIME_HAS_EXPIRED = stringToByte("TimeHasExpired");
        /** This port is used by client and server
         * to establish connection with each other. */
        public final static int SERVER_FINAL_PORT = 4488;
        /** Port, on which client sends multicast packets
         * and server listens for them in order, for them
         * to find each other. */
        public final static int SERVER_MULTI_PORT = 4444;
        public final static int CLIENT_MULTICAST_PORT = 4445;
//                public final static int SERVER_PORT = 4446;
        public final static int CLIENT_PORT = 4447;
        /** Broadcast group address. */
        public final static String GROUP_ADDRESS = "230.0.0.1";
        /** A default inner program charset name for converting
        *  strings to arrays of byte and backwards by
        *  {@link #byteToString(byte[])} and
        *  {@link #stringToByte(String)} methods.
        */
        private final static String DEFAULT_CHARSET = "UTF-8";

    /**
     * Forbidden method, because of
     * static nature of this class.
     * <p>
     * This method should never be
     * used, because this class does
     * not need to be instantiated.
     * <p>
     * @deprecated No replacement.
     */
    @Deprecated
    public ServerConstants(){}

    /**
     * Converts String type to
     * an array of bytes.
     *
     * @param input     some String message;
     * @return          array of byte, constructed
     *                  from input string, using
     *                  UTF-16 charset;
     */
        public static byte[] stringToByte(String input) {
            return input.getBytes(Charset.forName(DEFAULT_CHARSET));
        }

    /**
     * Converts an array
     * of bytes to a String.
     *
     * @param input An initial array of bytes;
     * @return      String message in UTF-16,
     *              gained in
     *              process of array conversion;
     */
    public static String byteToString(byte[] input) {
        return (new String(input, Charset.forName(DEFAULT_CHARSET)));
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
     * Prints all system's running
     * network interfaces info.
     * <p>
     * Prints all system's running NICs'
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
     * Gets a list of system's running NICs.
     *
     * @return  a list of all system's live network interfaces;
     * @throws SocketException  when can't retrieve
     *                          a list of interfaces;
     */
    private static ArrayList<NetworkInterface> getLiveNICs() throws SocketException {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            ArrayList<NetworkInterface> uPnets = new ArrayList<>();
        for (NetworkInterface netIf : Collections.list(nets)) {
            if (netIf.isUp()) {
                uPnets.add(netIf);
            }
        }
        return uPnets;
    }

    /**
     * Chooses running network
     * interface to work with.
     * <p>
     * Gets a list of system's
     * running interfaces and tries
     * to get a wired interface
     * first, then if none, attempts
     * to get wireless interface. If
     * none of them were found,
     * returns null.
     * @return  running network interface
     *          to work with or null if none
     * @throws SocketException  when can't get an
     *                          enumeration of
     *                          interfaces from system;
     */
    public static NetworkInterface chooseInterface() throws SocketException {
            NetworkInterface iFace = null;
            /* running interfaces */
            ArrayList<NetworkInterface> uPnets = getLiveNICs();

        System.out.println("A list of system's running interfaces:");
        for (NetworkInterface netIf : uPnets) {
            System.out.print(netIf.getName() + " ");
        }
        System.out.println();

        /* searching for proper interface */
        iFace = chooseInterface(uPnets, "eth");
        if (iFace == null) {
            iFace = chooseInterface(uPnets, "wlan");
        }
        if (iFace == null) {
            iFace = chooseInterface(uPnets, "wan");
        }
        if (iFace == null) {
            iFace = uPnets.get(0);
        }
        return iFace;
    }

    /**
     * Searches for the interface by
     * a keyword in it's name.
     * <p>
     * Searches in a list of network
     * interfaces for the first one
     * with the name, containing the
     * key.
     *
     * @param nics  List of network interfaces
     * @param key   String key
     * @return      Found interface and
     *              <code>null</code> if none
     */
    private static NetworkInterface chooseInterface(ArrayList<NetworkInterface> nics, String key) {
            NetworkInterface nic = null;
        for (NetworkInterface iFace : nics) {
            if (iFace.getName().contains(key)) {
                nic = iFace;
                break;
            }
        }
        return nic;
    }
}
