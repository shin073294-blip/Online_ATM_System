package com.talent.java.batch11.springbootapp.request;

import lombok.Data;

@Data
public class RegisterInfo {
    private String name;
    private String email;
    private String password;
    private String confirmPassword;
    private String phoneNumber;
    private String address;
}
