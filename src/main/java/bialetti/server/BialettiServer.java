package bialetti.server;

public abstract class BialettiServer {
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
     * Starts the server
     * @apiNote abstract method, should be defined by subclasses
     */
    public abstract void start();
    /**
     * Stops the server
     * @throws Exception if something goes wrong
     */
    public abstract void stop() throws Exception;

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
