package bialetti.server;

import bialetti.BialettiExceptionHandler;
import bialetti.annotations.exceptions.BialettiServerExceptionHandlerMethod;

import java.lang.reflect.Method;

public abstract class BialettiServerExceptionHandler extends BialettiExceptionHandler {
    /**
     * Calls the right handler method for a given throwable
     * @param throwable the Exception itself
     * @param bialettiServer the server
     */
    public final void raise(Throwable throwable, BialettiServer bialettiServer) {
        try {
            Method handlerMethod = getHandlerMethod(throwable,
                                                    BialettiServerExceptionHandlerMethod.class,
                                                    throwable.getClass(),
                                                    bialettiServer.getClass());

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
