package com.talent.java.batch11.springbootapp;

import com.talent.java.batch11.springbootapp.model.Account;
import com.talent.java.batch11.springbootapp.model.Transaction;
import com.talent.java.batch11.springbootapp.model.enumType.TransactionType; // Ensure this matches your package layout
import com.talent.java.batch11.springbootapp.service.AccountService;
import com.talent.java.batch11.springbootapp.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TransactionServiceImplTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    @Test
    @Rollback(false) // Prevents Spring from rolling back the transaction after the test finishes!
    public void testAddAndVerifyTransaction() {

        Account testAccount = new Account("Mark", "mark.test@email.com", "pass123", "123 Main St", "555-0199", 1500.0);
        accountService.saveAccount(testAccount);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

// Check if your constructor order looks like this:
        Transaction newTransaction = new Transaction();

        transactionService.saveTransaction(newTransaction);

        assertNotNull(newTransaction.getId(), "Transaction ID should not be null after saving!");

        System.out.println("Successfully added transaction with ID: " + newTransaction.getId());
    }
}