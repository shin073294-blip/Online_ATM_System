package com.talent.java.batch11.springbootapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TransferInfo {
    @NotNull(message = "Sender ID cannot be null")
    private Long senderId;

    @NotBlank(message = "Recipient email cannot be blank")
    private String recipientEmail;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be greater than zero")
    private Double amount;

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}