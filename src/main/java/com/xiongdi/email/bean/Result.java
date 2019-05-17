package com.xiongdi.email.bean;

import lombok.Data;


public class Result {

    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Result(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
