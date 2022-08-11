package bialetti.server;

import bialetti.BialettiExceptionHandler;
import bialetti.annotations.BialettiExceptionHandlerMethod;
import bialetti.annotations.BialettiServerExceptionHandlerMethod;

import java.lang.reflect.Method;

public abstract class BialettiServerExceptionHandler<T> extends BialettiExceptionHandler {
    /*
     * Calls the right handler method for the exception
     * @param exception The Exception itself
     * @param client The client that caused the exception
     * @param bialettiServer The server handling the connection
     */
    public final void raise(Exception exception, T client, BialettiServer<T> bialettiServer) {
        try {
            Method handlerMethod = getHandlerMethod(
                    exception, BialettiExceptionHandlerMethod.class,
                    exception.getClass(), client.getClass(), bialettiServer.getClass()
            );

            // Call handler method
            handlerMethod.invoke(this, exception, client, bialettiServer);
        }

        // What happens if there's no dedicated handler methd
        catch (NoSuchMethodException noSuchMethodException) {
            // Call the generic exception handler
            raise(exception, bialettiServer);
        }

        // If some other exception is raised during the process
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Calls the right handler method for the exception
     * @param exception The Exception itself
     * @param bialettiServer The server handling the connection
     */
    public final void raise(Exception exception, BialettiServer<T> bialettiServer) {
        try {
            Method handlerMethod = getHandlerMethod(
                    exception, BialettiServerExceptionHandlerMethod.class,
                    exception.getClass(), bialettiServer.getClass()
            );

            // Call handler method
            handlerMethod.invoke(this, exception, bialettiServer);
        }

        // What happens if there's no dedicated handler methd
        catch (NoSuchMethodException noSuchMethodException) {
            // Call the generic exception handler
            raise(exception);
        }

        // If some other exception is raised during the process
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
