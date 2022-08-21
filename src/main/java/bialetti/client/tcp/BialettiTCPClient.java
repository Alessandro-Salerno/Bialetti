package bialetti.client.tcp;

import bialetti.client.BialettiClient;
import bialetti.connection.tcp.BialettiTCPConnection;
import bialetti.exceptions.BialettiNullAddressException;

public abstract class BialettiTCPClient extends BialettiClient {
    protected final BialettiTCPConnection connection;

    /**
     * Constructor
     *
     * @param address the host address
     * @param port    the port
     * @throws BialettiNullAddressException if the address is null
     */
    public BialettiTCPClient(String address, int port) throws BialettiNullAddressException {
        super(address, port);
        connection = openConnection();

        init();
    }

    /**
     * Open the connection
     * @return A {@link BialettiTCPConnection} instance
     * @throws RuntimeException if something goes wrong while opening the connection
     */
    private BialettiTCPConnection openConnection() throws RuntimeException {
        try { return new BialettiTCPConnection(getServerAddress(), getServerPort());  }
        catch (Exception e) {
            // Throw exception
            throw new RuntimeException(e);
        }
    }
}
