package com.example.webfront.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class UserRegistrationRequest {

    private String userId;
    private String password;
    private String name;
    private String phone;
    private String email;
//    private String zipcode;   이거 필요없을듯
//    private String streetAdr;
//    private String detailAdr;
    private String role;
}