package com.wenjutian.injectleaning.refect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by V.Wenju.Tian on 2016/11/24.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CAnnotation {
    String name() default "张三";

    int age() default 10;
}
