package com.pomelo.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * @program: FakeSpringFramework
 * @description:
 * @author: zhengxu
 **/

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PomeloService {
    String value() default "";
}
