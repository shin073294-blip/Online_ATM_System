package com.talent.java.batch11.springbootapp.controller;

import com.talent.java.batch11.springbootapp.model.Account;
import com.talent.java.batch11.springbootapp.request.TransferInfo;
import com.talent.java.batch11.springbootapp.service.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TransactionController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/deposit")
    public String deposit(@RequestParam("amount") double amount, HttpSession session) {
        Account loginAccount = (Account) session.getAttribute("accountInfo");
        if (loginAccount != null) {
            long accountId = ((Number) loginAccount.getId()).longValue();
            accountService.processDeposit(accountId, amount);
            session.setAttribute("accountInfo", accountService.findByEmail(loginAccount.getEmail()));
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam("amount") double amount, HttpSession session) {
        Account loginAccount = (Account) session.getAttribute("accountInfo");
        if (loginAccount != null) {
            long accountId = ((Number) loginAccount.getId()).longValue();
            accountService.processWithdraw(accountId, amount);
            session.setAttribute("accountInfo", accountService.findByEmail(loginAccount.getEmail()));
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/topup")
    public String topup(@RequestParam("amount") double amount, HttpSession session) {
        Account loginAccount = (Account) session.getAttribute("accountInfo");
        if (loginAccount != null) {
            long accountId = ((Number) loginAccount.getId()).longValue();
            accountService.processTopUp(accountId, amount);
            session.setAttribute("accountInfo", accountService.findByEmail(loginAccount.getEmail()));
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/transfer")
    public String transfer(@ModelAttribute("transferInfo") TransferInfo transferInfo, HttpSession session) {
        Account loginAccount = (Account) session.getAttribute("accountInfo");
        if (loginAccount != null && transferInfo != null) {
            long senderId = ((Number) loginAccount.getId()).longValue();
            accountService.processTransfer(senderId, transferInfo.getRecipientEmail(), transferInfo.getAmount());
            session.setAttribute("accountInfo", accountService.findByEmail(loginAccount.getEmail()));
        }
        return "redirect:/dashboard";
    }
}