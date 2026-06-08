package com.talent.java.batch11.springbootapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.talent.java.batch11.springbootapp.model.enumType.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String createdDate;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private double amount;
    private double previousAmount;

    @JsonIgnore// To make ghost loop disappear
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;


    public Transaction(TransactionType transactionType, double amount, double previousAmount) {
        this.createdDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.transactionType = transactionType;
        this.amount = amount;
        this.previousAmount = previousAmount;
    }

    public Transaction(TransactionType transactionType, double amount, double prev, Account account) {
    }
}

