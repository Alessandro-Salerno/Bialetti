package bialetti.server.tcp;

import bialetti.annotations.methods.BialettiHandleMethod;
import bialetti.connection.tcp.BialettiTCPConnection;
import bialetti.exceptions.BialettiIllegalOperationException;
import bialetti.server.BialettiServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private ServerSocket serverSocket;

    /**
     * Constructor
     * @param port the port on which the server listens
     * @throws RuntimeException if something goes wrong while initializing the server's socket
     */
    public BialettiTCPServer(int port) throws RuntimeException {
        super(port);

        // Set fields
        activeConnections = new ArrayList<>();
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
            synchronized (activeConnections) { activeConnections.add(newConnection); }
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
        synchronized (activeConnections) {
            activeConnections.stream()
                              .parallel()
                              .filter(Objects::nonNull)
                              .forEach(conn -> conn.send(message));
        }
    }

    /**
     * Stops the server
     * @throws BialettiIllegalOperationException if the service was not running when the method was called
     */
    @Override
    public final void stop() throws BialettiIllegalOperationException {
        super.stop();

        synchronized (activeConnections) {
            // Close all connections
            activeConnections.stream()
                              .parallel()
                              .forEach(c -> c.getClient().stop());

            // Clear list of active connections and ensure that no other thread can access it
            activeConnections.clear();
        }
    }

    /**
     * @apiNote abstract method, should be defined by subclasses
     * @param bialettiTCPConnection the newly established connection
     * @return a client representation (Subclass of {@link BialettiTCPServerClient})
     */
    protected abstract ClientType getNewClient(BialettiTCPConnection bialettiTCPConnection);

    /**
     * Starts the server
     * @throws RuntimeException if something goes wrong while opening the connection
     */
    @Override
    public void run() throws RuntimeException {
        try { serverSocket=  new ServerSocket(getPort()); }
        catch (Exception e) {
            // Throw runtime exception
            throw new RuntimeException(e);
        }

        super.run();
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
            client.run();
        }

        /**
         * Closes the connection to the client
         */
        @Override
        public void close() {
            synchronized (activeConnections) {
                activeConnections.remove(this);
                justClose();
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
