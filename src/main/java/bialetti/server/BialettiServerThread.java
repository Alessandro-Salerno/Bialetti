package bialetti.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
     * The exception handler
     */
    private final BialettiServerExceptionHandler<T> exceptionHandler;
    /*
     * The client's handler method that the thread
     * is tasked to call
     */
    private final Method handlerMethod;

    /*
     * Default constructor
     * @param c The BIalettiServerClientRepresentation instance of the target client
     * @param s The BialettiServer instance of the host server
     * @param sxh The BialettiServerExceptionHandler instance
     */
    public BialettiServerThread(T c, Method handler, BialettiServer<T> s, BialettiServerExceptionHandler<T> sxh) {
        client           = c;
        handlerMethod    = handler;
        hostServer       = s;
        exceptionHandler = sxh;
    }

    @Override
    @SuppressWarnings("all")
    public void run() {
        // Main handler loop
        while (!Thread.interrupted()) {
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
