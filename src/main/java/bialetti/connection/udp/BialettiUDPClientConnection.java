package bialetti.connection.udp;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Subclass of {@link BialettiUDPConnection} that provides a client-specific interface for connections
 */
public class BialettiUDPClientConnection extends BialettiUDPConnection {
    /**
     * The address of the target server
     */
    private final String targetAddress;
    /**
     * The port on which the target server listens
     */
    private final int targetPort;

    /**
     * Constructor
     * @param address the address of the target server
     * @param port the port on which the server listens
     * @throws SocketException if the socket could not be opened
     */
    public BialettiUDPClientConnection(String address, int port) throws SocketException {
        super(new DatagramSocket(),
              new byte[512]);

        targetAddress = address;
        targetPort = port;
    }

    /**
     * Send a string to the server
     * @param data the string to be sent
     * @throws IOException if an I/O error occurs
     */
    public void send(String data)  throws IOException{ send(data.getBytes()); }
    /**
     * Send a bytearray to the server
     * @param data the bytearray to be sent
     * @throws IOException if an I/O error occurs
     */
    public void send(byte[] data) throws IOException { super.send(data, targetAddress, targetPort); }
}
