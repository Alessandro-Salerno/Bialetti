package bialetti.server;

import bialetti.BialettiConnection;
import bialetti.BialettiExceptionHandler;
import bialetti.annotations.BialettiServerExceptionMethod;

import java.lang.reflect.Method;

public abstract class BialettiServerExceptionHandler extends BialettiExceptionHandler {
    /*
     * Calls the right handler method for the exception
     * @param exception The Exception itself
     * @param bialettiConnection The connection that caused the exception
     * @param bialettiServer The server handling the connection
     */
    public final void raise(Exception exception, BialettiConnection bialettiConnection, BialettiServer bialettiServer) {
        try {
            Method handlerMethod = getHandlerMethod(
                    exception, BialettiExceptionHandler.class,
                    exception.getClass(), bialettiConnection.getClass(), bialettiServer.getClass()
            );

            // Call handler method
            handlerMethod.invoke(this, exception, bialettiConnection, bialettiServer);
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
    public final void raise(Exception exception, BialettiServer bialettiServer) {
        try {
            Method handlerMethod = getHandlerMethod(
                    exception, BialettiServerExceptionMethod.class,
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
