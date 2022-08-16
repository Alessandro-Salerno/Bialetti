package bialetti.connection.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * A UDP connection
 * @author Alessandro-Salerno
 * @hidden
 */
abstract class BialettiUDPConnection {
    /**
     * The UDP socket
     */
    private final DatagramSocket connectionSocket;
    /**
     * The buffer used to send and receive messages
     */
    private final byte[] buffer;

    /**
     * Constructor
     * @param sock the UDP socket
     * @param buff the buffer
     */
    public BialettiUDPConnection(DatagramSocket sock, byte[] buff) {
        connectionSocket = sock;
        buffer = buff;
    }

    /**
     * Receives a string
     * @return a string containing the message
     * @throws IOException if an I/O error occurs
     */
    public String receive() throws IOException {
        DatagramPacket packet = receivePacket();
        return new String(packet.getData(), 0, packet.getLength());
    }

    /**
     * Receives a bytearray
     * @return a bytearray containing the message
     * @throws IOException if an I/O error occurs
     */
    public byte[] receiveBytes() throws IOException {
        return receivePacket().getData();
    }

    /**
     * Recevies a {@link DatagramPacket}
     * @return the received packet
     * @throws IOException if an I/O error occurs
     */
    public DatagramPacket receivePacket() throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        connectionSocket.receive(packet);

        return packet;
    }

    /**
     * Sends a bytearray
     * @param data the bytearray containing the message
     * @param address the {@link InetAddress} of the target socket
     * @param port the port of the target socket
     * @throws IOException if an I/O error occurs
     */
    public void send(byte[] data, InetAddress address, int port) throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
        connectionSocket.send(packet);
    }

    /**
     * Sends a string
     * @param data the string containing the message
     * @param address the address of the target socket
     * @param port the port of the target socket
     * @throws IOException if an I/O error occurs
     */
    public void send(String data, String address, int port) throws IOException { send(data.getBytes(), address, port); }
    /**
     * Sends a bytearray
     * @param data the bytearray containing the message
     * @param address the address of the target socket
     * @param port the port of the target socket
     * @throws IOException if an I/O error occurs
     */
    public void send(byte[] data, String address, int port) throws IOException { send(data, address.getBytes(), port); }
    /**
     * Sends a bytearray
     * @param data the bytearray containing the message
     * @param address the address of the target socket (As a byte[])
     * @param port the port of the target socket
     * @throws IOException if an I/O error occurs
     */
    public void send(byte[] data, byte[] address, int port) throws IOException { send(data, InetAddress.getByAddress(address), port); }
    /**
     * Sends a string
     * @param data the string containing the message
     * @param address the {@link InetAddress} of the target socket
     * @param port the port of the target socket
     * @throws IOException if an I/O error occurs
     */
    public void send(String data, InetAddress address, int port) throws IOException { send(data.getBytes(), address, port); }

    /**
     * Closes the connection
     */
    public void close() {
        connectionSocket.close();
    }
}
