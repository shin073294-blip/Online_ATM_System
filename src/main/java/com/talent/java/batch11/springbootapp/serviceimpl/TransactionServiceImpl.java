package com.talent.java.batch11.springbootapp.serviceimpl;

import com.talent.java.batch11.springbootapp.model.Transaction;
import com.talent.java.batch11.springbootapp.repository.TransactionRepository;
import com.talent.java.batch11.springbootapp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    @Transactional
    public void saveTransaction(Transaction transaction) {
        if (transaction != null) {
            transactionRepository.save(transaction);
        }
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}