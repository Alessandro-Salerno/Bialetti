package bialetti.service;

import bialetti.BialettiExceptionHandler;
import bialetti.annotations.methods.BialettiHandleMethod;
import bialetti.exceptions.BialettiIllegalOperationException;
import bialetti.util.MethodThread;
import bialetti.util.ObjectUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * A Bialetti Service
 * @author Alessandro-Salerno
 */
abstract class BialettiService extends BialettiExceptionHandler {
    /**
     * The list of threads used to run service methods
     */
    private final List<MethodThread> threads;
    /**
     * The thread used to run the start method
     */
    private final Thread startThread;
    /**
     * A boolean that tells whether the service is running
     */
    private boolean running = false;

    /**
     * Constructor
     */
    public BialettiService() {
        // Set up service threads
        threads     = new ArrayList<>();
        startThread = new Thread(this::start);
    }

    /**
     * Starts the thread to run the start method
     * @throws BialettiIllegalOperationException if the start method throws one
     * @throws IllegalThreadStateException if the thread is in an invalid state
     */
    protected final void init() throws BialettiIllegalOperationException,
                                       IllegalThreadStateException {
        startThread.start();
    }

    /**
     * Spawns threads for all methods
     * @throws BialettiIllegalOperationException if the service is already running
     * @throws IllegalCallerException if the method is called directly
     */
    protected void start() throws BialettiIllegalOperationException,
                                  IllegalCallerException {
        // Make sure that the service is not running
        if (isRunning()) {
            throw new BialettiIllegalOperationException("Service \"" + getClass().getSimpleName() + "\" is already running");
        }

        // Make sure that the method is running on the right thread
        if (!Thread.currentThread().equals(startThread)) {
            throw new IllegalCallerException("Direct call to " + getClass().getSimpleName() + "\".start()");
        }

        // Spawn hanlder threads
        new ObjectUtility(this).forEachMethodWithAnnotation(BialettiHandleMethod.class,
                                                               method -> {
            MethodThread newThread
                    = new MethodThread(this::raiseException,
                                       this,
                                       method) {
                @Override
                public void run() {
                    try { startThread.join(); }
                    catch (InterruptedException ignored) { }

                    super.run();
                }
            };

            newThread.start();
            threads.add(newThread);
        });
        
        // State that the service has been started
        running = true;
    }

    /**
     * Clears all threads
     * @throws BialettiIllegalOperationException if the service was not running when the method was called
     */
    public void stop() throws BialettiIllegalOperationException {
        // Make sure that the service is running
        if (!isRunning()) {
            throw new BialettiIllegalOperationException("Service \"" + getClass().getSimpleName() + "\" is not running");
        }

        // Stop all threads
        synchronized (threads) {
            threads.stream()
                    .parallel()
                    .forEach(Thread::interrupt);

            threads.clear();
        }

        // Update
        running = false;
    }

    /**
     * @return Whether the service is running or not
     */
    public final boolean isRunning() { return running; }
}
