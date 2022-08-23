package bialetti.client;

import bialetti.annotations.methods.BialettiEndMethod;
import bialetti.annotations.methods.BialettiInitMethod;
import bialetti.exceptions.BialettiNullAddressException;
import bialetti.service.BialettiRunnableService;

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
    }

    /**
     * What happens when the client is started
     */
    @BialettiInitMethod
    public abstract void onStart();
    /**
     * What happens when the client is stopped
     */
    @BialettiEndMethod
    public abstract  void onStop();

    /**
     * @return the host address of the target server in string form
     */
    public String getServerAddress() { return serverAddress; }
    /**
     * @return the port of the target server
     */
    public int getServerPort() { return serverPort; }
}
