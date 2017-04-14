package com.wojciechkocik.obd.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Wojciech Kocik
 * @since 15.04.2017
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public String generateId() {
        String id = accountRepository.store(new Account());
        return id;
    }
}
