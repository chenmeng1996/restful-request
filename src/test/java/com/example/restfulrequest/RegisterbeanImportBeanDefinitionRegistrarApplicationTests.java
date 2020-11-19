package com.example.restfulrequest;

import com.example.restfulrequest.http.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
public class RegisterbeanImportBeanDefinitionRegistrarApplicationTests {
    @Autowired
    IRequestDemo iRequestDemo;

    @Test
    public void test1() {
        HttpResult<String> result = this.iRequestDemo.test1();
        String response = result.getResponse();
        log.info(">>>>>>>>>>{}", response);
        assertEquals("http request: url=http://abc.com and method=GET", response);
    }

    @Test
    public void test2() {
        HttpResult<String> result = this.iRequestDemo.test2();
        String response = result.getResponse();
        log.info(">>>>>>>>>>{}", response);
        assertEquals("http request: url=http://test2.com and method=POST", response);
    }

}