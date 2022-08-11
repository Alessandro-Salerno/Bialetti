package bialetti;

import bialetti.annotations.BialettiGenericExceptionHandlerMethod;

import java.lang.reflect.Method;

/*
 * The standard implementation for a BialettiServerExceptionHandler
 * @author Alessandro-Salerno
 */
public abstract class BialettiExceptionHandler {
    /*
     * Calls the right handler method for the exception
     * @param exception The Exception itself
     */
    public final void raise(Exception exception) {
        try {
            Method handlerMethod = getHandlerMethod(
                    exception, BialettiGenericExceptionHandlerMethod.class,
                    exception.getClass()
            );

            // Call handler method
            handlerMethod.invoke(this, exception);
        }

        // What happens if there's no dedicated handler methd
        catch (NoSuchMethodException noSuchMethodException) {
            // Call the generic exception handler
            onException(exception);
        }

        // If some other exception is raised during the process
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Gets the right method
     * @param exception The exception
     * @param annotation The annotation requried for the method to be ok
     * @param ...parameterTypes The types of the required parameters
     */
    @SuppressWarnings("unchecked")
    protected Method getHandlerMethod(Exception exception, Class annotation, Class<?>... parameterTypes) throws NoSuchMethodException {
        // Get handler method from the class
        Method handlerMethod = getClass().getMethod(
                "on" + exception.getClass().getSimpleName(),
                parameterTypes
        );

        // Throw NoSuchMethodException if the method is not marked as exception handler
        if (!handlerMethod.isAnnotationPresent(annotation)) {
            throw new NoSuchMethodException();
        }

        return handlerMethod;
    }

    /*
     * What happens when a generic exception is thrown
     * @param exception The exception
     */
    @BialettiGenericExceptionHandlerMethod
    public void onException(Exception exception) {
        exception.printStackTrace();
    }
}
