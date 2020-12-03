package com.example.restfulrequest.http.handler;


import com.example.restfulrequest.JsonResult;
import com.example.restfulrequest.annotation.HTTPRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RestTemplateHTTPHandler implements HTTPHandler {

    RestTemplate restTemplate = new RestTemplate();
    ObjectMapper objectMapper = new ObjectMapper();

    public RestTemplateHTTPHandler() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Object handle(Method method, Object[] args, String service) {
        // 方法的注解信息
        HTTPRequest httpRequest = method.getAnnotation(HTTPRequest.class);
        String path = httpRequest.url();
        String message = httpRequest.method().name();

        // 方法的参数
        Parameter[] parameters = method.getParameters();
        Class<?> resCls = method.getReturnType();
        Object body = null;
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        Map<String, Object> pathVariables = new HashMap<>();

        for (int i = 0; i < parameters.length; i++) {
            // body参数
            if (parameters[i].isAnnotationPresent(RequestBody.class)) {
                body = args[i];
                continue;
            }
            // params参数
            if (parameters[i].isAnnotationPresent(RequestParam.class)) {
                Object arg = args[i];
                params.set(parameters[i].getAnnotation(RequestParam.class).value(), arg.toString());
            }
            // path参数
            if (parameters[i].isAnnotationPresent(PathVariable.class)) {
                String key = parameters[i].getAnnotation(PathVariable.class).value();
                pathVariables.put(key, args[i]);
            }
        }

        // url
        StringBuilder sb = new StringBuilder();
        if (!service.equals("")) {
            String endpoint = getServer(service);
            sb.append(String.format("http://%s", endpoint));
        }
        sb.append(path);
        URI uri  = UriComponentsBuilder.fromHttpUrl(sb.toString()).queryParams(params).buildAndExpand(pathVariables).encode().toUri();

        // header
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<Object> httpEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Object> responseEntity;
        try {
            responseEntity = restTemplate.exchange(uri, HttpMethod.resolve(message), httpEntity, Object.class);
            // 处理http状态码
            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                throw new Exception("http状态码非200");
            }
        } catch (Exception e) {
            String err = String.format("调用%s:%s失败：%s", service, uri.toString(), e.getMessage());
            log.error(err);
            throw new RuntimeException(err, e);
        }

        // 处理body
        JsonResult jsonResult = objectMapper.convertValue(responseEntity.getBody(), JsonResult.class);
        if (jsonResult.getCode() != 200) {
            String err = String.format("调用%s异常", service);
            log.error(err);
            throw new RuntimeException(err);
        }

        if (resCls == void.class) {
            return null;
        }
        if (resCls == String.class) {
            try {
                return objectMapper.writeValueAsString(jsonResult.getData());
            } catch (JsonProcessingException e) {
                String err = "反序列化成字符串失败";
                log.error(err);
                throw new RuntimeException(err, e);
            }
        }
        return objectMapper.convertValue(jsonResult.getData(), resCls);
    }
}