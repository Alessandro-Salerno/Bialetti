package bialetti.client;

import bialetti.service.BialettiRunnableService;
import bialetti.exceptions.BialettiNullAddressException;

/**
 * A Bialetti Client
 * @author Alessandro-Salerno
 */
public abstract class BialettiClient extends BialettiRunnableService {
    /**
     * The host address of the target server
     */
    private final String serverAddress;
    /**
     * The port of the target server
     */
    private final int serverPort;

    /**
     * Constructor
     * @param address the host address
     * @param port the port
     * @throws BialettiNullAddressException if the address is null
     */
    public BialettiClient(String address, int port) throws BialettiNullAddressException {
        if (address == null) {
            // Throw an exception if the address is null
            throw new BialettiNullAddressException();
        }

        serverAddress = address;
        serverPort    = port;

        init();
    }

    /**
     * @return the host address of the target server in string form
     */
    public String getServerAddress() { return serverAddress; }
    /**
     * @return the port of the target server
     */
    public int getServerPort() { return serverPort; }
}
