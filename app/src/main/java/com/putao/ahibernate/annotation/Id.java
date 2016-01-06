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
public @interface Id {
    String name() default "";

    boolean autoGenerate() default false;
}