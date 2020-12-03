package com.example.restfulrequest.http.handler;

import com.example.restfulrequest.annotation.HTTPRequest;
import com.example.restfulrequest.http.HttpResult;
import com.example.restfulrequest.http.StringHttpResult;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;

public class DemoHttpHandler implements HTTPHandler {

    RestTemplate restTemplate = new RestTemplate();

    @Override
    public HttpResult<?> handle(Method method) {
        // 方法的注解信息
        HTTPRequest request = method.getAnnotation(HTTPRequest.class);
        String url = request.url();
        String methodName = request.method().name();
        String res = restTemplate.getForObject(url, String.class);
        return new StringHttpResult(res, 200);
    }
}