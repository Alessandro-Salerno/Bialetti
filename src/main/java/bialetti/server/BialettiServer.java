package bialetti.server;

import bialetti.annotations.methods.BialettiEndMethod;
import bialetti.annotations.methods.BialettiInitMethod;
import bialetti.service.BialettiRunnableService;

/**
 * A Bialetti Server
 * @author Alessandro-Salerno
 */
public abstract class BialettiServer extends BialettiRunnableService {
    /**
     * The port on which the server is hosted
     */
    private final int serverPort;

    public BialettiServer(int port) {
        serverPort = port;
    }

    /**
     * @return the server's port
     */
    public int getPort() { return serverPort; }

    /**
     * What happens when the server is started
     * @apiNote abstract method, should be defined by subclasses
     * @throws Exception if the user code throws one
     */
    @BialettiInitMethod
    public abstract void onStart() throws Exception;
    /**
     * What happens when the server is stopped
     * @apiNote abstract method, should be defined by subclasses
     * @throws Exception if the user code throws one
     */
    @BialettiEndMethod
    public abstract void onStop() throws Exception;
}
