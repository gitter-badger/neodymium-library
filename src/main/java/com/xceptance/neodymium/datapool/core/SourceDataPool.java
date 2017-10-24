package com.xceptance.neodymium.datapool.core;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface SourceDataPool
{
    Class<? extends DataListPool<?>> value();
}
