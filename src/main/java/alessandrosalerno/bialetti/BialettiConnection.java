package alessandrosalerno.bialetti;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
     * The connection's input stream reader
     */
    private BufferedReader inputStreamReader;
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
     */
    public String receive() throws IOException {
        String data = "<NO-DATA>";    // Buffer for the input stream reader

        // Reads an entire line from the stream
        data = inputStreamReader.readLine();

        // Returns the received data
        return data;
    }

    /*
     * Sends a string
     */
    public void send(String data) {
        // Sends the string
        outputPrintWriter.println(data);
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
            inputStreamReader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            outputPrintWriter = new PrintWriter(connectionSocket.getOutputStream(), true);
        }

        // Exception handler
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
