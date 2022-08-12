package bialetti.server;

import bialetti.BialettiConnection;
import jdk.net.Sockets;

import java.io.IOException;
import java.net.InetSocketAddress;
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
            client = getNewClient(this);
            mThread = new BialettiServerThread<T>(client, BialettiServer.this, exceptionHandler);
        }

        /*
         * Closes the connection to the client
         */
        @Override
        @SuppressWarnings("all")
        public void close() throws Exception {
            synchronized (activeConnections) {
                super.close();
                activeConnections.remove(this);
                ((BialettiServerClientRepresentation) client).onClose();
                getThread().interrupt();
            }
        }

        /*
         * Closes the connection without removing it from the list
         */
        @SuppressWarnings("all")
        public void justClose() throws Exception {
            super.close();
            ((BialettiServerClientRepresentation) client).onClose();
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
     * The event handler for server exceptions
     */
    protected final BialettiServerExceptionHandler<T> exceptionHandler;
    /*
     *  List of active connections
     */
    protected final List<BialettiServerConnection> activeConnections;
    /*
     * The SocketServer instance
     */
    protected final ServerSocket serverSocket;
    /*
     * Thread on which the server's listen method is run
     */
    protected Thread listenThread;

    /*
     * Default constructor
     * @param port The port on which the server listens
     * @param sxh An exception handler for the server
     */
    public BialettiServer(int port, BialettiServerExceptionHandler<T> sxh) {
        // Set fields
        serverPort        = port;
        exceptionHandler  = sxh;
        activeConnections = new ArrayList<>();

        // Open connection
        ServerSocket nServer;
        try { nServer = new ServerSocket(serverPort); }
        catch (Exception e) {
            // Call handler method
            exceptionHandler.raise(e, this);

            // Abort
            serverSocket = null;
            return;
        }

        // Save server socket in field
        serverSocket = nServer;

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
    public void stop() throws Exception {
        synchronized (activeConnections) {
            // Close all connections
            for (BialettiServerConnection connection : activeConnections) {
                connection.justClose();
            }

            // Clear list of active connections and ensure that no other thread can access it
            activeConnections.clear();
        }

        // Stop listening
        listenThread.interrupt();


        try { onStop(); }
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
        if (!serverSocket.isBound()) {
            // Reopen socket]
            try { serverSocket.bind(new InetSocketAddress("", serverPort)); }
            catch (Exception e) {
                // Call handler method
                exceptionHandler.raise(e, this);
            }
        }

        // Start listen thread
        listenThread = new Thread(this::listen);
        listenThread.start();

        try { onStart(); }
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
    protected abstract T getNewClient(BialettiConnection bialettiConnection);
    /*
     * What happens when the server is started
     */
    protected abstract void onStart() throws Exception;
    /*
     * What happens when the server is stopped
     */
    protected abstract void onStop() throws Exception;
}
