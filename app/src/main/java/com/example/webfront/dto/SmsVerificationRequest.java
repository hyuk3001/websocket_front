package com.example.webfront.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SmsVerificationRequest {
    private String phone;
    private String code;
}
