package alessandrosalerno.bialetti.server;

import alessandrosalerno.bialetti.BialettiConnection;
import alessandrosalerno.bialetti.BialettiExceptionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
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
     * The event handler for server events
     */
    protected final BialettiServerEventHandler serverEventHandler;
    /*
     * The event handler for server exceptions
     */
    protected final BialettiExceptionHandler exceptionHandler;
    /*
     *  List of connected clients
     */
    protected List<BialettiConnection> connectedClients;
    /*
     * List of threads dedicated to handling clients
     */
    protected List<BialettiServerThread> serverThreads;
    /*
     * The SocketServer instance
     */
    protected ServerSocket serverSocket;
    /*
     * Thread on which the server's listen method is run
     */
    protected Thread listenThread;

    /*
     * Default constructor
     * @param port The port on which the server is hosted
     * @param clientHandler BialettiConnectionEventHandler instance
     * @param serverHandler BialettiServerEventHandler instance
     * @param exHandler The server's exception handler
     */
    public BialettiServer(int port, BialettiConnectionEventHandler clientHandler, BialettiServerEventHandler serverHandler, BialettiExceptionHandler exHandler) {
        serverPort             = port;
        connectionEventHandler = clientHandler;
        serverEventHandler     = serverHandler;
        exceptionHandler       = exHandler;
        connectedClients       = new ArrayList<>();
        serverThreads          = new ArrayList<>();

        // Start the server
        start();
    }

    /*
     * Secondary constructor
     */
    public BialettiServer(int port, BialettiConnectionEventHandler clientHandler) {
        this(port, clientHandler, new BialettiServerEventHandler() {
            @Override
            public void onStart(BialettiServer server) throws Exception {
                System.out.println("[+] Server started");
            }

            @Override
            public void onStop(BialettiServer server) throws Exception {
                System.out.println("[+] Server stopped");
            }
        }, new BialettiExceptionHandler() {
            @Override
            public void onGenericException(Exception exception) {
                exception.printStackTrace();
            }

            @Override
            public void onIOException(IOException ioException) {
                ioException.printStackTrace();
            }

            @Override
            public void onSocketException(SocketException socketException) {
                socketException.printStackTrace();
            }
        });
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
     * Stops the server
     */
    public void stop() throws IOException {
        // Clear list of clients
        connectedClients.clear();

        // Stop all threads
        for (BialettiServerThread sThread : serverThreads) {
            sThread.interrupt();
        }

        // Clear threads list and close the server socket
        serverThreads.clear();
        serverSocket.close();

        try {
            // Call handler method
            serverEventHandler.onStop(this);
        }

        // Exception handler
        catch (Exception e) {
            // Call handler method
            exceptionHandler.onGenericException(e);
        }
    }

    /*
     * Starts the server
     * Creates ServerSocket instance
     */
    protected void start() {
        if (serverSocket != null && serverSocket.isClosed()) {
            serverSocket = null;
            start();
            return;
        }

        try {
            // Create server socket
            serverSocket = new ServerSocket(serverPort);
        }

        // IOException handler
        catch (IOException e) {
            // Call handler method
            exceptionHandler.onIOException(e);
        }

        // Create thread to listen to incoming requests
        listenThread = new Thread(this::listen);
        listenThread.start();

        try {
            // Call handler method
            serverEventHandler.onStart(this);
        }

        // Exception handler
        catch (Exception e) {
            // Call handler method
            exceptionHandler.onGenericException(e);
        }
    }

    /*
     * Listens for incoming connections
     */
    protected void listen() {
        try {
            // Accept connections forever
            while (true) {
                // Wait for a client to connect and instantiate a BialettiConnection for it
                BialettiConnection newClient = new BialettiConnection(serverSocket.accept());

                // Create new thread
                BialettiServerThread sThread = new BialettiServerThread(newClient, this, connectionEventHandler, exceptionHandler);

                // Append client to the list of connected clients and start the thread
                connectedClients.add(newClient);
                serverThreads.add(sThread);

                // Start handler thread
                sThread.start();
            }
        }

        // Exception handler
        catch (Exception e) {
            // Call handler method
            exceptionHandler.onGenericException(e);
        }
    }

    /*
     * Getter for the server's host port
     */
    public int getPort() { return serverPort; }
}
