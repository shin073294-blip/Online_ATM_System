package com.talent.java.batch11.springbootapp.controller.RestApi;

import com.talent.java.batch11.springbootapp.dto.request.TransactionRequest;
import com.talent.java.batch11.springbootapp.dto.request.TransferInfo;
import com.talent.java.batch11.springbootapp.dto.response.*;
import com.talent.java.batch11.springbootapp.model.Account;
import com.talent.java.batch11.springbootapp.dto.request.LoginInfo;
import com.talent.java.batch11.springbootapp.dto.request.RegisterInfo;
import com.talent.java.batch11.springbootapp.model.Transaction;
import com.talent.java.batch11.springbootapp.service.AccountService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/account") // 🛠️ Fixed to match your original AuthServiceImpl path checking
public class AccountRestController {

    @Autowired
    private AccountService accountService;

    // REGISTER AN ACCOUNT -> Path is: /account/register
    @PostMapping("/register")
    public ResponseEntity<?> registerAccount(@RequestBody RegisterInfo registerInfo) {
        if (!registerInfo.getPassword().equals(registerInfo.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Error: Passwords do not match!");
        }

        if (accountService.findByEmail(registerInfo.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Error: An account with this email already exists!");
        }

        Account account = new Account();
        BeanUtils.copyProperties(registerInfo, account, "id");
        account.setBalance(0.0);
        account.setRole(registerInfo.getRole());

        Account registeredAccount = accountService.saveAccount(account);
        AccountResponse response = new AccountResponse();
        BeanUtils.copyProperties(registeredAccount, response);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginAccount(@RequestBody LoginInfo loginInfo) {
        try {
            return accountService.handleLoginRequest(loginInfo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<DepositResponse> deposit(@RequestBody TransactionRequest request) {
        accountService.processDeposit(request.getAccountId(), request.getAmount());
        Account updatedAccount = accountService.findById(request.getAccountId());

        List<Transaction> allTransactions = updatedAccount.getTransactions();
        List<Transaction> latestTransactionOnly = allTransactions.isEmpty() ? allTransactions :
                List.of(allTransactions.get(allTransactions.size() - 1));

        return ResponseEntity.ok(new DepositResponse((int) updatedAccount.getBalance(), latestTransactionOnly));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawResponse> withdraw(@RequestBody TransactionRequest request) {
        accountService.processWithdraw(request.getAccountId(), request.getAmount());
        Account updatedAccount = accountService.findById(request.getAccountId());

        List<Transaction> allTransactions = updatedAccount.getTransactions();
        List<Transaction> latestTransactionOnly = allTransactions.isEmpty() ? allTransactions :
                List.of(allTransactions.get(allTransactions.size() - 1));

        WithdrawResponse response = new WithdrawResponse();
        response.setBalance((int) updatedAccount.getBalance());
        response.setTransactions(latestTransactionOnly);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/topup")
    public ResponseEntity<TopUpResponse> topup(@RequestBody TransactionRequest request) {
        accountService.processTopUp(request.getAccountId(), request.getAmount());
        Account updatedAccount = accountService.findById(request.getAccountId());

        List<Transaction> allTransactions = updatedAccount.getTransactions();
        List<Transaction> latestTransactionOnly = allTransactions.isEmpty() ? allTransactions :
                List.of(allTransactions.get(allTransactions.size() - 1));

        TopUpResponse response = new TopUpResponse();
        response.setBalance((int) updatedAccount.getBalance());
        response.setTransactions(latestTransactionOnly);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getTransactionHistory(@RequestBody Map<String, Long> requestBody) {
        Long id = requestBody.get("id");
        Account account = accountService.findById(id);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        // 🛠️ Return a clean copy list to prevent recursive parsing crashes
        return ResponseEntity.ok(accountService.getAllTransactionsByAccountId(id));
    }

    @GetMapping("/viewaccount")
    public ResponseEntity<?> getAccountDetails(@RequestBody Map<String, Long> requestBody){
        Long id = requestBody.get("id");
        Account account = accountService.findById(id);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Account not found!");
        }

        // 🛠️ FIX: Convert raw Account entity to flat AccountResponse DTO to block JSON infinite recursion
        AccountResponse response = new AccountResponse();
        BeanUtils.copyProperties(account, response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountByPathId(@PathVariable Long id) {
        Account account = accountService.findById(id);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Account not found!");
        }
        AccountResponse response = new AccountResponse();
        BeanUtils.copyProperties(account, response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/account")
    public ResponseEntity<?> viewAllAccounts(@RequestBody Map<String, Long> requestBody) {
        Long id = requestBody.get("id");
        String role = accountService.checkRole(id.intValue());
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: You do not have permission to view the account table.");
        }

        // 🛠️ FIX: Map list entries cleanly into DTO wrappers so admin queries don't trigger loops
        List<Account> allAccounts = accountService.getAllAccounts();
        List<AccountResponse> standardResponses = new ArrayList<>();
        for (Account acc : allAccounts) {
            AccountResponse res = new AccountResponse();
            BeanUtils.copyProperties(acc, res);
            standardResponses.add(res);
        }
        return ResponseEntity.ok(standardResponses);
    }

    @GetMapping("/admin/transaction")
    public ResponseEntity<?> viewAllTransactions(@RequestBody Map<String, Long> requestBody){
        Long id = requestBody.get("id");
        String role = accountService.checkRole(id.intValue());
        if (!"ADMIN".equalsIgnoreCase(role)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: You do not have permission to view the account table.");
        }
        List<Transaction> allTransactions = accountService.getAllTransactions();
        return ResponseEntity.ok(allTransactions);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(@RequestBody TransferInfo transferInfo) {
        accountService.processTransfer(
                transferInfo.getSenderId(),
                transferInfo.getRecipientEmail(),
                transferInfo.getAmount()
        );
        Account updatedAccount = accountService.findById(transferInfo.getSenderId());

        List<Transaction> allTransactions = updatedAccount.getTransactions();
        List<Transaction> latestTransactionOnly = allTransactions.isEmpty() ? allTransactions :
                List.of(allTransactions.get(allTransactions.size() - 1));

        TransferResponse response = new TransferResponse();
        response.setBalance((int) updatedAccount.getBalance());
        response.setTransactions(latestTransactionOnly);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard/{id}")
    public ResponseEntity<?> getDashboardData(@PathVariable int id) {
        Account account = accountService.getAccountById((long) id);

        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Account not found!");
        }

        Map<String, Object> dashboardData = new HashMap<>();

        AccountResponse accountProfile = new AccountResponse();
        BeanUtils.copyProperties(account, accountProfile);


        List<Account> allAccounts = accountService.getAllAccounts();
        List<AccountResponse> safeAccountList = new ArrayList<>();
        for(Account a : allAccounts) {
            AccountResponse res = new AccountResponse();
            BeanUtils.copyProperties(a, res);
            safeAccountList.add(res);
        }

        dashboardData.put("profile", accountProfile);
        dashboardData.put("allAccountsList", safeAccountList);
        dashboardData.put("transactionHistory", accountService.getAllTransactionsByAccountId((long) id));

        return ResponseEntity.ok(dashboardData);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
        Account account = accountService.findById(id);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Account not found!");
        }
        accountService.deleteById(id);
        return ResponseEntity.ok("Account with ID " + id + " has been successfully deleted.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable Long id, @RequestBody Map<String, Object> updateInfo) {
        Account existingAccount = accountService.findById(id);
        if (existingAccount == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Account not found!");
        }

        if (updateInfo.containsKey("name")) {
            existingAccount.setName((String) updateInfo.get("name"));
        }
        if (updateInfo.containsKey("email")) {
            existingAccount.setEmail((String) updateInfo.get("email"));
        }
        if (updateInfo.containsKey("address")) {
            existingAccount.setAddress((String) updateInfo.get("address"));
        }
        if (updateInfo.containsKey("phoneNumber")) {
            existingAccount.setPhoneNumber((String) updateInfo.get("phoneNumber"));
        }
        if (updateInfo.containsKey("role")) {
            existingAccount.setRole((String) updateInfo.get("role"));
        }

        Account savedAccount = accountService.saveAccount(existingAccount);

        AccountResponse response = new AccountResponse();
        BeanUtils.copyProperties(savedAccount, response);

        return ResponseEntity.ok(response);
    }

}