package bialetti.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * Marker annotation
 * Makes the method a valid exception handler
 * @author Alessandro-Salerno
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BialettiGenericExceptionHandlerMethod { }
