package com.example.restfulrequest;

import com.example.restfulrequest.http.HttpResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    IRequestDemo requestDemo;

    @Test
    void contextLoads() {
        HttpResult<String> result = requestDemo.test1();
        System.out.println(result);
    }

}
