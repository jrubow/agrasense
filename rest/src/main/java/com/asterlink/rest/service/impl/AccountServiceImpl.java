package com.asterlink.rest.service.impl;

import com.asterlink.rest.repository.AccountRepository;
import com.asterlink.rest.service.AccountService;
import org.springframework.stereotype.Service;

/**
 * Service implementation for Account.
 * Code for methods.
 * @author gl3bert
 */

@Service
public class AccountServiceImpl implements AccountService {

    // Set up repository access.
    private final AccountRepository accountRepository;
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
}
