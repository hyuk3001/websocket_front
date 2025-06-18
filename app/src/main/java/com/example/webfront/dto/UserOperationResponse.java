package com.example.webfront.dto;

import com.google.gson.annotations.SerializedName;

public class UserOperationResponse {
    @SerializedName("result")
    private boolean result;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Object data;

    public boolean isResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}