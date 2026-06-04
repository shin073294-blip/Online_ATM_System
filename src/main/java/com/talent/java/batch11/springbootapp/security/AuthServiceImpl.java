package com.talent.java.batch11.springbootapp.security;

import com.talent.java.batch11.springbootapp.serviceimpl.TokenServiceImpl;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class AuthServiceImpl extends OncePerRequestFilter{

    private final TokenServiceImpl tokenService;

    @Value("${api-key}")
    String appAPIKey;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String userAPIKey = request.getHeader("apiKey");

        try {
            if (appAPIKey.equals(userAPIKey)) {

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        "API_USER",
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);


                if (requestURI.equals("/account/login")|| requestURI.equals("/account/register")) {
                    filterChain.doFilter(request, response);
                    return;
                }

                String token = getTokenFromRequest(request);

                if (token != null) {
                    Authentication auth = tokenService.parseToken(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    filterChain.doFilter(request, response);
                }
                else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Bad Request - Missing access token\"}");
                }

            }
            else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Unauthorized - Missing or invalid API Key\"}");
            }

        } catch (JwtException  ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":" + ex.getMessage() + "}");
        }

    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String accessToken = request.getHeader("accessToken");
        if (accessToken != null && !accessToken.isBlank()) {
            return accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        }

        String bearer = request.getHeader("Authorization");

        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }

        return null;
    }
}
