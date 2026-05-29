package com.talent.java.batch11.springbootapp.service;

import com.talent.java.batch11.springbootapp.model.Account;
import com.talent.java.batch11.springbootapp.model.Transaction;
import com.talent.java.batch11.springbootapp.request.LoginInfo;

import java.util.List;

public interface AccountService {
        public Account login(LoginInfo loginInfo);
        public Account saveAccount(Account account);
        public Account findByEmail(String email);
        public Account findByPhoneNumber(String phoneNumber);
        void updateBalanceById(Long accountId, double newBalance);
        public List<Account> getAllAccounts();
        List<Transaction> getAllTransactionsByAccountId(Long accountId);
    }
