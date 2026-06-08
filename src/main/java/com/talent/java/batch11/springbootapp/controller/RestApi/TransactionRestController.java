package com.talent.java.batch11.springbootapp.controller.RestApi;

import com.talent.java.batch11.springbootapp.dto.request.TransactionRequest;
import com.talent.java.batch11.springbootapp.dto.request.TransferInfo;
import com.talent.java.batch11.springbootapp.service.AccountService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/transaction")
public class TransactionRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AccountService accountService;

    @PostMapping("/deposit")
    public ResponseEntity deposit(@Valid @RequestBody TransactionRequest request) {
        logger.info("Reach Deposit controller");
        return accountService.handleDepositRequest(request);
    }

    @PostMapping("/withdraw")
    public ResponseEntity withdraw(@Valid @RequestBody TransactionRequest request) {
        logger.info("Reach Withdraw controller");
        return accountService.handleWithdrawRequest(request);
    }

    @PostMapping("/topup")
    public ResponseEntity topup(@Valid @RequestBody TransactionRequest request) {
        logger.info("Reach Topup controller");
        return accountService.handleTopUpRequest(request);
    }

    @PostMapping("/transfer")
    public ResponseEntity transfer(@Valid @RequestBody TransferInfo transferInfo) {
        logger.info("Reach Transfer controller");
        return accountService.handleTransferRequest(transferInfo);
    }

    @GetMapping("/history")
    public ResponseEntity getTransactionHistory(@RequestBody Map<String, Long> requestBody) {
        logger.info("Reach Get Transaction History controller");
        return accountService.handleGetHistoryRequest(requestBody.get("id"));
    }

    @GetMapping("/admin/transaction")
    public ResponseEntity viewAllTransactions(@RequestBody Map<String, Long> requestBody) {
        logger.warn("Reach Admin View All Transactions controller");
        return accountService.handleAdminGetTransactionsRequest(requestBody.get("id"));
    }
}