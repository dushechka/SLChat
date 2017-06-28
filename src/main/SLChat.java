package main;

import client.model.Client;
import client.model.RoomsGetter;
import client.view.start.StartView;
import javafx.stage.Stage;
import server.model.Server;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import java.util.SortedMap;

import static server.model.ServerConstants.*;

/**
 * Main entrance to the whole program.
 * <p>
 * This static class Starts the main GUI
 * window and also guides program workflow,
 * serving as intermedium between other
 * objects in program. Contains methods for
 * getting service information ({@link #getIP()}
 * for example).
 *
 * @author  Andrey Fadeev
 * @since 0.2
 */
public class SLChat {
    /** Is used to stop {@link server.model.Server} thread. */
        public static boolean IS_SERVER_RUNNING;
    /** Is used to stop {@link client.model.Client} thread. */
        public static boolean IS_CLIENT_RUNNING;
    /** Is used, to determine, if
     *  {@link server.model.ClientConnector} already running, so
     *  the {@link client.model.Client} can connect to server */
        public static boolean IS_CLIENT_CONNECTOR_RUNNING;
    /** Server backend thread. */
        public static Server SLServer;
    /** Client backend thread. */
        public static Client SLClient;
    /** Main GUI window javafx stage. */
        public static Stage primaryStage;
    /** Main menu window backend instance. */
        public static StartView mainView;
    /** Client's GUI fxml file path. */
        public static final String CLIENT_GUI_PATH = "/client/view/chat/Chat.fxml";
    /** Login GUI fxml file path. */
        public static final String LOGIN_GUI_PATH = "/client/view/login/Login.fxml";
    /** Search GUI fxml file path. */
        public static final String SEARCH_GUI_PATH = "/client/view/search/Search.fxml";
    /** Create room GUI fxml file path. */
        public static final String CREATE_GUI_PATH = "/server/view/create/Create.fxml";
    /** Main window GUI fxml file path. */
        public static final String START_GUI_PATH = "/client/view/start/Start.fxml";
    /** A list of system's running network interfaces */
        private static ArrayList<NetworkInterface> liveNICs;
    /** Preffered network interface, to run client from. */
        private static NetworkInterface prefferedInterface;
    /** A list of system rinning network interfaces' IPv4 addresses */
        public static ArrayList<InetAddress> inetAddresses;
    /** A list of system rinning network interfaces' IPv4 addresses, that support multicasting */
    public static ArrayList<InetAddress> mCastAddresses;
    /** {@link #prefferedInterface} address, with wich program will work. */
        public static InetAddress prefferedAddress;
    /** Interface, which is used to make local connections. */
        public static InetAddress localAddress;
    /** A list of rooms, found on LAN */
        public static Hashtable<String, InetAddress> rooms;
        public static PrintStream history;
    /** Determines, wheter system supports {@link server.model.ServerConstants#DEFAULT_CHARSET} */
        private final boolean IS_DEFAULT_CHARSET_SUPPORTED;
    /** A path to file, which contains program state variables */
        private static String propertiesFilePath;
    /** Program state variables */
        private static Properties properties;


