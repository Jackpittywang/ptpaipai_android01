package com.putao.ahibernate.annotation;

/**
 * Created by jidongdong on 15/3/25.
 */

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface OneToMany {

    /**
     * (Optional) The entity class that is the target of the association.
     * Optional only if the collection property is defined using Java generics.
     * Must be specified otherwise.
     * <p/>
     * <p/>
     * Defaults to the parameterized type of the collection when defined using
     * generics.
     */
    Class targetEntity() default void.class;

    /**
     * The field that owns the relationship. Required unless the relationship is
     * unidirectional.
     */
    String mappedBy() default "";
}
