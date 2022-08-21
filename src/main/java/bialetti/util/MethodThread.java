package bialetti.util;

import bialetti.BialettiExceptionHandler;
import bialetti.annotations.exceptions.BialettiExceptionHandlerMethod;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * Subclass of {@link Thread}
 * Calls a method until the thread is interrupted and forwards exceptions to a handler
 * @author Alessandro-Salerno
 */
public class MethodThread extends Thread {
    /**
     * The exception handler
     */
    private final BialettiExceptionHandler exceptionHandler;
    /**
     * Caller utility
     */
    private final MethodCaller caller;

    /**
     * Constructor
     * @param eh the exception handler
     * @param target the object on which the method will be called
     * @param m the method
     * @param args the method's argumentts
     */
    public MethodThread(BialettiExceptionHandler eh,
                        Object target,
                        Method m,
                        Object... args) {
        exceptionHandler = eh;
        caller           = new MethodCaller(target, m, args);
    }

    /**
     * Constructor
     * @param handler a lambda expression that handles a generic exception
     * @param target the object on which the method will be called
     * @param m the method
     * @param args the method's argumentts
     */
    public MethodThread(Consumer<Throwable> handler,
                        Object target,
                        Method m,
                        Object... args) {
        this(new BialettiExceptionHandler() {
            @Override
            @BialettiExceptionHandlerMethod
            public void onThrowable(Throwable throwable) {
                handler.accept(throwable);
            }
        }, target, m, args);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try { caller.call(); }
            catch (Throwable t) {
                // Call handler method
                exceptionHandler.raiseException(t);
            }
        }
    }
}
