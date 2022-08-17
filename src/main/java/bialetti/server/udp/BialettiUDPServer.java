package bialetti.server.udp;

import bialetti.connection.udp.BialettiUDPServerSocket;
import bialetti.server.BialettiServer;
import bialetti.util.MethodThread;

import java.util.ArrayList;
import java.util.List;

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
     */
    public BialettiUDPServer(int port) {
        super(port);

        serverSocket = openServerSocket();
        init();
    }

    /**
     * Stops the server
     */
    @Override
    public final void stop() {
        super.stop();

        // Close the server
        serverSocket.close();

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
    protected void start() {
        super.start();

        try { onStart(); }
        catch (Exception e) {
            // Call handler method
            raiseException(e);
        }
    }

    /**
     * @return the UDP connection used by the server
     */
    public BialettiUDPServerSocket getConnection() { return serverSocket; }

    /**
     * @return a {@link BialettiUDPServerSocket} instance
     */
    private BialettiUDPServerSocket openServerSocket() {
        BialettiUDPServerSocket nSocket;
        try { nSocket = new BialettiUDPServerSocket(getPort(), 512); }
        catch (Exception e) {
            // Call handler method
            raiseException(e);
            nSocket = null;
        }

        return nSocket;
    }
}
