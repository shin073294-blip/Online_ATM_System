package com.talent.java.batch11.springbootapp.repository;

import com.talent.java.batch11.springbootapp.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository  extends JpaRepository<Transaction,Integer> {
}
