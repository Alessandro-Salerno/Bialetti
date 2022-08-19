package bialetti.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A utility that calls a given method on a target object when requested
 * @author Alessandro-Salerno
 */
public final class MethodCaller {
    /**
     * The object on which the method is called
     */
    private final Object target;
    /**
     * The method to call
     */
    private final Method method;
    /**
     * The method's arguments
     */
    private  final Object[] args;

    /**
     * Constructor
     * @param o the target object
     * @param m the method
     * @param a the method's arguments
     */
    public MethodCaller(Object o, Method m, Object... a) {
        target = o;
        method = m;
        args = a;
    }

    /**
     * Calls the method
     * @throws Throwable if an exception is farwarded from the method
     */
    public void call() throws Throwable {
        try { method.invoke(target); }

        catch (IllegalAccessException iae) {
            iae.printStackTrace();
        }

        catch (InvocationTargetException ite) {
            // Throw the underlying exception
            throw ite.getCause();
        }
    }
}
