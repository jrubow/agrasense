package com.asterlink.rest.service.impl;

import com.asterlink.rest.model.Account;
import com.asterlink.rest.repository.AccountRepository;
import com.asterlink.rest.service.AccountService;
import org.springframework.security.crypto.bcrypt.BCrypt;
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

    // Save new account entry.
    @Override
    public int addAccount(String email, String password, String firstName, String lastName) {
        if (accountRepository.existsByEmail(email)) {
            return 1; // Code 1: Email already registered.
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12)); // Always a 60-char string.
        Account account = new Account(email, hashedPassword, firstName, lastName);
        account.setId(getNextAccountId());
        accountRepository.save(account);
        return 0;
    }

    // Get next account ID.
    @Override
    public long getNextAccountId() {
        return accountRepository.findMaxId() == null ? 1000 : accountRepository.findMaxId() + 1;
    }

}
