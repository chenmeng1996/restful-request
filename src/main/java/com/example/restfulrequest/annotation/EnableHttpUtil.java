package com.example.restfulrequest.annotation;

import com.example.restfulrequest.http.HTTPRequestRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(HTTPRequestRegistrar.class)
public @interface EnableHttpUtil {

}