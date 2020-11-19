package com.example.restfulrequest.http;

public interface HttpResult<T> {

    T getResponse();

    int getStatus();
}
