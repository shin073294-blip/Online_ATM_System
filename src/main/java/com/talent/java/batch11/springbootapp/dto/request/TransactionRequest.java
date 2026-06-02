package com.talent.java.batch11.springbootapp.dto.request;
import lombok.Data;

@Data
public class TransactionRequest {
    private Long accountId;
    private double amount;
}
