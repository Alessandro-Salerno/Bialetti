package bialetti.util;

import java.io.OutputStream;
import java.io.PrintWriter;

/*
 * Bialetti's extension of Java's PrintWriter
 * @author Alessandro-Salerno
 */
public class BialettiPrintWriter extends PrintWriter {
    /*
     * Default constructor
     * @param stream The OutputStream
     */
    public BialettiPrintWriter(OutputStream stream) {
        super(stream, true);
    }

    /*
     * Writes a string to the stream
     */
    @Override
    public void print(String s) {
        super.print(s);
    }
}
