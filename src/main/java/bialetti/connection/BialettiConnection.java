package bialetti.connection;

import java.io.IOException;

/**
 * An interface for a connection
 * @author Alessandro-Salerno
 */
public interface BialettiConnection {
    /**
     * Reads from the input stream and returns everything as a byte array
     * @throws IOException if it fails to read
     * @return the message
     */
    String receive() throws IOException;
    /**
     * Sends a string
     * @param data the message to be sent
     */
    void send(String data);
    /**
     * Closes the connection
     * @apiNote abstract method, should be defined by subclasses
     * @throws Exception if something goes wrong
     */
    void close() throws Exception;
}
