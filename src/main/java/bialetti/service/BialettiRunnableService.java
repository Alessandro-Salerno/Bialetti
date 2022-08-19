package bialetti.service;

import bialetti.annotations.methods.BialettiEndMethod;
import bialetti.exceptions.BialettiIllegalOperationException;
import bialetti.util.MethodCaller;
import bialetti.util.ObjectUtility;

/**
 * Subclass of {@link BialettiManagedService}
 * A Bialetti Managed Service that can run as a standalone program thanks to a dummy thread
 * @author Alessandro-Salernoo
 */
public abstract class BialettiRunnableService extends BialettiManagedService {
    /**
     * A thread used to keep the process alive
     */
    private final Thread dummyThread;

    /**
     * Constructor
     */
    public BialettiRunnableService() {
        // Set up dummy thread
        dummyThread = new Thread(() -> { while (!Thread.interrupted()); });
    }

    @Override
    protected void start() throws BialettiIllegalOperationException {
        super.start();

        // Start dummy thread
        dummyThread.start();
    }

    @Override
    public void stop() throws BialettiIllegalOperationException {
        super.stop();

        // Stop dummy thread
        dummyThread.interrupt();
    }
}
