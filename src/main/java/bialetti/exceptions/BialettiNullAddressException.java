package bialetti.exceptions;

/**
 * A type of {@link BialettiInvalidAddressException} that is thrown if the address is null
 * @author Alessandro-Salerno
 */
public class BialettiNullAddressException extends BialettiInvalidAddressException {
    /**
     * Constructor
     */
    public BialettiNullAddressException() {
        super("Address cannot be null");
    }
}
