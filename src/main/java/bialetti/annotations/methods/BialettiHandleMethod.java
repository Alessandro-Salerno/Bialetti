package bialetti.annotations.methods;

import bialetti.server.tcp.BialettiTCPServerClient;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation
 * Marks a method for continuous execution on a separate thread until the service is terminated
 * @apiNote used inside @{@link bialetti.service.BialettiManagedService} child classes
 * @author Alessandro-Salerno
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BialettiHandleMethod { }
