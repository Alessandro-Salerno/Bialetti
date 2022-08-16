package bialetti.server.tcp;

import bialetti.annotations.BialettiHandleMethod;
import bialetti.connection.tcp.BialettiTCPConnection;
import bialetti.server.BialettiServer;
import bialetti.util.ObjectUtility;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * A Bialetti TCP Server
 * @param <ClientType> the type that defines a client (Subclass of {@link BialettiTCPServerClient})
 * @author Alessandro-Salerno
 */
public abstract class BialettiTCPServer<ClientType extends BialettiTCPServerClient<?>> extends BialettiServer {
    /**
     * The event handler for server exceptions
     */
    private final BialettiTCPServerExceptionHandler<ClientType> exceptionHandler;
    /**
     *  List of active connections
     */
    private final List<BialettiServerConnection> activeConnections;
    /**
     * The socket on which the server listens for new connections
     */
    private final ServerSocket serverSocket;
    /**
     * The thread on which the server's listen method is run
     */
    private Thread listenThread;

    /**
     * constructor
     * @param port the port on which the server listens
     * @param sxh an exception handler for the server
     */
    public BialettiTCPServer(int port, BialettiTCPServerExceptionHandler<ClientType> sxh) {
        super(port);

        // Set fields
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
    @Override
    public void start() {
        if (!serverSocket.isBound()) {
            // Reopen socket]
            try { serverSocket.bind(new InetSocketAddress("", getPort())); }
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
    @Override
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
     * @apiNote abstract method, should be defined by subclasses
     * @param bialettiTCPConnection the newly established connection
     * @return a client representation (Subclass of {@link BialettiTCPServerClient})
     */
    protected abstract ClientType getNewClient(BialettiTCPConnection bialettiTCPConnection);

    /**
     * @return a {@link ServerSocket} instance
     */
    private ServerSocket openServerSocket() {
        ServerSocket nServer;
        try { nServer = new ServerSocket(getPort()); }
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
            }
        }

        // Exception handler
        catch (Exception e) {
            // Call handler method
            exceptionHandler.raise(e, this);
        }
    }

    /**
     * An extension of {@link BialettiTCPConnection} that also holds the handler thread and
     * provides a server-specific close() method
     * @author Alessandro-Salerno
     */
    private final class BialettiServerConnection extends BialettiTCPConnection {
        /**
         * The thread that handles the connection
         */
        private final List<BialettiTCPServerThread<ClientType>> mThreads;
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
            new ObjectUtility(client).forEachMethodWithAnnotation(BialettiHandleMethod.class,
                                                                  method -> {
                BialettiTCPServerThread<ClientType> newThread
                        = new BialettiTCPServerThread<>(client,
                                                        method,
                                                        BialettiTCPServer.this,
                                                        exceptionHandler) {
                    @Override
                    public void run() {
                        // Make sure that the thread does not start before the start thread
                        try { startThread.join(); }
                        catch (InterruptedException ignored) { }

                        super.run();
                    }
                };

                newThread.start();
                mThreads.add(newThread);
            });

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
                getThreads().stream().parallel().forEach(Thread::interrupt);
            }
        }

        /**
         * Closes the connection without removing it from the list
         */
        public void justClose() throws Exception {
            super.close();
            client.onClose();
            getThreads().stream().parallel().forEach(Thread::interrupt);
        }

        /**
         * Getter for the handler thread
         * @return a list of BialettiServerThread instances
         */
        public List<BialettiTCPServerThread<ClientType>> getThreads() { return mThreads; }
    }
}
