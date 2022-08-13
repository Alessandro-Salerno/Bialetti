package bialetti.annotations.exceptions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation
 * Makes the method a valid exception handler
 * @apiNote used on methods of {@link bialetti.BialettiExceptionHandler}
 * @author Alessandro-Salerno
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BialettiGenericExceptionHandlerMethod { }
