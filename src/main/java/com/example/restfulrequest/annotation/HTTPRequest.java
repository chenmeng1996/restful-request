package com.example.restfulrequest.annotation;

import com.example.restfulrequest.http.HTTPMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HTTPRequest {
    HTTPMethod method() default HTTPMethod.GET;
    String url();
}