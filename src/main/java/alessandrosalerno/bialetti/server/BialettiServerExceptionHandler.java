package alessandrosalerno.bialetti.server;

import java.io.IOException;

/*
 * The standard interface for a BialettiServerExceptionHandler
 * @author Alessandro-Salerno
 */
public interface BialettiServerExceptionHandler {
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
}
