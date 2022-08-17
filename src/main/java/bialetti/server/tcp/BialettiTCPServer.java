package bialetti.server.tcp;

import bialetti.annotations.BialettiHandleMethod;
import bialetti.connection.tcp.BialettiTCPConnection;
import bialetti.server.BialettiServer;
import bialetti.util.MethodThread;

import java.io.IOException;
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
     *  List of active connections
     */
    private final List<BialettiServerConnection> activeConnections;
    /**
     * The socket on which the server listens for new connections
     */
    private final ServerSocket serverSocket;

    /**
     * Constructor
     * @param port the port on which the server liste
     */
    public BialettiTCPServer(int port) {
        super(port);

        // Set fields
        activeConnections = new ArrayList<>();
        serverSocket = openServerSocket();

        init();
    }

    /**
     * Listens for incoming connections
     * @apiNote runs on a separate thread
     */
    @BialettiHandleMethod
    public final void listen() {
        try {
            // Wait for a client to connect
            BialettiServerConnection newConnection = new BialettiServerConnection(serverSocket.accept());

            // Append client to the list of connected clients
            activeConnections.add(newConnection);
        }

        // Exception handler
        catch (Exception e) {
            // Call handler method
            raiseException(e);
        }
    }

    /**
     * Sends the same message to all active connections
     * @param message the message to be broadcast
     */
    public void broadcast(String message) {
        activeConnections.stream()
                          .parallel()
                          .forEach(conn -> conn.send(message));
    }

    /**
     * Stops the server
     */
    @Override
    public final void stop() {
        super.stop();

        synchronized (activeConnections) {
            // Close all connections
            activeConnections.stream()
                              .parallel()
                              .forEach(c -> c.getClient().stop());

            // Clear list of active connections and ensure that no other thread can access it
            activeConnections.clear();
        }

        try { onStop(); }
        catch (Exception e) {
            // Call handler method
            raiseException(e);
        }
    }

    /**
     * Starts the server
     */
    @Override
    protected final void start() {
        super.start();

        try { onStart(); }
        catch (Exception e) {
            // Call handler method
            raiseException(e);
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
            raiseException(e);
            nServer = null;
        }

        return nServer;
    }

    /**
     * An extension of {@link BialettiTCPConnection} that also holds the handler thread and
     * provides a server-specific close() method
     * @author Alessandro-Salerno
     */
    private final class BialettiServerConnection extends BialettiTCPConnection {
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
            client = getNewClient(this);
        }

        /**
         * Closes the connection to the client
         */
        @Override
        public void close() {
            synchronized (activeConnections) {
                justClose();
                activeConnections.remove(this);
            }
        }

        /**
         * Closes the connection without removing it from the list
         */
        public void justClose() {
            try { super.close(); }
            catch (Exception e) {
                // Call handler method
                client.raiseException(e);
            }
        }

        /**
         * @return the client
         */
        public ClientType getClient() { return client; }
    }
}
