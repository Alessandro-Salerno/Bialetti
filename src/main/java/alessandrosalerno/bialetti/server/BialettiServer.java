package alessandrosalerno.bialetti.server;

import alessandrosalerno.bialetti.BialettiConnection;

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
    protected final BialettiConnectionEventHandler connectionEventHandler;
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
     * Default constructor
     * @param port The port on which the server is hosted
     * @param clientHandler BialettiConnectionEventHandler instance
     */
    public BialettiServer(int port, BialettiConnectionEventHandler clientHandler) {
        serverPort             = port;
        connectionEventHandler = clientHandler;
        connectedClients       = new ArrayList<>();
        serverThreads          = new ArrayList<>();

        // Create thread to listen to incoming requests
        listenThread = new Thread(this::listen);
        listenThread.start();
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

                // Create new thread
                BialettiServerThread sThread = new BialettiServerThread(newClient, this, connectionEventHandler);

                // Append client to the list of connected clients and start the thread
                connectedClients.add(newClient);
                serverThreads.add(sThread);

                // Start handler thread
                sThread.start();
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
        serverThreads.remove(sThread);
        connectionEventHandler.onClose(connection,this);
        sThread.interrupt();
    }

    /*
     * Getter for the server's host port
     */
    public int getPort() { return serverPort; }
}
