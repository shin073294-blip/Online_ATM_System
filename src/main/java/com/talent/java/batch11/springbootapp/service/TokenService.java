package com.talent.java.batch11.springbootapp.service;

import com.talent.java.batch11.springbootapp.model.Account;
import org.springframework.security.core.Authentication;

public interface TokenService {
    public String generateAccessToken(Account account);
    Authentication parseToken(String token);
    public String generateRefreshToken(Account account);
    public Account getAccountByToken(String token);
}