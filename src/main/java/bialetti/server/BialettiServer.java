package bialetti.server;

import bialetti.BialettiConnection;
import bialetti.BialettiExceptionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class BialettiServer {
    /*
     * An extension of BialettiConnection that also holds the handler thread and
     * provides a server-specific close() method
     * @author Alessandro-Salerno
     */
    protected final class BialettiServerConnection extends BialettiConnection {
        /*
         * The thread that handles the connection
         */
        private final BialettiServerThread mThread;

        /*
         * Default constructor
         * @param socket The client's socket
         * @param server The server instance
         */
        public BialettiServerConnection(Socket socket) {
            super(socket);
            mThread = new BialettiServerThread(this, BialettiServer.this, connectionEventHandler, exceptionHandler);
        }

        /*
         * Closes the connection to the client
         */
        @Override
        public void close() throws IOException {
            super.close();
            connectedClients.remove(this);
            connectionEventHandler.onClose(this, BialettiServer.this);
            getThread().interrupt();
        }

        /*
         * Getter for the handler thread
         */
        public BialettiServerThread getThread() { return mThread; }
    }

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
    protected List<BialettiServerConnection> connectedClients;
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
        }, new BialettiExceptionHandler() {});
    }

    /*
     * Stops the server
     */
    public void stop() throws IOException {
        // Close all connections
        for (BialettiServerConnection connection : connectedClients) {
            connection.close();
        }

        // Clear list of connections and close server socket
        connectedClients.clear();
        serverSocket.close();

        try {
            // Call handler method
            serverEventHandler.onStop(this);
        }

        // Exception handler
        catch (Exception e) {
            // Call handler method
            exceptionHandler.raise(e, this);
        }
    }

    /*
     * Starts the server
     * Creates ServerSocket instance
     */
    public void start() {
        if (serverSocket != null) {
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
            e.printStackTrace();
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
            exceptionHandler.raise(e, this);
        }
    }

    /*
     * Listens for incoming connections
     */
    protected void listen() {
        try {
            // Accept connections forever
            while (true) {
                // Wait for a client to connect
                BialettiServerConnection newClient = new BialettiServerConnection(serverSocket.accept());

                // Append client to the list of connected clients
                connectedClients.add(newClient);

                // Start handler thread
                newClient.getThread().start();
            }
        }

        // Exception handler
        catch (Exception e) {
            // Call handler method
            exceptionHandler.raise(e, this);
        }
    }

    /*
     * Getter for the server's host port
     */
    public int getPort() { return serverPort; }
}
