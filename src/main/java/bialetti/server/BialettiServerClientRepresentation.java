package bialetti.server;

import bialetti.connection.tcp.BialettiTCPConnection;

/**
 * A representation of a client
 * @param <ServerType> the server class
 * @author Alessandro-Salerno
 */
public abstract class BialettiServerClientRepresentation<ServerType extends BialettiServer<?>> {
    /**
     * The connection on which che client runs on
     */
    protected final BialettiTCPConnection connection;
    /**
     * The server handling the connection
     */
    protected final ServerType server;

    /**
     * Constructor
     * @param c the connection
     * @param s the server
     */
    public BialettiServerClientRepresentation(BialettiTCPConnection c, ServerType s) {
        connection = c;
        server     = s;
    }

    /**
     * @return the {@link BialettiTCPConnection} to the client
     */
    public BialettiTCPConnection getConnection() { return connection; }
    /**
     * @return the {@link BialettiServer}
     */
    public ServerType getServer() { return server; }

    /**
     * What happens when the connection is first established
     * @apiNote abstract method, should be defined by subclasses
     * @throws Exception if the user code throws one
     */
    public abstract void onConnect() throws Exception;
    /**
     * What happens when the connection is terminated
     * @apiNote abstract method, should be defined by subclasses
     * @throws Exception if the user code throws one
     */
    public abstract void onClose() throws Exception;
}
