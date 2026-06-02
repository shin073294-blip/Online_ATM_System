package com.talent.java.batch11.springbootapp.dto.response;

import com.talent.java.batch11.springbootapp.model.Transaction;
import lombok.Data;
import java.util.List;

@Data
public class TopUpResponse {
    private int balance;
    private List<Transaction> transactions;
}