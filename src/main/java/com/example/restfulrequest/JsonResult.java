package com.example.restfulrequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * response结果封装
 * @param <T>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonResult<T> {

    public static final JsonResult<Object> SUCCESS_JSON_RESULT = new JsonResult<>(200, "success", null);
    public static final JsonResult<Object> FAIL_JSON_RESULT = new JsonResult<>(400, "fail", null);

    private Integer code = 200;
    private String message;
    private T data;

    public JsonResult(JsonResult<T> jsonResult) {
        this.code = jsonResult.getCode();
        this.message = jsonResult.getMessage();
        this.data = jsonResult.getData();
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> JsonResult<T> success(T data) {
        return new JsonResult<T>(200, "success", data);
    }

}
