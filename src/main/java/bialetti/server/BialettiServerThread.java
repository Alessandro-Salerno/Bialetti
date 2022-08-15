package bialetti.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Subclass of {@link Thread}
 * Used to handle a client
 * @param <ClientType> the type that defines a client (Subclass of {@link BialettiServerClientRepresentation})
 * @author Alessandro-Salerno
 */
class BialettiServerThread<ClientType extends BialettiServerClientRepresentation<?>> extends Thread {
    /**
     * The target client
     */
    private final ClientType client;
    /**
     * The host server
     */
    private final BialettiServer<ClientType> hostServer;
    /**
     * The exception handler
     */
    private final BialettiServerExceptionHandler<ClientType> exceptionHandler;
    /**
     * The client's handler method that the thread
     * is tasked to call
     */
    private final Method handlerMethod;

    /**
     * Constructor
     * @param c the BialettiServerClientRepresentation instance of the target client
     * @param s the BialettiServer instance of the host server
     * @param sxh the BialettiServerExceptionHandler instance
     */
    public BialettiServerThread(ClientType c,
                                Method handler,
                                BialettiServer<ClientType> s,
                                BialettiServerExceptionHandler<ClientType> sxh) {
        client           = c;
        handlerMethod    = handler;
        hostServer       = s;
        exceptionHandler = sxh;
    }

    @Override
    public void run() {
        // Main handler loop
        while (!Thread.interrupted()) {
            // Call dedicated handler
            try { handlerMethod.invoke(client); }

            catch (InvocationTargetException e) {
                // Call handler method
                exceptionHandler.raise(e.getCause(), client, hostServer);
            }

            catch (IllegalAccessException iae) {
                iae.printStackTrace();
                return;
            }
        }
    }
}
