package com.talent.java.batch11.springbootapp.service;

import com.talent.java.batch11.springbootapp.model.Account;
import com.talent.java.batch11.springbootapp.model.Transaction;
import com.talent.java.batch11.springbootapp.request.LoginInfo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AccountService {
        public Account login(LoginInfo loginInfo);
        public Account saveAccount(Account account);
        public Account findByEmail(String email);
        public Account findByPhoneNumber(String phoneNumber);
        void updateBalanceById(Long accountId, double newBalance);
        public List<Account> getAllAccounts();
        public String checkRole(int id);
        List<Transaction> getAllTransactionsByAccountId(Long accountId);

        // 🔑 NEW: Track and write transaction entries for Deposits using your TransactionService
        @Transactional
        void processDeposit(Long accountId, double amount);

        // 🔑 NEW: Track and write transaction entries for Withdrawals using your TransactionService
        @Transactional
        void processWithdraw(Long accountId, double amount);

        // 🔑 NEW: Track and write transaction entries for Mobile Top-ups using your TransactionService
        @Transactional
        void processTopUp(Long accountId, double amount);

        // 🔑 NEW: Track and write mutual transaction entries for Fund Transfers using your TransactionService
        @Transactional
        void processTransfer(Long senderId, String recipientEmail, double amount);

        List<Transaction> getAllTransactions();
}
