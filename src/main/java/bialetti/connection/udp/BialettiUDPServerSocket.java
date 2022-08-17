package bialetti.connection.udp;

import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Subclass of {@link BialettiUDPConnection} that provides a server-specific interface for connections
 * @author Alessandro-Salerno
 */
public class BialettiUDPServerSocket extends BialettiUDPConnection {
    /**
     * Constructor
     * @param port the port on which the socket will be opened
     * @param window the size of the buffer
     * @throws SocketException if the socket could not be opened
     */
    public BialettiUDPServerSocket(int port, int window) throws SocketException {
        super(new DatagramSocket(port),
              new byte[window]);
    }
}
