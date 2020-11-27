package com.example.restfulrequest.http;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StringHttpResult implements HttpResult<String> {
    private String result;
    private int status;

    @Override
    public String getResponse() {
        return this.result;
    }

    @Override
    public int getStatus() {
        return this.status;
    }
}