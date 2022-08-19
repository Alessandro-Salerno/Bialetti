package bialetti.exceptions;

/**
 * An exception that occurs when the host address given to a network service turns out to be invalid
 * @author Alessandro-Salerno
 */
public class BialettiInvalidAddressException extends RuntimeException {
    /**
     * Constructor
     * @param message the exception's message
     */
    public BialettiInvalidAddressException(String message) {
        super(message);
    }

    /**
     * Constructor
     * @param cause the cause of the exception ({@link Throwable})
     */
    public BialettiInvalidAddressException(Throwable cause) {
        super(cause);
    }
}
