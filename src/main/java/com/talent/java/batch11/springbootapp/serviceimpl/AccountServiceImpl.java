package com.talent.java.batch11.springbootapp.serviceimpl;

import com.talent.java.batch11.springbootapp.dto.request.*;
import com.talent.java.batch11.springbootapp.dto.response.*;
import com.talent.java.batch11.springbootapp.model.Account;
import com.talent.java.batch11.springbootapp.model.Transaction;
import com.talent.java.batch11.springbootapp.model.enumType.TransactionType;
import com.talent.java.batch11.springbootapp.repository.AccountRepository;
import com.talent.java.batch11.springbootapp.service.AccountService;
import com.talent.java.batch11.springbootapp.service.TokenService;
import com.talent.java.batch11.springbootapp.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AccountRepository accountRepository;
    private final TokenService tokenService;
    private final TransactionService transactionService;

    @Override
    public ResponseEntity handleRegisterRequest(RegisterInfo registerInfo) {
        logger.info("Service Layer - Processing registration request");
        if (!registerInfo.getPassword().equals(registerInfo.getConfirmPassword())) {
            logger.error("Service Layer - Registration failed: passwords do not match");
            return ResponseEntity.badRequest().body("Error: Passwords do not match!");
        }
        if (accountRepository.findAccountByEmail(registerInfo.getEmail()) != null) {
            logger.error("Service Layer - Registration failed: email already exists");
            return ResponseEntity.badRequest().body("Error: An account with this email already exists!");
        }

        Account account = new Account();
        BeanUtils.copyProperties(registerInfo, account, "id");
        account.setBalance(0.0);
        account.setRole(registerInfo.getRole());

        Account registeredAccount = accountRepository.save(account);
        logger.info("Service Layer - Registration successful");

        AccountResponse response = new AccountResponse();
        BeanUtils.copyProperties(registeredAccount, response);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity handleLoginRequest(LoginInfo loginInfo) {
        logger.info("Service Layer - Processing login request");
        Account account = accountRepository.findAccountByEmail(loginInfo.getEmail());
        if (account == null || !account.getPassword().equals(loginInfo.getPassword())) {
            logger.warn("Service Layer - Login failed: invalid credentials");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid email or password");
        }

        String accessToken = tokenService.generateAccessToken(account);
        String refreshToken = tokenService.generateRefreshToken(account);

        HttpHeaders headers = new HttpHeaders();
        headers.add("accessToken", accessToken);
        headers.add("refreshToken", refreshToken);

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Login successful!");
        body.put("id", account.getId());
        body.put("name", account.getName());
        body.put("role", account.getRole());

        logger.info("Service Layer - Login successful, tokens generated");
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity handleDepositRequest(TransactionRequest request) {
        logger.info("Service Layer - Processing deposit request");
        processDeposit(request.getAccountId(), request.getAmount());

        Account updatedAccount = accountRepository.findAccountById(request.getAccountId());
        List<Transaction> allTransactions = updatedAccount.getTransactions();
        List<Transaction> latestTx = allTransactions.isEmpty() ? allTransactions : List.of(allTransactions.get(allTransactions.size() - 1));

        DepositResponse response = new DepositResponse((int) updatedAccount.getBalance(), latestTx);
        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional
    public ResponseEntity handleWithdrawRequest(TransactionRequest request) {
        logger.info("Service Layer - Processing withdraw request");
        processWithdraw(request.getAccountId(), request.getAmount());

        Account updatedAccount = accountRepository.findAccountById(request.getAccountId());
        List<Transaction> allTransactions = updatedAccount.getTransactions();
        List<Transaction> latestTx = allTransactions.isEmpty() ? allTransactions : List.of(allTransactions.get(allTransactions.size() - 1));

        WithdrawResponse response = new WithdrawResponse();
        response.setBalance((int) updatedAccount.getBalance());
        response.setTransactions(latestTx);

        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional
    public ResponseEntity handleTopUpRequest(TransactionRequest request) {
        logger.info("Service Layer - Processing top-up request");
        processTopUp(request.getAccountId(), request.getAmount());

        Account updatedAccount = accountRepository.findAccountById(request.getAccountId());
        List<Transaction> allTransactions = updatedAccount.getTransactions();
        List<Transaction> latestTx = allTransactions.isEmpty() ? allTransactions : List.of(allTransactions.get(allTransactions.size() - 1));

        TopUpResponse response = new TopUpResponse();
        response.setBalance((int) updatedAccount.getBalance());
        response.setTransactions(latestTx);

        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional
    public ResponseEntity handleTransferRequest(TransferInfo transferInfo) {
        logger.info("Service Layer - Processing transfer request");
        processTransfer(transferInfo.getSenderId(), transferInfo.getRecipientEmail(), transferInfo.getAmount());

        Account updatedAccount = accountRepository.findAccountById(transferInfo.getSenderId());
        List<Transaction> allTransactions = updatedAccount.getTransactions();
        List<Transaction> latestTx = allTransactions.isEmpty() ? allTransactions : List.of(allTransactions.get(allTransactions.size() - 1));

        TransferResponse response = new TransferResponse();
        response.setBalance((int) updatedAccount.getBalance());
        response.setTransactions(latestTx);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity handleGetHistoryRequest(Long accountId) {
        logger.info("Service Layer - Fetching transaction history");
        Account account = accountRepository.findAccountById(accountId);
        if (account == null) {
            logger.error("Service Layer - History failed: account not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Account not found!");
        }
        return ResponseEntity.ok(account.getTransactions());
    }

    @Override
    public ResponseEntity handleGetAccountDetailsRequest(Long accountId) {
        logger.info("Service Layer - Fetching account details");
        Account account = accountRepository.findAccountById(accountId);
        if (account == null) {
            logger.error("Service Layer - Details failed: account not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Account not found!");
        }
        AccountResponse response = new AccountResponse();
        BeanUtils.copyProperties(account, response);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity handleAdminGetAccountsRequest(Long adminId) {
        logger.info("Service Layer - Admin fetching all accounts");
        Account admin = accountRepository.findAccountById(adminId);
        if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
            logger.error("Service Layer - Admin action rejected: access denied");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: You do not have permission to view the account table.");
        }

        List<Account> allAccounts = accountRepository.findAll();
        List<AccountResponse> responses = new ArrayList<>();
        for (Account acc : allAccounts) {
            AccountResponse res = new AccountResponse();
            BeanUtils.copyProperties(acc, res);
            responses.add(res);
        }
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity handleAdminGetTransactionsRequest(Long adminId) {
        logger.info("Service Layer - Admin fetching all transactions");
        Account admin = accountRepository.findAccountById(adminId);
        if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
            logger.error("Service Layer - Admin action rejected: access denied");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: You do not have permission to view the transaction table.");
        }
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @Override
    public ResponseEntity handleGetDashboardRequest(Long accountId) {
        logger.info("Service Layer - Fetching dashboard data");
        Account account = accountRepository.findAccountById(accountId);
        if (account == null) {
            logger.error("Service Layer - Dashboard failed: account not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Account not found!");
        }

        Map<String, Object> dashboardData = new HashMap<>();
        AccountResponse accountProfile = new AccountResponse();
        BeanUtils.copyProperties(account, accountProfile);

        List<Account> allAccounts = accountRepository.findAll();
        List<AccountResponse> safeAccountList = new ArrayList<>();
        for (Account a : allAccounts) {
            AccountResponse res = new AccountResponse();
            BeanUtils.copyProperties(a, res);
            safeAccountList.add(res);
        }

        dashboardData.put("profile", accountProfile);
        dashboardData.put("allAccountsList", safeAccountList);
        dashboardData.put("transactionHistory", account.getTransactions());

        return ResponseEntity.ok(dashboardData);
    }

    @Override
    @Transactional
    public ResponseEntity handleDeleteAccountRequest(Long id) {
        logger.info("Service Layer - Processing account deletion");
        if (!accountRepository.existsById(id)) {
            logger.error("Service Layer - Deletion failed: account not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Account not found!");
        }
        accountRepository.deleteById(id);
        logger.warn("Service Layer - Account deleted successfully");
        return ResponseEntity.ok("Account with ID " + id + " has been successfully deleted.");
    }

    @Override
    @Transactional
    public ResponseEntity handleUpdateAccountRequest(Long id, Map<String, Object> updateInfo) {
        logger.info("Service Layer - Processing account update");
        Account existingAccount = accountRepository.findAccountById(id);
        if (existingAccount == null) {
            logger.error("Service Layer - Update failed: account not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Account not found!");
        }

        if (updateInfo.containsKey("name")) existingAccount.setName((String) updateInfo.get("name"));
        if (updateInfo.containsKey("email")) existingAccount.setEmail((String) updateInfo.get("email"));
        if (updateInfo.containsKey("address")) existingAccount.setAddress((String) updateInfo.get("address"));
        if (updateInfo.containsKey("phoneNumber")) existingAccount.setPhoneNumber((String) updateInfo.get("phoneNumber"));
        if (updateInfo.containsKey("role")) existingAccount.setRole((String) updateInfo.get("role"));

        Account savedAccount = accountRepository.save(existingAccount);
        AccountResponse response = new AccountResponse();
        BeanUtils.copyProperties(savedAccount, response);

        logger.info("Service Layer - Update successful");
        return ResponseEntity.ok(response);
    }

    // --- Core Legacy Implementations & Utility Methods ---
    @Override public Account login(LoginInfo loginInfo) { return accountRepository.findAccountByEmail(loginInfo.getEmail()); }
    @Override public Account saveAccount(Account account) { return accountRepository.save(account); }
    @Override public Account findByEmail(String email) { return accountRepository.findAccountByEmail(email); }
    @Override public Account findByPhoneNumber(String phoneNumber) { return accountRepository.findAccountByPhoneNumber(phoneNumber); }
    @Override public List<Account> getAllAccounts() { return accountRepository.findAll(); }
    @Override public List<Transaction> getAllTransactions() { return transactionService.getAllTransactions(); }
    @Override public List<Transaction> getAllTransactionsByAccountId(long accountId) { return accountRepository.findAccountById(accountId).getTransactions(); }

    @Override public Account findById(Long id) { return accountRepository.findAccountById(id); }
    @Override public void deleteById(Long id) { accountRepository.deleteById(id); }

    @Override
    public ResponseEntity getAccountById(long accountId) {
        if (accountId == 0) return ResponseEntity.noContent().build();
        Account account = accountRepository.findAccountById(accountId);
        if (account == null) return ResponseEntity.notFound().build();
        account.setTransactions(null);
        return ResponseEntity.ok(account);
    }

    @Override
    @Transactional
    public void updateBalanceById(int accountId, double newBalance) {
        Account account = accountRepository.findById((long) accountId).orElseThrow(() -> new RuntimeException("Account not found"));
        account.setBalance(newBalance);
        accountRepository.save(account);
    }

    // --- Core Transaction Processors ---
    @Override
    @Transactional
    public void processDeposit(Long accountId, double amount) {
        logger.info("Database Ledger - Executing deposit process");
        Account account = accountRepository.findAccountById(accountId);
        if (account == null) return;
        if (amount <= 0) return;
        double prev = account.getBalance();
        account.setBalance(prev + amount);
        accountRepository.save(account);
        transactionService.saveTransaction(new Transaction(TransactionType.DEPOSIT, amount, prev, account));
    }

    @Override
    @Transactional
    public void processWithdraw(Long accountId, double amount) {
        logger.info("Database Ledger - Executing withdraw process");
        Account account = accountRepository.findAccountById(accountId);
        if (account == null) return;
        if (amount <= 0 || account.getBalance() < amount) return;
        double prev = account.getBalance();
        account.setBalance(prev - amount);
        accountRepository.save(account);
        transactionService.saveTransaction(new Transaction(TransactionType.WITHDRAW, amount, prev, account));
    }

    @Override
    @Transactional
    public void processTopUp(Long accountId, double amount) {
        logger.info("Database Ledger - Executing top-up process");
        Account account = accountRepository.findAccountById(accountId);
        if (account == null) return;
        if (amount <= 0 || account.getBalance() < amount) return;
        double prev = account.getBalance();
        account.setBalance(prev - amount);
        accountRepository.save(account);
        transactionService.saveTransaction(new Transaction(TransactionType.TOPUP, amount, prev, account));
    }

    @Override
    @Transactional
    public void processTransfer(Long senderId, String recipientEmail, double amount) {
        logger.info("Database Ledger - Executing transfer process");
        Account sender = accountRepository.findAccountById(senderId);
        Account recipient = accountRepository.findAccountByEmail(recipientEmail);
        if (sender == null || recipient == null) return;
        if (amount <= 0 || sender.getBalance() < amount) return;

        double prevSender = sender.getBalance();
        sender.setBalance(prevSender - amount);
        accountRepository.save(sender);
        transactionService.saveTransaction(new Transaction(TransactionType.TRANSFER, amount, prevSender, sender));

        double prevRecipient = recipient.getBalance();
        recipient.setBalance(prevRecipient + amount);
        accountRepository.save(recipient);
        transactionService.saveTransaction(new Transaction(TransactionType.DEPOSIT, amount, prevRecipient, recipient));
    }
}