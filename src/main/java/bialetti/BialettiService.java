package bialetti;

import bialetti.annotations.BialettiHandleMethod;
import bialetti.util.MethodThread;
import bialetti.util.ObjectUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * A Bialetti Service
 * @author Alessandro-Salerno
 */
public abstract class BialettiService extends BialettiExceptionHandler {
    /**
     * The list of threads used to run service methods
     */
    private final List<MethodThread> threads;
    /**
     * The thread used to run the start method
     */
    private final Thread startThread;
    /**
     * A thread used to keep the process alive
     */
    private final Thread dummyThread;
    /**
     * A boolean that tells whether the service has already been started
     */
    private boolean started = false;

    /**
     * Constructor
     */
    public BialettiService() {
        // Set up service threads
        threads     = new ArrayList<>();
        startThread = new Thread(this::start);

        // Set up dummy thread
        dummyThread = new Thread(() -> { while (!Thread.interrupted()); });
        dummyThread.start();
    }

    /**
     * Starts the thread to run the start method
     */
    protected final void init() {
        if (started) return;
        startThread.start();
    }

    /**
     * Spawns threads for all methods
     */
    protected void start() {
        // Do nothing if the service has already been started
        if (started) return;
        
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
        started = true;
    }

    /**
     * Clears all threads
     */
    public void stop() {
        // Stop all threads
        synchronized (threads) {
            threads.stream()
                    .parallel()
                    .forEach(Thread::interrupt);

            threads.clear();
        }

        // Stop dummy thread
        dummyThread.interrupt();
    }
}
