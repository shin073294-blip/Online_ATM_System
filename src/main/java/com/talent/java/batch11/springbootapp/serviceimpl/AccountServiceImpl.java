package com.talent.java.batch11.springbootapp.serviceimpl;

import com.talent.java.batch11.springbootapp.model.Account;
import com.talent.java.batch11.springbootapp.model.Transaction;
import com.talent.java.batch11.springbootapp.repository.AccountRepository;
import com.talent.java.batch11.springbootapp.repository.TransactionRepository;
import com.talent.java.batch11.springbootapp.request.LoginInfo;
import com.talent.java.batch11.springbootapp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Account login(LoginInfo loginInfo) {
        Account account = accountRepository.findAccountByEmail(loginInfo.getEmail());
        if (account == null || !account.getPassword().equals(loginInfo.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        return account;
    }

    @Override
    @Transactional
    public Account saveAccount(Account account) {
        try {
            System.out.println("Saving Account " + account);
            return accountRepository.save(account);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Account findByEmail(String email) {
        return accountRepository.findAccountByEmail(email);
    }

    @Override
    public Account findByPhoneNumber(String phoneNumber) {
        return accountRepository.findAccountByPhoneNumber(phoneNumber);
    }

    @Override
    @Transactional
    public void updateBalanceById(Long accountId, double newBalance) {
        Account account = accountRepository.findAccountById(accountId);
        if (account != null) {
            account.setBalance(newBalance);
            accountRepository.save(account);
        } else {
            throw new RuntimeException("Account not found with id: " + accountId);
        }
    }

    @Override
    public List<Transaction> getAllTransactionsByAccountId(Long accountId) {
        Account account = accountRepository.findAccountById(accountId);
        return account != null ? account.getTransactions() : null;
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
}