package bialetti.server;

import bialetti.BialettiExceptionHandler;
import bialetti.annotations.BialettiExceptionHandlerMethod;
import bialetti.annotations.BialettiServerExceptionHandlerMethod;

import java.lang.reflect.Method;

public abstract class BialettiServerExceptionHandler<ClientType> extends BialettiExceptionHandler {
    /*
     * Calls the right handler method for the throwable
     * @param throwable The Exception itself
     * @param client The client that caused the throwable
     * @param bialettiServer The server handling the connection
     */
    public final void raise(Throwable throwable, ClientType client, BialettiServer<ClientType> bialettiServer) {
        try {
            Method handlerMethod = getHandlerMethod(
                    throwable, BialettiExceptionHandlerMethod.class,
                    throwable.getClass(), client.getClass(), bialettiServer.getClass()
            );

            // Call handler method
            handlerMethod.invoke(this, throwable, client, bialettiServer);
        }

        // What happens if there's no dedicated handler methd
        catch (NoSuchMethodException noSuchMethodException) {
            // Call the generic throwable handler
            raise(throwable, bialettiServer);
        }

        // If some other throwable is raised during the process
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Calls the right handler method for the throwable
     * @param throwable The Exception itself
     * @param bialettiServer The server handling the connection
     */
    public final void raise(Throwable throwable, BialettiServer<ClientType> bialettiServer) {
        try {
            Method handlerMethod = getHandlerMethod(
                    throwable, BialettiServerExceptionHandlerMethod.class,
                    throwable.getClass(), bialettiServer.getClass()
            );

            // Call handler method
            handlerMethod.invoke(this, throwable, bialettiServer);
        }

        // What happens if there's no dedicated handler methd
        catch (NoSuchMethodException noSuchMethodException) {
            // Call the generic throwable handler
            raise(throwable);
        }

        // If some other throwable is raised during the process
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
