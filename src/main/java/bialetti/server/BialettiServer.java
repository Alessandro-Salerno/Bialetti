package bialetti.server;

import bialetti.BialettiService;

public abstract class BialettiServer extends BialettiService {
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
    protected abstract void onStart() throws Exception;
    /**
     * What happens when the server is stopped
     * @apiNote abstract method, should be defined by subclasses
     * @throws Exception if the user code throws one
     */
    protected abstract void onStop() throws Exception;
}
