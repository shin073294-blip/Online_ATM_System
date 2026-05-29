package com.talent.java.batch11.springbootapp.controller;

import com.talent.java.batch11.springbootapp.model.Account;
import com.talent.java.batch11.springbootapp.request.LoginInfo;
import com.talent.java.batch11.springbootapp.request.RegisterInfo;
import com.talent.java.batch11.springbootapp.request.TransferInfo;
import com.talent.java.batch11.springbootapp.service.AccountService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/")
    public String viewHomePage(Model model) {
        return "index";
    }

    @GetMapping("/register")
    public String register(Model model) {
        RegisterInfo registerInfo = new RegisterInfo();
        model.addAttribute("registerInfo", registerInfo);
        return "register";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @RequestMapping(value = "/registerAccount", method = RequestMethod.POST)
    public String registerAccount(@ModelAttribute("registerInfo") RegisterInfo registerInfo, HttpSession session, Model model) {

        if (!registerInfo.getPassword().equals(registerInfo.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match!");
            return "register";
        }

        if (accountService.findByEmail(registerInfo.getEmail()) != null) {
            model.addAttribute("error", "An account with this email already exists!");
            return "register";
        }

        Account account = new Account();
        BeanUtils.copyProperties(registerInfo, account, "id");
        account.setBalance(0.0);

        Account registeredAccount = accountService.saveAccount(account);
        session.setAttribute("accountInfo", registeredAccount);

        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login(Model model) {
        LoginInfo loginInfo = new LoginInfo();
        model.addAttribute("logininfo", loginInfo);
        return "login";
    }

    @PostMapping("/loginAccount")
    public String loginAccount(@ModelAttribute("logininfo") LoginInfo loginInfo, HttpSession session, Model model) {
        try {
            Account account = accountService.login(loginInfo);
            if (account != null) {
                session.setAttribute("accountInfo", account);
                return "redirect:/dashboard";
            }
            model.addAttribute("error", "Invalid email or password!");
            return "login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @PostMapping("/deposit")
    public String deposit(@RequestParam("amount") double amount, HttpSession session) {
        Account loginAccount = (Account) session.getAttribute("accountInfo");

        if (loginAccount != null && amount > 0) {
            double newBalance = loginAccount.getBalance() + amount;

            long accountId = ((Number) loginAccount.getId()).longValue();
            accountService.updateBalanceById((long) accountId, newBalance);

            loginAccount.setBalance(newBalance);
            session.setAttribute("accountInfo", loginAccount);
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam("amount") double amount, HttpSession session) {
        Account loginAccount = (Account) session.getAttribute("accountInfo");

        if (loginAccount != null && amount > 0 && loginAccount.getBalance() >= amount) {
            double newBalance = loginAccount.getBalance() - amount;

            long accountId = ((Number) loginAccount.getId()).longValue();
            accountService.updateBalanceById((long) accountId, newBalance);

            loginAccount.setBalance(newBalance);
            session.setAttribute("accountInfo", loginAccount);
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/topup")
    public String topup(@RequestParam("amount") double amount, HttpSession session) {
        Account loginAccount = (Account) session.getAttribute("accountInfo");

        if (loginAccount != null && amount > 0 && loginAccount.getBalance() >= amount) {
            double newBalance = loginAccount.getBalance() - amount;

            long accountId = ((Number) loginAccount.getId()).longValue();
            accountService.updateBalanceById((long) accountId, newBalance);

            loginAccount.setBalance(newBalance);
            session.setAttribute("accountInfo", loginAccount);
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/transfer")
    public String transfer(@ModelAttribute("transferInfo") TransferInfo transferInfo, HttpSession session) {
        Account loginAccount = (Account) session.getAttribute("accountInfo");

        if (loginAccount != null && transferInfo != null && transferInfo.getAmount() > 0) {
            if (loginAccount.getBalance() >= transferInfo.getAmount()) {

                Account recipient = accountService.findByEmail(transferInfo.getRecipientEmail());
                if (recipient != null) {
                    double newSenderBalance = loginAccount.getBalance() - transferInfo.getAmount();
                    double newRecipientBalance = recipient.getBalance() + transferInfo.getAmount();

                    long senderId = ((Number) loginAccount.getId()).longValue();
                    long recipientId = ((Number) recipient.getId()).longValue();

                    accountService.updateBalanceById((long) senderId, newSenderBalance);
                    accountService.updateBalanceById((long) recipientId, newRecipientBalance);

                    loginAccount.setBalance(newSenderBalance);
                    session.setAttribute("accountInfo", loginAccount);
                }
            }
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboardPage(Model model, HttpSession session) {
        Account loginAccount = (Account) session.getAttribute("accountInfo");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        long accountId = ((Number) loginAccount.getId()).longValue();

        model.addAttribute("currentAccount", loginAccount);
        model.addAttribute("allaccount", accountService.getAllAccounts());
        model.addAttribute("transactions", accountService.getAllTransactionsByAccountId(accountId));

        return "dashboard";
    }
}