    /**
     * Main program entry.
     */
    public SLChat() {
        IS_DEFAULT_CHARSET_SUPPORTED = isCharsetPresent(DEFAULT_CHARSET);
        /* duplicating standart output to log files */
        try {
            if (IS_DEFAULT_CHARSET_SUPPORTED) {
                catchSOut(DEFAULT_CHARSET);
            } else {
                catchSOut(null);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Program can't find the file!");
            e.printStackTrace();
        } catch (UnsupportedEncodingException exc) {
            exc.printStackTrace();
        }

        /* Making a file for history, initializing a stream
           to write to it and saving that stream to corresponding
            field so the Client class could write to it. */
        try {

            String historyPath = makeFolder(HISTORY_FOLDER_NAME
                                                            + "/" + LocalDate.now().toString().substring(0,10));
            if (IS_DEFAULT_CHARSET_SUPPORTED) {
                System.out.println("***" + DEFAULT_CHARSET + " IS SUPPORTED***");
                history = makeFileOutput(historyPath, TXT_APPENDIX, DEFAULT_CHARSET);
            } else {
                System.out.println("***" + DEFAULT_CHARSET + " IS NOT SUPPORTED***");
                history = makeFileOutput(historyPath, TXT_APPENDIX, null);
            }
        } catch (FileNotFoundException exc) {
            exc.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            IS_SERVER_RUNNING = false;
            IS_CLIENT_RUNNING = false;
            IS_CLIENT_CONNECTOR_RUNNING = false;
            localAddress = InetAddress.getByName("localhost");
            /* choosing live network interface to work with */
            liveNICs = getLiveNICs();
            prefferedInterface = chooseInterface(liveNICs);
            System.out.println("Preffered interface: " + prefferedInterface.getName());
            System.out.println("Does interface support multicasting: " + prefferedInterface.supportsMulticast());
            /* getting a list of all available running interfaces IPv4 addresses */
            inetAddresses = new ArrayList<>();
            mCastAddresses = new ArrayList<>();
            getIfacesAddresses(liveNICs, inetAddresses, false);
            getIfacesAddresses(liveNICs, mCastAddresses, true);
            prefferedAddress = getIfaceAddress(prefferedInterface);
            System.out.println("Preffered address is: " + prefferedAddress);
            /* starting program GUI */
            new Thread() {
                @Override
                public void run() {
                    javafx.application.Application.launch(StartView.class);
                }
            }.start();
        } catch (SocketException se) {
            System.out.println("Looks like program can't retrieve IPv4 address from interface.");
            se.printStackTrace();
        } catch (Error err) {
            System.out.println("It looks like the network subsystem is unavailable.");
            err.printStackTrace();
        } catch (IndexOutOfBoundsException exc) {
            System.out.println("It looks, like no live network interfaces is available.");
        } catch (UnknownHostException e){}
    }

    /**
     * Starts main GUI window.
     * <p>
     * Instantiates new thread of class
     * {@link client.view.start.StartView Startview}.
     *
     * @param args  never used.
     */
    public static void main(String[] args) {
            /* checking if program is running first */
            propertiesFilePath = makeFolder(CFG_FOLDER_NAME) + "/properties";
            File propertiesFile = new File(propertiesFilePath);
            properties = new Properties();
        try {
            if (propertiesFile.exists()) {
                FileInputStream PFIn = new FileInputStream(propertiesFile);
                properties.load(PFIn);
                PFIn.close();
                if (properties.getProperty("isRunning").equals("true")) {
                    /* exiting, if program is already running */
                    System.exit(0);
                }
            }
            properties.setProperty("isRunning", "true");
            storeProperties(properties, propertiesFile);
            new SLChat();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static void storeProperties(Properties prop) throws IOException {
        storeProperties(prop, new File(propertiesFilePath));
    }

    private static void storeProperties(Properties prop, File propFile) throws IOException {
        FileOutputStream PFOut = new FileOutputStream(propFile);
        prop.store(PFOut, null);
        PFOut.close();
    }

    /**
     */
    public static void getIP() {
            int i = 1;
            ArrayList<RoomsGetter> getters = new ArrayList<>();
            /* time, at which RoomsGetter threads will work */
            final int WORKING_TIME = 100;

        rooms = new Hashtable<>();
        long startTime = System.currentTimeMillis();
        /* Creating a pool of RoomsGetters, to get servers
           InetAddresses from LAN's on all network interfaces. */
        System.out.println("Starting getters and seekers.");
        for (InetAddress iFace : inetAddresses) {
            RoomsGetter getter = new RoomsGetter(iFace, i);
            getter.start();
            getters.add(getter);
            i++;
        }
        System.out.println("Seekers and getters started.");
        /* waiting for a given period of time */
        try {
            while ((System.currentTimeMillis() - startTime) < WORKING_TIME) {
                Thread.sleep(100);
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        System.out.println("Killing seekers and getters.");
        for (RoomsGetter gt : getters) {
            gt.die();
            rooms.putAll(gt.getRooms());
        }
        System.out.println("A list of gained rooms:");
        for (String roomName : rooms.keySet()) {
            System.out.println(roomName);
        }
    }



    /**
     * Invokes {@link #connectClient(InetAddress)}
     * method and supplies serverAddress field
     * to it in {@link InetAddress} format.
     *
     * @param serverAddress server's address
     *                      to connect.
     */
    public static void connectClient(String serverAddress) {
        try {
            connectClient(InetAddress.getByName(serverAddress));
        } catch (UnknownHostException exc) {
            mainView.alertWindow("Wrong", "Server not found!");
        }
    }

    /**
     * Connects to server and
     * changes view to login
     * window if success.
     *
     * @see client.view.login.LoginController
     * @param serverAddress server's address
     *                      to connect.
     */
    public static void connectClient(InetAddress serverAddress) {
        try {
            /*
             * Attempting to start client with
             * gained server address. If failed
             * to start, {@link client.model.Client}
             * will throw an exception.
             */
            if (Client.connectClient(serverAddress)) {
                mainView.openNewWindow(LOGIN_GUI_PATH);
            } else {
                mainView.alertWindow("Wrong.", "Server not found!");
            }
        } catch (IOException e) {
            mainView.alertWindow("Oops!", "Sorry... Can't connect to server. :'-(");
            System.out.println("Couldn't open login window.");
        }
    }

    /**
     * Intercepts given output
     * stream (realPrintStream),
     * prints message to it and
     * duplicates to given
     * PrintStream (newOutputStream).
     *
     * @param realPrintStream   PrintStram to catch messages.
     * @param newOutputStream   PrintStrim to duplicate messages.
     * @return  new PrintStream, which prints messages to both streams.
     */
    private static PrintStream duplicatePrintStream(PrintStream realPrintStream, PrintStream newOutputStream) {

            return new PrintStream(realPrintStream) {

                @Override
                public void print(final String str) {
                    realPrintStream.print(str);
                    newOutputStream.print(str);
                }

                /* Overriding this method, so the output
                   strings in file could be separated. */
                @Override
                public void println(final String str) {
                    realPrintStream.println(str);
                    newOutputStream.println(LocalTime.now() + ": " + str);
                }
        };
    }

    /**
     * Opens new PrintStream at
     * {@link FileOutputStream}
     * with given file path.
     *
     * @param folderPath    a path to folder,
     *                      where file resides.
     * @param fileName      a name of output file.
     * @param encoding      encoding, at which to
     *                      save a stream of symbols.
     * @return  opened PrintStream to file.
     * @throws FileNotFoundException    not checked yet.
     * @throws UnsupportedEncodingException when given encoding
     *                                      is not suppoerted
     *                                      by system.
     */
    private static PrintStream makeFileOutput(String folderPath, String fileName, String encoding)
                                                throws FileNotFoundException, UnsupportedEncodingException {

        /* determining full path to output file */
        String filePath = folderPath + "/";
//        filePath += LocalDate.now().toString().substring(0,10) + "_";
        filePath += LocalTime.now().toString().substring(0,8).replace(':','-');
        filePath += "_" + fileName;

        File file = new File(filePath);
        FileOutputStream fileOutputStream = new FileOutputStream(filePath, true);
        if (encoding != null) {
            return new PrintStream(new FileOutputStream(new File(filePath), true),
                    true, DEFAULT_CHARSET);
        } else {
            return new PrintStream(new FileOutputStream(new File(filePath), true),
                    true);
        }
    }

    /**
     * Creates folder for program.
     * <p>
     * Creates folder by given name
     * in folder, named "SLChat", which
     * is located in user's home folder.
     *
     * @param folderName    Folder name to create.
     * @return Full path to the folder, in
     *         correct system style, even if
     *         folder wasn't creates because
     *         it is already exists.
     */
    private static String makeFolder(String folderName) {
        String s = "/";
        String home = System.getProperty("user.home");
        String folderPath = home + s + PROGRAM_FOLDER_NAME + s + folderName;
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder.toString();
    }

    /**
     * Catches default systems
     * output streams and
     * duplicates them to output
     * files.
     *
     * @param encoding  encoding in which to save output.
     * @see    #makeFileOutput(String, String, String)
     * @see    #duplicatePrintStream(PrintStream, PrintStream)
     * @throws FileNotFoundException    when cannot assign
     *                                  file output in
     *                                  supplementary method.
     * @throws UnsupportedEncodingException when system does not
     *                                      support used encoding.
     */
    private static void catchSOut(String encoding) throws FileNotFoundException, UnsupportedEncodingException {
        String logPath = makeFolder(LOG_FOLDER_NAME + "/" + LocalDate.now().toString().substring(0,10));
        System.setOut(duplicatePrintStream(System.out, makeFileOutput(logPath, (OUT_FILE_PATH + TXT_APPENDIX),
                                                                                                encoding)));
        System.setErr(duplicatePrintStream(System.err, makeFileOutput(logPath, (ERR_FILE_PATH + TXT_APPENDIX),
                                                                                                encoding)));
    }

    /**
     * Determines, whether system
     * supports charset, given by
     * charsetName parameter.
     *
     * @param charsetName   a charset
     *                      to be found.
     * @return  true, if given charset
     *          is supported by a system.
     */
    private static boolean isCharsetPresent(String charsetName) {
            SortedMap<String, Charset> charsetsMap = Charset.availableCharsets();
        for(String name : charsetsMap.keySet()) {
            if (charsetName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static Properties getProperties() {
        return properties;
    }
}