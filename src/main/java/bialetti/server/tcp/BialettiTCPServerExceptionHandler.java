package bialetti.server.tcp;

import bialetti.BialettiExceptionHandler;
import bialetti.annotations.exceptions.BialettiExceptionHandlerMethod;
import bialetti.server.BialettiServerExceptionHandler;

import java.lang.reflect.Method;

/**
 * Server-side extension of {@link BialettiExceptionHandler}
 * @param <ClientType> the type that defines the client (Subclass of {@link BialettiTCPServerClient})
 * @author Alessandro-Salerno
 */
public abstract class BialettiTCPServerExceptionHandler<ClientType extends BialettiTCPServerClient<?>>
        extends BialettiServerExceptionHandler {
    /**
     * Calls the right handler method for a given throwable
     * @param throwable the exception itself
     * @param client the client that caused the throwable
     * @param bialettiTCPServer the server handling the connection
     */
    public final void raise(Throwable throwable, ClientType client, BialettiTCPServer<ClientType> bialettiTCPServer) {
        try {
            Method handlerMethod = getHandlerMethod(throwable,
                                                    BialettiExceptionHandlerMethod.class,
                                                    throwable.getClass(),
                                                    client.getClass(),
                                                    bialettiTCPServer.getClass());

            // Call handler method
            handlerMethod.invoke(this, throwable, client, bialettiTCPServer);
        }

        // What happens if there's no dedicated handler methd
        catch (NoSuchMethodException noSuchMethodException) {
            // Call the generic throwable handler
            raise(throwable, bialettiTCPServer);
        }

        // If some other throwable is raised during the process
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
