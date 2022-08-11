package bialetti.server;

import bialetti.BialettiConnection;

/*
 * Class that extends java s standard Thread
 * Used to handle clients
 * @author Alessandro-Salerno
 */
class BialettiServerThread extends Thread {
    /*
     * The target client
     */
    private BialettiConnection connectedClient;
    /*
     * The host server
     */
    private BialettiServer hostServer;
    /*
     * The event handler
     */
    private final BialettiConnectionEventHandler eventHandler;
    /*
     * The cexception handler
     */
    private final BialettiServerExceptionHandler exceptionHandler;

    /*
     * Default constructor
     * @param client The BialettiConnection instance of the target client
     * @param server The BialettiServer instance of the host server
     * @param handler The BialettiEventHandler instance for the target server
     * @param exHandler The BialettiServerExceptionHandler instance
     */
    public BialettiServerThread(BialettiConnection client, BialettiServer server, BialettiConnectionEventHandler handler, BialettiServerExceptionHandler exHandler) {
        connectedClient  = client;
        hostServer       = server;
        eventHandler     = handler;
        exceptionHandler = exHandler;
    }

    @Override
    public void run() {
        try {
            // Call initial connection method
            eventHandler.onConnect(connectedClient, hostServer);
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
                eventHandler.handle(connectedClient, hostServer);
            }

            // Exception handler
            catch (Exception e) {
                // Call exception handler method
                exceptionHandler.raise(e, connectedClient, hostServer);
            }
        }
    }
}
