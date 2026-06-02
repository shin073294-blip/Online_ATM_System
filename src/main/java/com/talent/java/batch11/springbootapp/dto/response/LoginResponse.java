package com.talent.java.batch11.springbootapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class LoginResponse {
        private String message;
        private Long accountId;
        private String name;
        private String role;
    }

