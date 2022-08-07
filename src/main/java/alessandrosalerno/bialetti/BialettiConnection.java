package alessandrosalerno.bialetti;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

/*
 * A wrapper for Java's TCP Socket Class
 * @author Alessandro-Salerno
 */
public class BialettiConnection {
    /*
     * The Java Socket for the connection
     */
    private Socket connectionSocket;
    /*
     * The connection's input stream
     */
    private InputStreamReader inputStreamReader;
    /*
     * The connection's output stream
     */
    private PrintWriter outputPrintWriter;

    /*
     * Client-side constructor
     * @param address The IP address of the destination host
     * @param port The port of the destination host
     */
    public BialettiConnection(String address, int port) {
        try {
            // Establish a connection to the desired address and port
            connectionSocket = new Socket(address, port);
            initializeConnection();
        }

        // Exception handler
        catch (Exception e) {
            System.out.println("[-] Socket Error");
            e.printStackTrace();
        }
    }

    /*
     * Server-side constructor
     * @param socket The Java TCP Socket instance
     */
    public BialettiConnection(Socket socket) {
        connectionSocket = socket;
        initializeConnection();
    }

    /*
     * Reads from the input stream and returns everything as a byte array
     * Throws IOException
     */
    public byte[] receive() throws IOException {
        int read;                                   // Current character (Read from the stream)
        StringBuilder buffer = new StringBuilder(); // Buffer

        // Read until the end of the stream is reached
        while ((read = inputStreamReader.read()) != -1) {
            // Appends the current character to the buffer
            buffer.append((char) read);
        }

        // Casts the buffer to a byte array and returns it
        return buffer.toString().getBytes();
    }

    /*
     * Sends a bytearray
     */
    public void send(byte[] data) {
        // Casts the bytearray to a string and sends it via the socket
        outputPrintWriter.print(Arrays.toString(data));
    }

    /*
     * Closes the TCP Socket Connection
     */
    public void close() {
        try {
            // Close connection to the desired address and port
            connectionSocket.close();
        }

        // Exception handler
        catch (Exception e) {
            System.out.println("[-] Socket Error");
            e.printStackTrace();
        }
    }

    /*
     * Getter for the Java TCP Socket Instance
     */
    public Socket getSocket() { return connectionSocket; }

    /*
     * Initialization method for the connection
     * Creates input and output channels for the connection
     */
    private void initializeConnection() {
        try {
            inputStreamReader = new InputStreamReader(connectionSocket.getInputStream());
            outputPrintWriter = new PrintWriter(connectionSocket.getOutputStream(), true);
        }

        // Exception handler
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
