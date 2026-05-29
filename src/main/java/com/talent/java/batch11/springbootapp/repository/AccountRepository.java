package com.talent.java.batch11.springbootapp.repository;

import com.talent.java.batch11.springbootapp.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account,Integer> {
    Account findAccountById(Long id);
    Account findAccountByEmail(String email);
    Account findAccountByPhoneNumber(String phoneNumber);
    Account findAccountByAddress( String address);
}
