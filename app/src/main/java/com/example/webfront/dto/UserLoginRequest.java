package com.example.webfront.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;

@Builder
@AllArgsConstructor
public class UserLoginRequest {

    private String userId;
    private String password;

}
