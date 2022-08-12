package bialetti;

import bialetti.util.BialettiInputStreamReader;
import bialetti.util.BialettiPrintWriter;

import java.io.IOException;
import java.net.Socket;

/*
 * A wrapper for Java's TCP Socket Class
 * @author Alessandro-Salerno
 */
public class BialettiConnection {
    /*
     * The Java Socket for the connection
     */
    protected final Socket connectionSocket;
    /*
     * The connection's input stream reader
     */
    protected final BialettiInputStreamReader inputStreamReader;
    /*
     * The connection's output stream
     */
    protected final BialettiPrintWriter outputPrintWriter;

    /*
     * Client-side constructor
     * @param address The IP address of the destination host
     * @param port The port of the destination host
     */
    public BialettiConnection(String address, int port) throws IOException {
        this(new Socket(address, port));
    }

    /*
     * Server-side constructor
     * @param socket The Java TCP Socket instance
     */
    public BialettiConnection(Socket socket) throws IOException {
        connectionSocket = socket;

        // Saves streams
        inputStreamReader = new BialettiInputStreamReader(connectionSocket.getInputStream());
        outputPrintWriter = new BialettiPrintWriter(connectionSocket.getOutputStream());
    }

    /*
     * Reads from the input stream and returns everything as a byte array
     */
    public String receive() throws IOException {
        // Reads the entire stream and returns it in string form
        return inputStreamReader.readall();
    }

    /*
     * Sends a string
     */
    public void send(String data) {
        // Sends the string
        outputPrintWriter.print(data);
        outputPrintWriter.flush();
    }

    /*
     * Closes the connection
     */
    public void close() throws Exception {
        getSocket().close();
    }

    /*
     * Getter for the Java TCP Socket Instance
     */
    public Socket getSocket() { return connectionSocket; }
}
