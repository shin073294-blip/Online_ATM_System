package com.talent.java.batch11.springbootapp.service;

import com.talent.java.batch11.springbootapp.model.Account;
import com.talent.java.batch11.springbootapp.model.Transaction;
import com.talent.java.batch11.springbootapp.dto.request.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface AccountService {
        // Tutor's Original Methods
        public Account login(LoginInfo loginInfo);
        public Account saveAccount(Account account);
        public Account findByEmail(String email);
        public Account findByPhoneNumber(String phoneNumber);
        public void updateBalanceById(int accountId, double newBalance);
        public List<Account> getAllAccounts();
        public List<Transaction> getAllTransactionsByAccountId(long accountId);
        public ResponseEntity getAccountById(long accountId);
        public ResponseEntity handleLoginRequest(LoginInfo loginInfo);

        // Your Other Feature Handlers (Using clean raw ResponseEntity)
        ResponseEntity handleRegisterRequest(RegisterInfo registerInfo);
        ResponseEntity handleDepositRequest(TransactionRequest request);
        ResponseEntity handleWithdrawRequest(TransactionRequest request);
        ResponseEntity handleTopUpRequest(TransactionRequest request);
        ResponseEntity handleTransferRequest(TransferInfo transferInfo);
        ResponseEntity handleGetHistoryRequest(Long accountId);
        ResponseEntity handleGetAccountDetailsRequest(Long accountId);
        ResponseEntity handleAdminGetAccountsRequest(Long adminId);
        ResponseEntity handleAdminGetTransactionsRequest(Long adminId);
        ResponseEntity handleGetDashboardRequest(Long accountId);
        ResponseEntity handleDeleteAccountRequest(Long id);
        ResponseEntity handleUpdateAccountRequest(Long id, Map<String, Object> updateInfo);

        // Utilities & Core Processing Methods (Fixes TransactionController & Missing Methods)
        Account findById(Long id);
        List<Transaction> getAllTransactions();
        void deleteById(Long id);

        void processDeposit(Long accountId, double amount);
        void processWithdraw(Long accountId, double amount);
        void processTopUp(Long accountId, double amount);
        void processTransfer(Long senderId, String recipientEmail, double amount);
}