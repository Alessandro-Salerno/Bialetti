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
     * The cexception handler
     */
    private final BialettiServerExceptionHandler<T> exceptionHandler;

    /*
     * Default constructor
     * @param client The BialettiConnection instance of the target client
     * @param server The BialettiServer instance of the host server
     * @param exHandler The BialettiServerExceptionHandler instance
     */
    public BialettiServerThread(T c, BialettiServer<T> s, BialettiServerExceptionHandler<T> sxh) {
        client           = c;
        hostServer       = s;
        exceptionHandler = sxh;
    }

    @Override
    @SuppressWarnings("all")
    public void run() {
        try {
            // Call initial connection method
            ((BialettiServerClientRepresentation) client).onConnect();
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
                ((BialettiServerClientRepresentation) client).handle();
            }

            // Exception handler
            catch (Exception e) {
                // Call exception handler method
                exceptionHandler.raise(e, client, hostServer);
            }
        }
    }
}
