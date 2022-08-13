package bialetti;

import bialetti.util.BialettiInputStreamReader;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A wrapper for Java's {@link Socket}
 * @author Alessandro-Salerno
 */
public class BialettiConnection {
    /**
     * The Java {@link Socket} for the connection
     */
    protected final Socket connectionSocket;
    /**
     * The connection's {@link BialettiInputStreamReader}
     */
    protected final BialettiInputStreamReader inputStreamReader;
    /**
     * The connection's {@link java.io.OutputStream}
     */
    protected final PrintWriter outputPrintWriter;

    /**
     * Client-side constructor
     * @param address the IP address of the destination host
     * @param port the port of the destination host
     */
    public BialettiConnection(String address, int port) throws IOException {
        this(new Socket(address, port));
    }

    /**
     * Server-side constructor
     * @param socket the Java {@link Socket} instance
     * @throws  IOException if it is not able to instantiate the {@link BialettiInputStreamReader} or the {@link PrintWriter}
     */
    public BialettiConnection(Socket socket) throws IOException {
        connectionSocket = socket;

        // Saves streams
        inputStreamReader = new BialettiInputStreamReader(connectionSocket.getInputStream());
        outputPrintWriter = new PrintWriter(connectionSocket.getOutputStream(), true);
    }

    /**
     * Reads from the input stream and returns everything as a byte array
     * @throws IOException if it fails to read
     * @return the message
     */
    public String receive() throws IOException {
        // Reads the entire stream and returns it in string form
        return inputStreamReader.readall();
    }

    /**
     * Sends a string
     * @param data the message to be sent
     */
    public void send(String data) {
        // Sends the string
        outputPrintWriter.print(data);
        outputPrintWriter.flush();
    }

    /**
     * Closes the connection
     * @throws IOException if an I/O error occurs while closing the connection
     */
    public void close() throws Exception {
        getSocket().close();
    }

    /**
     * @return the {@link Socket} instance
     */
    public Socket getSocket() { return connectionSocket; }
}
