package com.talent.java.batch11.springbootapp.Account;
import com.talent.java.batch11.springbootapp.model.Account;
import com.talent.java.batch11.springbootapp.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountServiceImplTest {
    @Autowired
    private AccountService accountService;

    @Test
    @Rollback(false)
    public void testAddAndVerifyUser() {
        Account newAccount = new Account("mark", "mark@email.com", "password123", "123 Main St", "555-0199", 500.0);
        accountService.saveAccount(newAccount);
        assertNotNull(newAccount.getId(), "Account ID should not be null after saving!");
        System.out.println("Successfully added user with ID: " + newAccount.getId());
    }
}
