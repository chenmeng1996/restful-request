package com.example.restfulrequest.http.handler;

import com.example.restfulrequest.annotation.HTTPRequest;
import com.example.restfulrequest.http.HttpResult;
import com.example.restfulrequest.http.StringHttpResult;

import java.lang.reflect.Method;

public class DemoHttpHandler implements HTTPHandler {

    @Override
    public HttpResult<?> handle(Method method) {
        HTTPRequest request = method.getAnnotation(HTTPRequest.class);
        String url = request.url();
        String methodName = request.httpMethod().name();
        String str = String.format("http request: url=%s and method=%s", url, methodName);
        return new StringHttpResult(str, 200);
    }
}