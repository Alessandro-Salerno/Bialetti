package bialetti.server;

/*
 * Class that extends java s standard Thread
 * Used to handle clients
 * @author Alessandro-Salerno
 */
class BialettiServerThread<T> extends Thread {
    /*
     * The target client
     */
    private final T client;
    /*
     * The host server
     */
    private final BialettiServer<T> hostServer;
    /*
     * The event handler
     */
    private final BialettiConnectionEventHandler<T> eventHandler;
    /*
     * The cexception handler
     */
    private final BialettiServerExceptionHandler<T> exceptionHandler;

    /*
     * Default constructor
     * @param client The BialettiConnection instance of the target client
     * @param server The BialettiServer instance of the host server
     * @param handler The BialettiEventHandler instance for the target server
     * @param exHandler The BialettiServerExceptionHandler instance
     */
    public BialettiServerThread(T client, BialettiServer<T> server, BialettiConnectionEventHandler<T> handler, BialettiServerExceptionHandler<T> exHandler) {
        this.client = client;
        hostServer       = server;
        eventHandler     = handler;
        exceptionHandler = exHandler;
    }

    @Override
    public void run() {
        try {
            // Call initial connection method
            eventHandler.onConnect(client, hostServer);
        }

        // Exception handler
        catch (Exception e) {
            // Call handler method
            exceptionHandler.raise(e);
        }

        // Main handler loop
        while (!Thread.interrupted()) {
            try {
                // Call handler method
                eventHandler.handle(client, hostServer);
            }

            // Exception handler
            catch (Exception e) {
                // Call exception handler method
                exceptionHandler.raise(e, client, hostServer);
            }
        }
    }
}
