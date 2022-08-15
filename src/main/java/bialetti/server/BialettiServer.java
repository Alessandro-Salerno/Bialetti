package bialetti.server;

import bialetti.annotations.BialettiHandleMethod;
import bialetti.connection.tcp.BialettiTCPConnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A Bialetti Server
 * @param <ClientType> the type that defines a client (Subclass of {@link BialettiServerClientRepresentation})
 * @author Alessandro-Salerno
 */
public abstract class BialettiServer<ClientType extends BialettiServerClientRepresentation<?>> {
    /**
     * The port on which the server is hosted
     */
    protected final int serverPort;
    /**
     * The event handler for server exceptions
     */
    protected final BialettiServerExceptionHandler<ClientType> exceptionHandler;
    /**
     *  List of active connections
     */
    protected final List<BialettiServerConnection> activeConnections;
    /**
     * The socket on which the server listens for new connections
     */
    protected final ServerSocket serverSocket;
    /**
     * The thread on which the server's listen method is run
     */
    protected Thread listenThread;

    /**
     * constructor
     * @param port the port on which the server listens
     * @param sxh an exception handler for the server
     */
    public BialettiServer(int port, BialettiServerExceptionHandler<ClientType> sxh) {
        // Set fields
        serverPort        = port;
        exceptionHandler  = sxh;
        activeConnections = new ArrayList<>();

        // Open connection
        serverSocket = openServerSocket();
        start();
    }

    /**
     * Sends the same message to all active connections
     * @param message the message to be broadcast
     */
    public void broadcast(String message) {
        activeConnections.forEach(connection -> connection.send(message));
    }

    /**
     * Starts the server
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

    /**
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

    /**
     * @return the server's port
     */
    public int getPort() { return serverPort; }

    /**
     * @apiNote abstract method, should be defined by subclasses
     * @param bialettiConnection The newly established connection
     * @return a client representation (Subclass of {@link BialettiServerClientRepresentation})
     */
    protected abstract ClientType getNewClient(BialettiTCPConnection bialettiTCPConnection);
    /**
     * What happens when the server is started
     * @apiNote abstract method, should be defined by subclasses
     * @throws Exception if the user code throws one
     */
    protected abstract void onStart() throws Exception;
    /**
     * What happens when the server is stopped
     * @apiNote abstract method, should be defined by subclasses
     * @throws Exception if the user code throws one
     */
    protected abstract void onStop() throws Exception;

    /**
     * @return a {@link ServerSocket} instance
     */
    private ServerSocket openServerSocket() {
        ServerSocket nServer;
        try { nServer = new ServerSocket(serverPort); }
        catch (Exception e) {
            // Call handler method
            exceptionHandler.raise(e, this);
            nServer = null;
        }

        return nServer;
    }

    /**
     * Listens for incoming connections
     * @apiNote runs on a separate thread
     */
    private void listen() {
        try {
            // Accept connections forever
            while (!Thread.interrupted()) {
                // Wait for a client to connect
                BialettiServerConnection newConnection = new BialettiServerConnection(serverSocket.accept());

                // Append client to the list of connected clients
                activeConnections.add(newConnection);

                // Start handler thread
                newConnection.getThreads().forEach(Thread::start);
            }
        }

        // Exception handler
        catch (Exception e) {
            // Call handler method
            exceptionHandler.raise(e, this);
        }
    }

    /**
     * An extension of BialettiConnection that also holds the handler thread and
     * provides a server-specific close() method
     * @author Alessandro-Salerno
     */
    protected final class BialettiServerConnection extends BialettiTCPConnection {
        /**
         * The thread that handles the connection
         */
        private final List<BialettiServerThread<ClientType>> mThreads;
        /**
         * A ClientType instance
         */
        private final ClientType client;

        /**
         * Default constructor
         * @param socket the client's socket
         * @throws IOException if the super constructor throws one
         */
        public BialettiServerConnection(Socket socket) throws IOException {
            super(socket);
            mThreads = new ArrayList<>();
            client   = getNewClient(this);

            // Create thread to handle the onConnect Event
            Thread startThread = new Thread(() -> {
                try { client.onConnect(); }
                catch (Exception e) {
                    // Call handler method
                    exceptionHandler.raise(e);
                }
            });

            // Spawn a thread for each handle method
            Arrays.stream(client.getClass().getMethods()).toList().forEach(method -> {
                if (!method.isAnnotationPresent(BialettiHandleMethod.class))
                    return;

                mThreads.add(new BialettiServerThread<>(client,
                                                        method,
                                                     BialettiServer.this,
                                                        exceptionHandler) {
                    @Override
                    public void run() {
                        // Make sure that the thread does not start before the start thread
                        try { startThread.join(); }
                        catch (InterruptedException ignored) { }

                        super.run();
                    }
                });}
            );

            // Call onConnect handler via new thread
            startThread.start();
        }

        /**
         * Closes the connection to the client
         * @throws Exception if the super method throws one
         * @throws Exception if the onClose handler throws one
         */
        @Override
        public void close() throws Exception {
            synchronized (activeConnections) {
                super.close();
                activeConnections.remove(this);
                client.onClose();
                getThreads().forEach(Thread::interrupt);
            }
        }

        /**
         * Closes the connection without removing it from the list
         */
        public void justClose() throws Exception {
            super.close();
            client.onClose();
            getThreads().forEach(Thread::interrupt);
        }

        /**
         * Getter for the handler thread
         * @return a list of BialettiServerThread instances
         */
        public List<BialettiServerThread<ClientType>> getThreads() { return mThreads; }
    }
}
