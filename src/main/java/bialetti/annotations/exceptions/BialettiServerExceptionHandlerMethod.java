package bialetti.annotations.exceptions;

import bialetti.server.tcp.BialettiTCPServerExceptionHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation
 * Makes the method a valid server exception handler
 * @apiNote used on methods of {@link BialettiTCPServerExceptionHandler}
 * @author Alessandro-Salerno
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BialettiServerExceptionHandlerMethod { }
