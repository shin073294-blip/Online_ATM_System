package com.talent.java.batch11.springbootapp.serviceimpl;

import com.talent.java.batch11.springbootapp.model.Account;
import com.talent.java.batch11.springbootapp.repository.AccountRepository;
import com.talent.java.batch11.springbootapp.service.TokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Service
public class TokenServiceImpl implements TokenService {

    @Value("${jwt.secretkey}")
    private String secretKey;

    @Value("${jwt.accessexpiration}")
    private int accessExpiration;

    @Value("${jwt.refreshexpiration}")
    private int refreshExpiration;

    @Autowired
    private AccountRepository accountRepository;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    @Override
    public String generateAccessToken(Account account) {
        return Jwts.builder()
                .subject(account.getEmail())
                .claim("name", account.getName())
                .claim("account_id", account.getId())
                .claim("email", account.getEmail())
                .claim("ROLE", account.getRole() != null ? account.getRole() : "USER")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String generateRefreshToken(Account account) {
        return Jwts.builder()
                .subject(account.getEmail())
                .claim("name", account.getName())
                .claim("account_id", account.getId())
                .claim("email", account.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public Authentication parseToken(String token) {
        try {
            Jws<Claims> jwt = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            Claims payload = jwt.getPayload();
            String email = payload.getSubject();
            String roles = payload.get("ROLE", String.class);

            Optional<Account> optional = Optional.ofNullable(accountRepository.findAccountByEmail(email));
            if (optional.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not found.");
            }
            Account account = optional.get();

            var authorities = Arrays.stream(roles != null ? roles.split(",") : new String[0])
                    .filter(r -> !r.trim().isEmpty())
                    .map(SimpleGrantedAuthority::new)
                    .toList();


            return UsernamePasswordAuthenticationToken.authenticated(
                    email,
                    null,
                    authorities
            );

        } catch (ExpiredJwtException e) {
            throw new JwtException("Access token is expired.", e);
        } catch (JwtException e) {
            throw new JwtException("Access token is invalid.", e);
        }
    }

    @Override
    public Account getAccountByToken(String token) {
        try {
            Jws<Claims> jwt = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            String email = jwt.getPayload().getSubject();
            Optional<Account> optional = Optional.ofNullable(accountRepository.findAccountByEmail(email));
            if (optional.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not found.");
            }
            return optional.get();
        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token.");
        }
    }
}
