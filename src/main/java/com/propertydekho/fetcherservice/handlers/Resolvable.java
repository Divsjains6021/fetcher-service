package com.propertydekho.fetcherservice.handlers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Resolvable {
    ResolutionGroup group() default ResolutionGroup.ONE;
}
