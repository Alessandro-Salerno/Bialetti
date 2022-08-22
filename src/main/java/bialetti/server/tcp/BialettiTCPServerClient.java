package bialetti.server.tcp;

import bialetti.annotations.methods.BialettiEndMethod;
import bialetti.annotations.methods.BialettiInitMethod;
import bialetti.connection.tcp.BialettiTCPConnection;
import bialetti.exceptions.BialettiIllegalOperationException;
import bialetti.service.BialettiManagedService;

/**
 * A representation of a client
 * @param <ServerType> the server class
 * @author Alessandro-Salerno
 */
public abstract class BialettiTCPServerClient<ServerType extends BialettiTCPServer<?>> extends BialettiManagedService {
    /**
     * The connection on which che client runs on
     */
    private final BialettiTCPConnection connection;
    /**
     * The server handling the connection
     */
    private final ServerType server;

    /**
     * Constructor
     * @param c the connection
     * @param s the server
     */
    public BialettiTCPServerClient(BialettiTCPConnection c, ServerType s) {
        connection = c;
        server     = s;
    }

    /**
     * Shuts down the client
     * @throws BialettiIllegalOperationException if the service was not running when the method was called
     */
    @Override
    public final void stop() throws BialettiIllegalOperationException {
        super.stop();

        try { connection.close(); }
        catch (Exception e) {
            // Call handler method
            raiseException(e);
        }
    }

    /**
     * @return the {@link BialettiTCPConnection} to the client
     */
    public BialettiTCPConnection getConnection() { return connection; }
    /**
     * @return the {@link BialettiTCPServer}
     */
    public ServerType getServer() { return server; }

    /**
     * What happens when the connection is first established
     * @apiNote abstract method, should be defined by subclasses
     * @throws Exception if the user code throws one
     */
    @BialettiInitMethod
    public abstract void onConnect() throws Exception;
    /**
     * What happens when the connection is terminated
     * @apiNote abstract method, should be defined by subclasses
     * @throws Exception if the user code throws one
     */
    @BialettiEndMethod
    public abstract void onClose() throws Exception;
}
