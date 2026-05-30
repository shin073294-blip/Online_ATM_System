package com.talent.java.batch11.springbootapp.serviceimpl;

import com.talent.java.batch11.springbootapp.model.Account;
import com.talent.java.batch11.springbootapp.model.Transaction;
import com.talent.java.batch11.springbootapp.model.enumType.TransactionType;
import com.talent.java.batch11.springbootapp.repository.AccountRepository;
import com.talent.java.batch11.springbootapp.repository.TransactionRepository;
import com.talent.java.batch11.springbootapp.request.LoginInfo;
import com.talent.java.batch11.springbootapp.service.AccountService;
import com.talent.java.batch11.springbootapp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // 🔑 Injecting your specialized transaction component cleanly
    @Autowired
    private TransactionService transactionService;

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
    public String checkRole(int id) {
        // 🔑 Cast the int parameter to a Long to match your updated repository layout
        Optional<Account> accountOpt = accountRepository.findById((long) id);
        if (accountOpt.isPresent()) {
            // 🔑 Fixed: Changed .getrole() to .getRole() with a capital 'R' to match Lombok's camelCase generation
            String role = accountOpt.get().getRole();
            return role != null ? role : "USER";
        }
        return "USER";
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // 🔑 NEW: Track and write transaction entries for Deposits using your TransactionService
    @Transactional
    @Override
    public void processDeposit(Long accountId, double amount) {
        Account account = accountRepository.findAccountById(accountId);
        if (account != null && amount > 0) {
            double previousBalance = account.getBalance();
            double newBalance = previousBalance + amount;
            account.setBalance(newBalance);
            accountRepository.save(account);

            Transaction tx = new Transaction(TransactionType.DEPOSIT, amount, previousBalance);
            tx.setAccount(account);
            transactionService.saveTransaction(tx);
        }
    }

    // 🔑 NEW: Track and write transaction entries for Withdrawals using your TransactionService
    @Transactional
    @Override
    public void processWithdraw(Long accountId, double amount) {
        Account account = accountRepository.findAccountById(accountId);
        if (account != null && amount > 0 && account.getBalance() >= amount) {
            double previousBalance = account.getBalance();
            double newBalance = previousBalance - amount;
            account.setBalance(newBalance);
            accountRepository.save(account);

            Transaction tx = new Transaction(TransactionType.WITHDRAW, amount, previousBalance);
            tx.setAccount(account);
            transactionService.saveTransaction(tx);
        }
    }

    // 🔑 NEW: Track and write transaction entries for Mobile Top-ups using your TransactionService
    @Transactional
    @Override
    public void processTopUp(Long accountId, double amount) {
        Account account = accountRepository.findAccountById(accountId);
        if (account != null && amount > 0 && account.getBalance() >= amount) {
            double previousBalance = account.getBalance();
            double newBalance = previousBalance - amount;
            account.setBalance(newBalance);
            accountRepository.save(account);

            Transaction tx = new Transaction(TransactionType.TOPUP, amount, previousBalance);
            tx.setAccount(account);
            transactionService.saveTransaction(tx);
        }
    }

    // 🔑 NEW: Track and write mutual transaction entries for Fund Transfers using your TransactionService
    @Transactional
    @Override
    public void processTransfer(Long senderId, String recipientEmail, double amount) {
        Account sender = accountRepository.findAccountById(senderId);
        Account recipient = accountRepository.findAccountByEmail(recipientEmail);

        if (sender != null && recipient != null && amount > 0 && sender.getBalance() >= amount) {
            double previousSenderBalance = sender.getBalance();
            sender.setBalance(previousSenderBalance - amount);
            accountRepository.save(sender);

            Transaction senderTx = new Transaction(TransactionType.TRANSFER, amount, previousSenderBalance);
            senderTx.setAccount(sender);
            transactionService.saveTransaction(senderTx);

            double previousRecipientBalance = recipient.getBalance();
            recipient.setBalance(previousRecipientBalance + amount);
            accountRepository.save(recipient);

            Transaction recipientTx = new Transaction(TransactionType.DEPOSIT, amount, previousRecipientBalance);
            recipientTx.setAccount(recipient);
            transactionService.saveTransaction(recipientTx);
        }
    }


    @Override
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }
}