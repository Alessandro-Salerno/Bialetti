package bialetti.annotations;

import bialetti.server.tcp.BialettiTCPServerClient;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation
 * Makes a method a valid connection handler
 * @apiNote used inside @{@link BialettiTCPServerClient} child classes
 * @author Alessandro-Salerno
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BialettiHandleMethod { }
