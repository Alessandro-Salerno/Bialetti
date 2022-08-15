package bialetti;

import bialetti.annotations.exceptions.BialettiGenericExceptionHandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/*
 * The standard implementation for a BialettiServerExceptionHandler
 * @author Alessandro-Salerno
 */
public abstract class BialettiExceptionHandler {
    /*
     * Calls the right handler method for the throwable
     * @param throwable The Exception itself
     */
    public final void raise(Throwable throwable) {
        try {
            Method handlerMethod = getHandlerMethod(
                    throwable, BialettiGenericExceptionHandlerMethod.class,
                    throwable.getClass()
            );

            // Call handler method
            handlerMethod.invoke(this, throwable);
        }

        // What happens if there's no dedicated handler method
        catch (NoSuchMethodException noSuchMethodException) {
            // Call the generic throwable handler
            onThrowable(throwable);
        }

        // If some other throwable is raised during the process
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Gets the right method
     * @param throwable The throwable
     * @param annotation The annotation required for the method to be ok
     * @param ...parameterTypes The types of the required parameters
     */
    protected final Method getHandlerMethod(Throwable throwable, Class<? extends Annotation> annotation, Class<?>... parameterTypes) throws NoSuchMethodException {
        // Get handler method from the class
        Method handlerMethod = getClass().getMethod("on" + throwable.getClass().getSimpleName(),
                                                    parameterTypes);

        // Throw NoSuchMethodException if the method is not marked as throwable handler
        if (!handlerMethod.isAnnotationPresent(annotation)) {
            throw new NoSuchMethodException();
        }

        return handlerMethod;
    }

    @BialettiGenericExceptionHandlerMethod
    public void onThrowable(Throwable throwable) {
        throwable.printStackTrace();
    }
}
