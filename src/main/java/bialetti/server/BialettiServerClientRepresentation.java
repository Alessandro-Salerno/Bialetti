package bialetti.server;

import bialetti.BialettiConnection;

/*
 * A representation of a client
 * @author Alessandro-Salerno
 */
public abstract class BialettiServerClientRepresentation<ServerType> {
    /*
     * The connection on which che client runs on
     */
    protected final BialettiConnection connection;
    /*
     * The server handling the client
     */
    protected final ServerType server;

    /*
     * Default constructor
     * @param c The connection
     * @param s The server
     */
    public BialettiServerClientRepresentation(BialettiConnection c, ServerType s) {
        connection = c;
        server     = s;
    }

    /*
     * Getter for the connection
     */
    public BialettiConnection getConnection() { return connection; }
    /*
     * Getter for the server
     */
    public ServerType getServer() { return server; }

    /*
     * What happens when the connection is first established
     */
    public abstract void onConnect() throws Exception;
    /*
     * What happens when the connection is terminated
     */
    public abstract void onClose() throws Exception;
}
