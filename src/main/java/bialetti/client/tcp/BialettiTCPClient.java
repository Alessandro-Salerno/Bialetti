package bialetti.client.tcp;

import bialetti.client.BialettiClient;
import bialetti.connection.tcp.BialettiTCPConnection;
import bialetti.exceptions.BialettiIllegalOperationException;
import bialetti.exceptions.BialettiNullAddressException;

/**
 * A Bialetti TCP Client
 * @author Alessandro-Salerno
 */
public abstract class BialettiTCPClient extends BialettiClient {
    /**
     * The connection to the server
     */
    private BialettiTCPConnection connection;

    /**
     * Constructor
     *
     * @param address the host address
     * @param port    the port
     * @throws BialettiNullAddressException if the address is null
     */
    public BialettiTCPClient(String address, int port) throws BialettiNullAddressException {
        super(address, port);
    }

    /**
     * Stops the client
     */
    @Override
    public void stop() throws RuntimeException,
                              BialettiIllegalOperationException {
        super.stop();

        try { connection.close(); }
        catch (Exception e) {
            // Throw a runtime exception
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates the connection and calls super.run()
     */
    @Override
    public void run() throws RuntimeException {
        try { connection = new BialettiTCPConnection(getServerAddress(), getServerPort());  }
        catch (Exception e) {
            // Throw exception
            throw new RuntimeException(e);
        }

        super.run();
    }

    /**
     * @return the client's {@link BialettiTCPConnection}
     */
    public BialettiTCPConnection getConnection() { return connection; }
}
