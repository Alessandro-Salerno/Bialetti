package bialetti.server.udp;

import bialetti.server.BialettiServerExceptionHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Subclass of {@link Thread}
 * Used to call a handler method on a separated thread
 * @author Alessandro-Salerno
 */
class BialettiUDPServerThread extends Thread {
    /**
     * The exception handler
     */
    private final BialettiServerExceptionHandler exceptionHandler;
    /**
     * The server
     */
    private final BialettiUDPServer hostServer;
    /**
     * The handler method
     */
    private final Method handlerMethod;

    /**
     * Constructor
     * @param m the method to call
     * @param s the server
     * @param sxh the exception handler
     */
    public BialettiUDPServerThread(Method m,
                                   BialettiUDPServer s,
                                   BialettiServerExceptionHandler sxh) {
        handlerMethod    = m;
        hostServer       = s;
        exceptionHandler = sxh;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            // Call dedicated handler
            try { handlerMethod.invoke(hostServer); }

            catch (InvocationTargetException e) {
                // Call handler method
                exceptionHandler.raise(e.getCause(), hostServer);
            }

            catch (IllegalAccessException iae) {
                iae.printStackTrace();
                return;
            }
        }
    }
}
