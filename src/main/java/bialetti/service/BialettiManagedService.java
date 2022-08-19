package bialetti.service;

import bialetti.annotations.methods.BialettiEndMethod;
import bialetti.annotations.methods.BialettiInitMethod;
import bialetti.exceptions.BialettiIllegalOperationException;
import bialetti.util.MethodCaller;
import bialetti.util.ObjectUtility;

/**
 * Subclass of {@link BialettiService}
 * A Bialetti Service that also calls init and end methods
 * @author Alessandro-Salerno
 */
public class BialettiManagedService extends BialettiService {
    @Override
    protected void start() throws BialettiIllegalOperationException {
        super.start();

        // Call init methods
        new ObjectUtility(this).forEachMethodWithAnnotation(BialettiInitMethod.class,
                                                               method -> {
            try { new MethodCaller(this,
                                   method).call(); }
            catch (Throwable t) {
                // Call handler method
                raiseException(t);
            }
        });
    }

    @Override
    public void stop() throws BialettiIllegalOperationException {
        super.stop();

        // Call end methods
        new ObjectUtility(this).forEachMethodWithAnnotation(BialettiEndMethod.class,
                                                               method -> {
            try { new MethodCaller(this,
                                   method).call(); }
            catch (Throwable t) {
                // Call handler method
                raiseException(t);
            }
        });
    }
}
