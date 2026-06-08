package com.talent.java.batch11.springbootapp.controller.RestApi;

import com.talent.java.batch11.springbootapp.dto.request.LoginInfo;
import com.talent.java.batch11.springbootapp.dto.request.RegisterInfo;
import com.talent.java.batch11.springbootapp.service.AccountService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/account")
public class AccountRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity registerAccount(@Valid @RequestBody RegisterInfo registerInfo) {
        logger.info("Reach Register controller");
        return accountService.handleRegisterRequest(registerInfo);
    }

    @PostMapping("/login")
    public ResponseEntity loginAccount(@Valid @RequestBody LoginInfo loginInfo) {
        logger.info("Reach Login controller");
        return accountService.handleLoginRequest(loginInfo);
    }

    @GetMapping("/viewaccount")
    public ResponseEntity getAccountDetails(@RequestBody Map<String, Long> requestBody) {
        logger.info("Reach View Account Details controller");
        return accountService.handleGetAccountDetailsRequest(requestBody.get("id"));
    }

    @GetMapping("/{id}")
    public ResponseEntity getAccountByPathId(@PathVariable Long id) {
        logger.info("Reach Get Account By Path ID controller");
        return accountService.getAccountById(id);
    }

    @GetMapping("/admin/account")
    public ResponseEntity viewAllAccounts(@RequestBody Map<String, Long> requestBody) {
        logger.warn("Reach Admin View All Accounts controller");
        return accountService.handleAdminGetAccountsRequest(requestBody.get("id"));
    }

    @GetMapping("/dashboard/{id}")
    public ResponseEntity getDashboardData(@PathVariable Long id) {
        logger.info("Reach Get Dashboard Data controller");
        return accountService.handleGetDashboardRequest(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteAccount(@PathVariable Long id) {
        logger.warn("Reach Delete Account controller");
        return accountService.handleDeleteAccountRequest(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateAccount(@PathVariable Long id, @RequestBody Map<String, Object> updateInfo) {
        logger.info("Reach Update Account controller");
        return accountService.handleUpdateAccountRequest(id, updateInfo);
    }
}