package com.example.restfulrequest.http.handler;

import com.example.restfulrequest.http.HttpResult;

import java.lang.reflect.Method;

public interface HTTPHandler {
    HttpResult<?> handle(Method method);
}