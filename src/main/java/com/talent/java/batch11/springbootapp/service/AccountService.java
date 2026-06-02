package com.talent.java.batch11.springbootapp.service;

import com.talent.java.batch11.springbootapp.model.Account;
import com.talent.java.batch11.springbootapp.model.Transaction;
import com.talent.java.batch11.springbootapp.dto.request.LoginInfo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AccountService {
        public Account login(LoginInfo loginInfo);
        public Account saveAccount(Account account);
        public Account findByEmail(String email);
        Account getAccountById(Long id);
        Account findById(Long id);
        public Account findByPhoneNumber(String phoneNumber);
        void updateBalanceById(Long accountId, double newBalance);
        public List<Account> getAllAccounts();
        public String checkRole(int id);
        List<Transaction> getAllTransactionsByAccountId(Long accountId);

        @Transactional
        void processDeposit(Long accountId, double amount);

        @Transactional
        void processWithdraw(Long accountId, double amount);

        @Transactional
        void processTopUp(Long accountId, double amount);

        @Transactional
        void processTransfer(Long senderId, String recipientEmail, double amount);

        List<Transaction> getAllTransactions();
}
