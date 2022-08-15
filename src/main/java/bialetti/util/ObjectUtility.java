package bialetti.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Bialetti utility class that provides methods to inspect objects
 * @authr Alessandro-Salerno
 */
public class ObjectUtility {
    /**
     * The object to be inspected
     */
    private final Object object;

    /**
     * Constructor
     * @param o the object to be inspected
     */
    public ObjectUtility(Object o) {
        object = o;
    }

    /**
     * Runs an action for every method of the object if a condition is met
     * @param condition the condition (Example: method -> method.isAnnotationPresent(...))
     * @param action the action to perform if the condition is met (Example: method -> System.out.println(method.toString()))
     */
    public void forEachMethodIf(Function<? super Method, Boolean> condition, Consumer<? super Method> action) {
        Arrays.stream(object.getClass().getMethods()).parallel().forEach(method -> {
            if (condition.apply(method)) {
                action.accept(method);
            }
        });
    }

    /**
     * Runs an action if a method holds a given annotation
     * @param annotation the annotation required
     * @param action the action to be performed
     */
    public void forEachMethodWithAnnotation(Class<? extends Annotation> annotation, Consumer<? super Method> action) {
        forEachMethodIf(method -> method.isAnnotationPresent(annotation), action);
    }
}
