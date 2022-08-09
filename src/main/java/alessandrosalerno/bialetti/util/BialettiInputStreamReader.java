package alessandrosalerno.bialetti.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/*
 * Bialegtti's extension of java's InputStreamReader
 * @author Alessandro-Salerno
 */
public class BialettiInputStreamReader extends InputStreamReader {
    /*
     * Default constructor
     * @param stream The InputStream
     */
    public BialettiInputStreamReader(InputStream stream) {
        super(stream);
    }

    /*
     * Reads the entire message
     */
    public String readall() throws IOException {
        int character;                              // The current character
        StringBuilder buffer = new StringBuilder(); // The buffer

        // Reads from the input stream until a 0 is found
        while ((character = read()) != 0) {
            buffer.append((char) character);
        }

        return buffer.toString();
    }
}
