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
 */
public class ServerConstants {
    /** String, that {@link client.model.Seeker} and {@link server.model.ClientNotifier}
        are using to identify, that datagrams, which they receive belongs to each other. */
        public final static byte[] SERVER_STRING = stringToByte("SLChat");
    /** Message, that {@link client.model.Seeker} sends to itself,
        to stop listening for server's answer, if it is not responding. */
        public final static byte[] TIME_HAS_EXPIRED = stringToByte("TimeHasExpired");
    /** This port is used by client and server
        to establish connection with each other. */
        public final static int SERVER_FINAL_PORT = 4488;
    /** Port, on which client sends multicast packets and
        server listens for them in order, to find each other. */
        public final static int SERVER_MULTI_PORT = 4444;
    /** Port, from which {@link client.model.Seeker} sends multicast packets */
        public final static int CLIENT_MULTICAST_PORT = 4848;
    /** Port, on which client receives server's answer packet,
        and retrieves servers IP address from it */
        public final static int CLIENT_PORT = 8484;
    /** Broadcast group address at which {@link client.model.Seeker sends packets}. */
        public final static String GROUP_ADDRESS = "230.0.0.1";
    /** Charset, which is used for output (to files for example) */
        public final static String DEFAULT_CHARSET = "UTF-8";
    /** Charset, which is used in java runtime only and never for output */
        private final static String INNER_CHARSET = "UTF-8";
    /** A path to program log folder, which containing program service messages and exceptions */
        public final static String LOG_FOLDER_NAME = "log";
    /** A path to program history folder, which containing chat messages */
        public final static String HISTORY_FOLDER_NAME = "history";
    /** A path to folder, which contains program output files */
        public final static String PROGRAM_FOLDER_NAME = "SLChat";
    /** Path to program's output file */
        public final static String OUT_FILE_PATH = "out";
    /** Path to program's error output file */
        public final static String ERR_FILE_PATH = "err";
    /** An extention of output text files, so systems could define them as so */
        public final static String TXT_APPENDIX = ".txt";

    /**
     * Forbidden method, because of
     * static nature of this class.
     * <p>
     * This method should never be
     * used, because this class does
     * not need to be instantiated.
     *
     * @deprecated No replacement.
     */
    @Deprecated
    public ServerConstants(){}

    /**
     * Converts String object to
     * an array of bytes.
     *
     * @param input     some String message.
     * @return          array of bytes, constructed
     *                  from input string, using
     *                  {@link #DEFAULT_CHARSET}.
     */
        public static byte[] stringToByte(String input) {
            return input.getBytes(Charset.forName(INNER_CHARSET));
        }

    /**
     * Converts an array
     * of bytes to a String.
     *
     * @param input An initial array of bytes.
     * @return      String message in
     *              {@link #DEFAULT_CHARSET},
     *              gained in
     *              process of array conversion.
     */
    public static String byteToString(byte[] input) {
        return (new String(input, Charset.forName(INNER_CHARSET)));
        }

    /**
     * Gets a list of system's running NICs.
     *
     * @return  a list of all system's live network interfaces.
     * @throws SocketException  when can't retrieve
     *                          a list of interfaces.
     */
    public static ArrayList<NetworkInterface> getLiveNICs() throws SocketException {
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
     * Gets interface address, from
     * which to send packets.
     *
     * @param nif   A network interface, for
     *              which to get an address.
     * @return      {@link InetAddress} from
     *              which to send packets.
     * @throws SocketException  When cannot get
     *                          address by name.
     */
    public static InetAddress getIfaceAddress(NetworkInterface nif) throws SocketException {
            InetAddress address = null;
        System.out.println("Extracting IPv4 address from " + nif.getName() + ": " + nif.getDisplayName());
        for (InetAddress addr : Collections.list(nif.getInetAddresses())) {
            /* selects an IPv4 address, assigned to interface */
            if (addr.toString().length() <= 16) {
                address = addr;
            }
        }
        return address;
    }

    /**
     * Gets interface address, which
     * supports multicasting.
     *
     * @param nif   A network interface, for
     *              which to get an address.
     * @return      {@link InetAddress} which
     *              supports multicasting.
     * @throws SocketException  When cannot get
     *                          address by name.
     */
    private static InetAddress getMcastAddress(NetworkInterface nif) throws SocketException {
        InetAddress address = null;
        System.out.println("Extracting multicast address from " + nif.getName() + ": " + nif.getDisplayName());
        for (InetAddress addr : Collections.list(nif.getInetAddresses())) {
            /* selects an IPv4 address, assigned to interface */
            if (addr.toString().length() <= 16 && nif.supportsMulticast()) {
                address = addr;
            }
        }
        return address;
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
     *
     * @param   uPnets  A list of found
     *                  running system's
     *                  network interfaces.
     * @see     #getLiveNICs()
     * @return  running network interface
     *          to work with or null if none.
     * @throws SocketException  when can't get an
     *                          enumeration of
     *                          interfaces from system.
     */
    public static NetworkInterface chooseInterface(ArrayList<NetworkInterface> uPnets) throws SocketException {
            NetworkInterface iFace = null;
            /* running interfaces */

        System.out.println("A list of system's running interfaces:");
        for (NetworkInterface netIf : uPnets) {
            System.out.print(netIf.getName() + " ");
        }
        System.out.println();

        /* searching for proper interface */
        iFace = chooseInterface(uPnets, "wlan");
        if (iFace == null) {
            iFace = chooseInterface(uPnets, "wan");
        }
        if (iFace == null) {
            iFace = chooseInterface(uPnets, "eth");
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
     * @param nics  List of network interfaces;
     * @param key   String key;
     * @return      Found interface and
     *              <code>null</code> if none.
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

    /**
     * Extracts all the IPv4
     * addresses from system
     * running network interfaces.
     *
     * @param mCast Determines, if
     *              work only with
     *              interfaces, which
     *              support multicast.
     *
     * @param nics  List of system's
     *              running interfaces.
     * @param addresses List, to which
     *                  this method
     *                  saves addresses.
     * @throws SocketException  when cannot interconnect
     *                          with some of the interfaces.
     */
    public static void getIfacesAddresses(ArrayList<NetworkInterface> nics,
                                          ArrayList<InetAddress> addresses, boolean mCast) throws SocketException {
            InetAddress address;
        for (NetworkInterface iface : nics) {
            if (mCast) {
                address = getMcastAddress(iface);
            } else {
                address = getIfaceAddress(iface);
            }
            if (address != null) {
                System.out.println("Adding address to list: " + address);
                addresses.add(address);
            }
        }
    }
}
