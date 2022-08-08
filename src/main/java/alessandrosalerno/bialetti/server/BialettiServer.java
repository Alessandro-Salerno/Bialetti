package alessandrosalerno.bialetti.server;

import alessandrosalerno.bialetti.BialettiConnection;
import alessandrosalerno.bialetti.BialettiEventHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class BialettiServer {
    /*
     * The port on which the server is hosted
     */
    protected final int serverPort;
    /*
     * The BialettiEventHandler instance that implements
     * the necessary handler methods
     */
    protected final BialettiEventHandler bialettiEventHandler;
    /*
     *  List of connected clients
     */
    protected List<BialettiConnection> connectedClients;
    /*
     * List of threads dedicated to handling clients
     */
    protected List<BialettiServerThread> serverThreads;
    /*
     * Thread on which the server's listen method is run
     */
    protected Thread listenThread;


    /*
     * Default constructor (Used with anonymous classes)
     * @param port The port on which the server is hosted
     * @param clientHandler BialettiClientHandler instance
     */
    @SuppressWarnings("unchecked")
    public BialettiServer(int port, BialettiEventHandler clientHandler) {
        serverPort            = port;
        bialettiEventHandler = clientHandler;
        connectedClients      = new ArrayList<>();
        serverThreads         = new ArrayList<>();

        // Create thread to listen to incoming requests
        listenThread = new Thread() {
            @Override
            public void run() {
                listen();
            }
        };

        listenThread.start();
    }

    /*
     * Secondary constructor (Used with named classes)
     * @param port The port on which the server is hosted
     * @param handlerClass The BialettiClientHandler class itself
     */
    public BialettiServer(int port, Class<BialettiEventHandler> handlerClass) throws Exception {
        this(port, handlerClass.getDeclaredConstructor().newInstance());
    }

    /*
     * Listens for incoming connections
     */
    protected void listen() {
        // Create server socket
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            // Accept connections forever
            while (true) {
                // Wait for a client to connect and instantiate a BialettiConnection for it
                BialettiConnection newClient = new BialettiConnection(serverSocket.accept());

                BialettiServerThread sThread = new BialettiServerThread(newClient, this, bialettiEventHandler);
                sThread.start();

                // Append client to the list of connected clients and start the thread
                connectedClients.add(newClient);
                serverThreads.add(sThread);
            }
        }

        // Exception handler
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Closes a connection to a client
     * @param connection The BialettiConnection instance of the client
     */
    public void closeConnection(BialettiConnection connection) throws IOException {
        connection.getSocket().close();
        BialettiServerThread sThread = serverThreads.get(connectedClients.indexOf(connection));
        connectedClients.remove(connection);
        bialettiEventHandler.onClose(connection, this);
        sThread.interrupt();
        serverThreads.remove(sThread);
    }

    /*
     * Getter for the server's host port
     */
    public int getPort() { return serverPort; }
}
