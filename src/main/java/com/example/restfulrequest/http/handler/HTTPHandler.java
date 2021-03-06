package com.example.restfulrequest.http.handler;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public interface HTTPHandler {
    Object handle(Method method, Object[] args, String service);

    default List<String> getServers(String service) {
        return new ArrayList<>();
    }

    default String getServer(String service) {
        return "";
    }
}