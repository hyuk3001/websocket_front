package com.example.webfront.dto;

import lombok.Builder;
import lombok.AllArgsConstructor;


@Builder
@AllArgsConstructor
public class UserRegistrationRequest {

    private String userId;
    private String password;
    private String name;
    private String phone;
    private String email;
}