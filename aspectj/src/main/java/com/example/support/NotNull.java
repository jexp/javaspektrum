package com.example.support;

import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE,FIELD,PARAMETER,METHOD,CONSTRUCTOR})
public @interface NotNull {
}