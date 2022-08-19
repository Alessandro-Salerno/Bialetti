package bialetti.exceptions;

/**
 * An exception that occurs when an operation is performed in a context where it shall not or
 * otherwise cannot be completed.
 * @author Alessandro-Salerno
 */
public class BialettiIllegalOperationException extends RuntimeException {
    /**
     * Constructor
     * @param message the exception's message
     */
    public BialettiIllegalOperationException(String message) {
        super(message);
    }

    /**
     * Constructor
     * @param cause the cause of the exception ({@link Throwable})
     */
    public BialettiIllegalOperationException(Throwable cause) {
        super(cause);
    }
}
