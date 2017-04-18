package com.wojciechkocik.obd.init;

import com.wojciechkocik.obd.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Wojciech Kocik
 * @since 15.04.2017
 */
@RestController("/account")
public class InitController {

    private final AccountService accountService;

    @Autowired
    public InitController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public InitResponse generateId() {
        InitResponse initResponse = new InitResponse();
        initResponse.setAccountId(accountService.generateId());
        return initResponse;
    }
}
