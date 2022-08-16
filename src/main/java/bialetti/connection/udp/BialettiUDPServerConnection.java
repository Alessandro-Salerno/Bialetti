package bialetti.connection.udp;

import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Subclass of {@link BialettiUDPConnection} that provides a server-specific interface for connections
 */
public class BialettiUDPServerConnection extends BialettiUDPConnection {
    /**
     * Constructor
     * @param port the port on which the socket will be opened
     * @param window the size of the buffer
     * @throws SocketException if the socket could not be opened
     */
    public BialettiUDPServerConnection(int port, int window) throws SocketException {
        super(new DatagramSocket(port),
              new byte[window]);
    }
}
