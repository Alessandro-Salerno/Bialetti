package bialetti.annotations.methods;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation
 * Marks a method for execution when the service is terminated
 * @apiNote used inside @{@link bialetti.service.BialettiManagedService} child classes
 * @author Alessandro-Salerno
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BialettiEndMethod { }
