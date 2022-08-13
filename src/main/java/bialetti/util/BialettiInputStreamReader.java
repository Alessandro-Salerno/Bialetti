package bialetti.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Bialetti's extension of Java's {@link InputStreamReader}
 * @author Alessandro-Salerno
 */
public class BialettiInputStreamReader extends InputStreamReader {
    /**
     * Constructor
     * @param stream the InputStream
     */
    public BialettiInputStreamReader(InputStream stream) {
        super(stream);
    }

    /**
     * Reads the entire message
     * @throws IOException if an error occurs while reading from the stream
     */
    public String readall() throws IOException {
        char[] buffer = new char[1024];             // Temp buffer
        StringBuilder data = new StringBuilder();   // Final output
        int numRead;                                // Number of characters read from the stream

        do {
            // Read from the stream
            numRead = read(buffer, 0, buffer.length);

            // If the stream was read successfully, then append the temp buffer to the final string
            if (numRead != -1) data.append(buffer, 0, numRead); else break;
        } while (numRead == buffer.length);

        return data.toString();
    }
}
