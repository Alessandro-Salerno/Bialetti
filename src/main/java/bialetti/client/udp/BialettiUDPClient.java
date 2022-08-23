package bialetti.client.udp;

import bialetti.client.BialettiClient;
import bialetti.connection.udp.BialettiUDPClientConnection;
import bialetti.exceptions.BialettiIllegalOperationException;
import bialetti.exceptions.BialettiNullAddressException;

/**
 * A Bialetti UDP Client
 * @author Alessandro-Salerno
 */
public abstract class BialettiUDPClient extends BialettiClient {
    private BialettiUDPClientConnection connection;

    /**
     * Constructor
     *
     * @param address the host address
     * @param port    the port
     * @throws BialettiNullAddressException if the address is null
     */
    public BialettiUDPClient(String address, int port) throws BialettiNullAddressException {
        super(address, port);
    }

    /**
     * Stops the client
     */
    @Override
    public void stop() throws BialettiIllegalOperationException {
        super.stop();
        connection.close();
    }

    /**
     * Creates the connection and calls super.run()
     */
    @Override
    public void run() throws RuntimeException {
        try { connection = new BialettiUDPClientConnection(getServerAddress(), getServerPort()); }
        catch (Exception e) {
            // Throw runtime exception
            throw new RuntimeException(e);
        }

        super.run();
    }

    /**
     * @return the {@link BialettiUDPClientConnection}
     */
    public BialettiUDPClientConnection getConnection() { return connection; }
}
