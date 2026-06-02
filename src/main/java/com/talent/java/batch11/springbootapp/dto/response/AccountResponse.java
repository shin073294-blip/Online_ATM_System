package com.talent.java.batch11.springbootapp.dto.response;

import lombok.Data;

@Data
public class AccountResponse {
    private Long id;
    private String name;
    private String email;
    private String address;
    private String phoneNumber;
    private double balance;
    private String role;
}

