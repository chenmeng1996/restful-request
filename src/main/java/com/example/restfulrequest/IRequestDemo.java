package com.example.restfulrequest;

import com.example.restfulrequest.http.HTTPMethod;
import com.example.restfulrequest.annotation.HTTPRequest;
import com.example.restfulrequest.annotation.HTTPUtil;
import com.example.restfulrequest.http.HttpResult;
import org.springframework.stereotype.Component;

@Component
@HTTPUtil
public interface IRequestDemo {

    @HTTPRequest(url = "http://www.baidu.com")
    HttpResult<String> test1();

    @HTTPRequest(url = "http://test2.com", httpMethod = HTTPMethod.POST)
    HttpResult<String> test2();
}