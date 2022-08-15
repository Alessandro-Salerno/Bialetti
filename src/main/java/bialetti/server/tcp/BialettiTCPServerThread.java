package bialetti.server.tcp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Subclass of {@link Thread}
 * Used to handle a client
 * @param <ClientType> the type that defines a client (Subclass of {@link BialettiTCPServerClient})
 * @author Alessandro-Salerno
 */
class BialettiTCPServerThread<ClientType extends BialettiTCPServerClient<?>> extends Thread {
    /**
     * The target client
     */
    private final ClientType client;
    /**
     * The host server
     */
    private final BialettiTCPServer<ClientType> hostServer;
    /**
     * The exception handler
     */
    private final BialettiTCPServerExceptionHandler<ClientType> exceptionHandler;
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
    public BialettiTCPServerThread(ClientType c,
                                   Method handler,
                                   BialettiTCPServer<ClientType> s,
                                   BialettiTCPServerExceptionHandler<ClientType> sxh) {
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
