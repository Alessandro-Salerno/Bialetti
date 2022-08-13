package bialetti.server;

import bialetti.BialettiExceptionHandler;
import bialetti.annotations.exceptions.BialettiExceptionHandlerMethod;
import bialetti.annotations.exceptions.BialettiServerExceptionHandlerMethod;

import java.lang.reflect.Method;

/**
 * Server-side extension of {@link BialettiExceptionHandler}
 * @param <ClientType> the type that defines the client (Subclass of {@link BialettiServerClientRepresentation})
 * @author Alessandro-Salerno
 */
public abstract class BialettiServerExceptionHandler<ClientType extends BialettiServerClientRepresentation<?>> extends BialettiExceptionHandler {
    /**
     * Calls the right handler method for a given throwable
     * @param throwable the exception itself
     * @param client the client that caused the throwable
     * @param bialettiServer the server handling the connection
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

    /**
     * Calls the right handler method for a given throwable
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
