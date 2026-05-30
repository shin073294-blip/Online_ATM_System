package com.talent.java.batch11.springbootapp.controller;

import com.talent.java.batch11.springbootapp.model.Account;
import com.talent.java.batch11.springbootapp.request.LoginInfo;
import com.talent.java.batch11.springbootapp.request.RegisterInfo;
import com.talent.java.batch11.springbootapp.service.AccountService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
        account.setRole(registerInfo.getRole());

        Account registeredAccount = accountService.saveAccount(account);
        session.setAttribute("accountInfo", registeredAccount);

        if ("ADMIN".equalsIgnoreCase(registeredAccount.getRole())) {
            model.addAttribute("adminName", registeredAccount.getName());
            model.addAttribute("allaccount", accountService.getAllAccounts());
            model.addAttribute("allTransactions", accountService.getAllTransactions());
            return "admindashboard";
        }

        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login(Model model) {
        LoginInfo loginInfo = new LoginInfo();
        model.addAttribute("logininfo", loginInfo);
        return "login";
    }

    @PostMapping("/login")
    public String loginAccount(@ModelAttribute("logininfo") LoginInfo loginInfo, HttpSession session, Model model) {
        try {
            Account account = accountService.login(loginInfo);

            if (account != null) {
                session.setAttribute("accountInfo", account);

                String role = account.getRole();

                if ("ADMIN".equalsIgnoreCase(role)) {
                    model.addAttribute("adminName", account.getName());
                    model.addAttribute("allaccount", accountService.getAllAccounts());
                    model.addAttribute("allTransactions", accountService.getAllTransactions());
                    return "admindashboard";
                } else {
                    return "redirect:/dashboard";
                }
            }

            model.addAttribute("error", "Invalid email or password!");
            return "login";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
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