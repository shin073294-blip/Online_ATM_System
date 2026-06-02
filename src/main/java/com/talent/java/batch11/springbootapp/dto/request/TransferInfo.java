package com.talent.java.batch11.springbootapp.dto.request;
import lombok.Data;

@Data
public class TransferInfo {
    private Long senderId; //To handle the stateless Rest calls
    private String recipientEmail;
    private double amount;
}
