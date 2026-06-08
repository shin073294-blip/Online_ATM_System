package com.talent.java.batch11.springbootapp;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AccountRestServiceImplTest {

    @Test

    void testGetAccountByID(){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/account/8"))
                .header("apikey", "atm_api_sec_8d2f7a9e4b1c3d5f6a7e8b9c0d1e2f3a")
                .header("authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJldGhhbkBnbWFpbC5jb20iLCJuYW1lIjoiRXRoYW4iLCJhY2NvdW50X2lkIjo2LCJlbWFpbCI6ImV0aGFuQGdtYWlsLmNvbSIsIlJPTEUiOiJVU0VSIiwiaWF0IjoxNzgwNTgxODI2LCJleHAiOjE3ODA1ODU0MjZ9.OX6bbIhL29ab6flyeDgJ1ZIZonfU-RjiGxk2nMUGZgYk13S_M7704ssOClU_GAYrNM6xpArO4fHGINOQdzJ8iQ")
                .method("DELETE", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.body());
    }

    void testLoginAccount() {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9990/account/login"))
                .header("apikey", "zuTG5ioRPx75sOderkUMGuDnepg8WD4z6jKD4ClPktHUWDlT")
                .header("content-type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(
                        "{\n  \"email\": \"seintthu@gmail.com\",\n  \"password\": \"123456\"\n}"))
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.body());
    }

    void testRegisterAccount(){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/account/register"))
                .header("content-type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString("{\n  \"name\": \"Ethan\",\n  \"email\": \"ethan@gmail.com\",\n  \"password\": \"pass123\",\n  \"confirmPassword\": \"pass123\",\n  \"phoneNumber\": \"0998769321\",\n  \"address\": \"Mandalay\",\n  \"role\": \"USER\"\n}"))
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.body());
    }
    void testUpdateAccount(){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/account/6"))
                .header("apikey", "atm_api_sec_8d2f7a9e4b1c3d5f6a7e8b9c0d1e2f3a")
                .header("authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJldGhhbkBnbWFpbC5jb20iLCJuYW1lIjoiV2Vza2VyIiwiYWNjb3VudF9pZCI6NiwiZW1haWwiOiJldGhhbkBnbWFpbC5jb20iLCJST0xFIjoiVVNFUiIsImlhdCI6MTc4MDkwODgwOCwiZXhwIjoxNzgwOTEyNDA4fQ.LS2fJ1Ov_SqwNpRSCD7K7pP--IZ2n4cE6YFuYB9_CcdD_zy0COIj089RWoLBOIqo92qC6taj8MkojOJQkDfl5A")
                .header("content-type", "application/json")
                .method("PUT", HttpRequest.BodyPublishers.ofString("{\n  \"name\": \"Wesker\"\n}"))
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.body());
    }
    void testDeleteAccount(){}
}
