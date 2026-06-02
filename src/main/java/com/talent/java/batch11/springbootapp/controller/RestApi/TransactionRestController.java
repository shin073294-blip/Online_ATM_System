package com.talent.java.batch11.springbootapp.controller.RestApi;

import com.talent.java.batch11.springbootapp.dto.request.TransactionRequest;
import com.talent.java.batch11.springbootapp.dto.request.TransferInfo;
import com.talent.java.batch11.springbootapp.dto.response.*;
import com.talent.java.batch11.springbootapp.model.Account;
import com.talent.java.batch11.springbootapp.model.Transaction;
import com.talent.java.batch11.springbootapp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class TransactionRestController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/deposit")
    public ResponseEntity<DepositResponse> deposit(@RequestBody TransactionRequest request) {
        accountService.processDeposit(request.getAccountId(), request.getAmount());
        Account updatedAccount = accountService.findById(request.getAccountId());

        List<Transaction> allTransactions = updatedAccount.getTransactions();
        List<Transaction> latestTransactionOnly = allTransactions.isEmpty() ? allTransactions :
                List.of(allTransactions.get(allTransactions.size() - 1));

        return ResponseEntity.ok(new DepositResponse((int) updatedAccount.getBalance(), latestTransactionOnly));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawResponse> withdraw(@RequestBody TransactionRequest request) {
        accountService.processWithdraw(request.getAccountId(), request.getAmount());
        Account updatedAccount = accountService.findById(request.getAccountId());


        List<Transaction> allTransactions = updatedAccount.getTransactions();
        List<Transaction> latestTransactionOnly = allTransactions.isEmpty() ? allTransactions :
                List.of(allTransactions.get(allTransactions.size() - 1));

        WithdrawResponse response = new WithdrawResponse();
        response.setBalance((int) updatedAccount.getBalance());
        response.setTransactions(latestTransactionOnly);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/topup")
    public ResponseEntity<TopUpResponse> topup(@RequestBody TransactionRequest request) {
        accountService.processTopUp(request.getAccountId(), request.getAmount());
        Account updatedAccount = accountService.findById(request.getAccountId());

        // Slice the history list so it only returns the single most recent record
        List<Transaction> allTransactions = updatedAccount.getTransactions();
        List<Transaction> latestTransactionOnly = allTransactions.isEmpty() ? allTransactions :
                List.of(allTransactions.get(allTransactions.size() - 1));

        TopUpResponse response = new TopUpResponse();
        response.setBalance((int) updatedAccount.getBalance());
        response.setTransactions(latestTransactionOnly);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(@RequestBody TransferInfo transferInfo) {
        accountService.processTransfer(
                transferInfo.getSenderId(),
                transferInfo.getRecipientEmail(),
                transferInfo.getAmount()
        );
        Account updatedAccount = accountService.findById(transferInfo.getSenderId());


        List<Transaction> allTransactions = updatedAccount.getTransactions();
        List<Transaction> latestTransactionOnly = allTransactions.isEmpty() ? allTransactions :
                List.of(allTransactions.get(allTransactions.size() - 1));

        TransferResponse response = new TransferResponse();
        response.setBalance((int) updatedAccount.getBalance());
        response.setTransactions(latestTransactionOnly);

        return ResponseEntity.ok(response);
    }
}