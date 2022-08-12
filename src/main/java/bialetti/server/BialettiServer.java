package bialetti.server;

import bialetti.BialettiConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public abstract class BialettiServer<T> {
    /*
     * An extension of BialettiConnection that also holds the handler thread and
     * provides a server-specific close() method
     * @author Alessandro-Salerno
     */
    protected final class BialettiServerConnection extends BialettiConnection {
        /*
         * The thread that handles the connection
         */
        private final BialettiServerThread<T> mThread;
        /*
         * A T (ClientType) instance
         */
        private final T client;

        /*
         * Default constructor
         * @param socket The client's socket
         * @param server The server instance
         */
        public BialettiServerConnection(Socket socket) throws IOException {
            super(socket);
            client = getClientFromBialettiConnection(this);
            mThread = new BialettiServerThread<T>(client, BialettiServer.this, connectionEventHandler, exceptionHandler);
        }

        /*
         * Closes the connection to the client
         */
        @Override
        public void close() throws IOException {
            super.close();
            activeConnections.remove(this);
            connectionEventHandler.onClose(client, BialettiServer.this);
            getThread().interrupt();
        }

        /*
         * Getter for the handler thread
         */
        public BialettiServerThread<T> getThread() { return mThread; }
    }

    /*
     * The port on which the server is hosted
     */
    protected final int serverPort;
    /*
     * The BialettiEventHandler instance that implements
     * the necessary handler methods
     */
    protected final BialettiConnectionEventHandler<T> connectionEventHandler;
    /*
     * The event handler for server events
     */
    protected final BialettiServerEventHandler serverEventHandler;
    /*
     * The event handler for server exceptions
     */
    protected final BialettiServerExceptionHandler<T> exceptionHandler;
    /*
     *  List of active connections
     */
    protected List<BialettiServerConnection> activeConnections;
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
    public BialettiServer(int port, BialettiConnectionEventHandler<T> clientHandler, BialettiServerEventHandler serverHandler, BialettiServerExceptionHandler<T> exHandler) {
        serverPort             = port;
        connectionEventHandler = clientHandler;
        serverEventHandler     = serverHandler;
        exceptionHandler       = exHandler;
        activeConnections      = new ArrayList<>();

        // Start the server
        start();
    }

    /*
     * Sends the same message to all active connections
     * @param message The message to be broadcasted
     */
    public void broadcast(String message) {
        activeConnections.forEach(connection -> connection.send(message));
    }

    /*
     * Stops the server
     */
    public void stop() throws IOException {
        // Clear list of connections and close server socket
        activeConnections.clear();
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
        catch (Exception e) {
            exceptionHandler.raise(e, this);
            return;
        }

        // Create thread to listen to incoming requests
        listenThread = new Thread(() -> listen());
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
            while (!Thread.interrupted()) {
                // Wait for a client to connect
                BialettiServerConnection newConnection = new BialettiServerConnection(serverSocket.accept());

                // Append client to the list of connected clients
                activeConnections.add(newConnection);

                // Start handler thread
                newConnection.getThread().start();
            }

            // Stops the server when the thread is interrupted
            stop();
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

    /*
     * User-defined method
     * Returns a T instance (ClientType) given a connection
     * @param bialettiConnection The newly established connection
     */
    protected abstract T getClientFromBialettiConnection(BialettiConnection bialettiConnection);
}
