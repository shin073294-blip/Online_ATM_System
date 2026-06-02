package com.talent.java.batch11.springbootapp.controller.RestApi;

import com.talent.java.batch11.springbootapp.model.Account;
import com.talent.java.batch11.springbootapp.dto.request.LoginInfo;
import com.talent.java.batch11.springbootapp.dto.request.RegisterInfo;
import com.talent.java.batch11.springbootapp.dto.response.AccountResponse;
import com.talent.java.batch11.springbootapp.dto.response.LoginResponse;
import com.talent.java.batch11.springbootapp.service.AccountService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/account") // Keeps API endpoints distinct from HTML web paths
public class AccountRestController {

    @Autowired
    private AccountService accountService;

    // 1. REGISTER AN ACCOUNT (via Bruno)
    @PostMapping("/register")
    public ResponseEntity<?> registerAccount(@RequestBody RegisterInfo registerInfo) {

        if (!registerInfo.getPassword().equals(registerInfo.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Error: Passwords do not match!");
        }

        if (accountService.findByEmail(registerInfo.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Error: An account with this email already exists!");
        }

        // Convert DTO to DB Entity layout
        Account account = new Account();
        BeanUtils.copyProperties(registerInfo, account, "id");
        account.setBalance(0.0);
        account.setRole(registerInfo.getRole());

        Account registeredAccount = accountService.saveAccount(account);

        // Convert to secure Response DTO so we don't leak the password field back to Bruno
        AccountResponse response = new AccountResponse();
        BeanUtils.copyProperties(registeredAccount, response);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. LOGIN (via Bruno)
    @PostMapping("/login")
    public ResponseEntity<?> loginAccount(@RequestBody LoginInfo loginInfo) {
        try {
            Account account = accountService.login(loginInfo);

            if (account != null) {
                // Return clear status messages & access roles using your Response DTO
                LoginResponse loginResponse = new LoginResponse(
                        "Login successful!",
                        account.getId(), // Safe extraction of int id from Account entity
                        account.getName(),
                        account.getRole()
                );
                return ResponseEntity.ok(loginResponse);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Invalid email or password!");

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 3. GET DASHBOARD DATA FOR A SPECIFIC USER (via Bruno)
    @GetMapping("/dashboard/{id}")
    public ResponseEntity<?> getDashboardData(@PathVariable int id) {
        // Safe conversion of primitive int to Long object signature expected by AccountService
        Account account = accountService.getAccountById((long) id);

        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Account not found!");
        }

        // Package up data payloads neatly for Bruno to display
        Map<String, Object> dashboardData = new HashMap<>();

        AccountResponse accountProfile = new AccountResponse();
        BeanUtils.copyProperties(account, accountProfile);

        dashboardData.put("profile", accountProfile);
        dashboardData.put("allAccountsList", accountService.getAllAccounts());

        // Pass ID explicitly matching your service's defined parameter signatures
        dashboardData.put("transactionHistory", accountService.getAllTransactionsByAccountId((long) id));

        return ResponseEntity.ok(dashboardData);
    }
}