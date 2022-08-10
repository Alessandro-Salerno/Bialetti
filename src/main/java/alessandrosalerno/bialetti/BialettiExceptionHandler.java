package alessandrosalerno.bialetti;

import java.io.IOException;
import java.net.SocketException;

/*
 * The standard interface for a BialettiServerExceptionHandler
 * @author Alessandro-Salerno
 */
public interface BialettiExceptionHandler {
    /*
     * What happens when a generic exception is thrown
     * @param exception The exception itself
     */
    void onGenericException(Exception exception);
    /*
     * What happens when an IO Exception is thrown
     * @param ioException The IO Exception itself
     */
    void onIOException(IOException ioException);
    /*
     * What happens when a Socket Exception is thrown
     * @param socketException The Socket Exception itself
     */
    void onSocketException(SocketException socketException);
}
