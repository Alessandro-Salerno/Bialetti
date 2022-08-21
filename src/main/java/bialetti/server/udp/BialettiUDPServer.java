package bialetti.server.udp;

import bialetti.connection.udp.BialettiUDPServerSocket;
import bialetti.exceptions.BialettiIllegalOperationException;
import bialetti.server.BialettiServer;

/**
 * A Bialetti UDP Server
 * @author Alessandro-Salerno
 */
public abstract class BialettiUDPServer extends BialettiServer {
    /**
     * The socket on which the server listens for incoming requests
     * and sends outgoing replies
     */
    private final BialettiUDPServerSocket serverSocket;

    /**
     * Constructor
     * @param port the port on which to open the server
     * @throws RuntimeException if something goes wrong while opening the connection
     */
    public BialettiUDPServer(int port) throws RuntimeException {
        super(port);

        serverSocket = openServerSocket();
        init();
    }

    /**
     * Stops the server
     * @throws BialettiIllegalOperationException if the service was not running when the method was called
     */
    @Override
    public final void stop() throws BialettiIllegalOperationException {
        super.stop();

        // Close the server
        serverSocket.close();
    }

    /**
     * @return the UDP connection used by the server
     */
    public BialettiUDPServerSocket getConnection() { return serverSocket; }

    /**
     * @return a {@link BialettiUDPServerSocket} instance
     * @throws RuntimeException if something goes wrong while opening the connection
     */
    private BialettiUDPServerSocket openServerSocket() throws RuntimeException {
        try { return new BialettiUDPServerSocket(getPort(), 512); }
        catch (Exception e) {
            // Throw runtime exception
            throw new RuntimeException(e);
        }
    }
}
