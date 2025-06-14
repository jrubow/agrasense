package com.asterlink.rest.service.impl;

import com.asterlink.rest.model.Account;
import com.asterlink.rest.repository.AccountRepository;
import com.asterlink.rest.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service implementation for Account.
 * Code for methods.
 * @author gl3bert
 */

@Service
public class AccountServiceImpl implements AccountService {

    // Authentication manager for proper JWT logic.
    @Autowired
    AuthenticationManager authenticationManager;

    // Set up repository access.
    private final AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;

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

    // Verify login credentials.
    @Override
    public int checkCredentials(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        if (authentication.isAuthenticated()) {
            Account account = accountRepository.findByEmail(email);
            accountRepository.updateLastLogin(account.getId(), LocalDateTime.now());
            return 0;
        }
        return 1;
    }

    // Delete account and supplemental fields. Requires password validation.
    @Override
    public int deleteAccount(String email, String password) {
        if (!accountRepository.existsByEmail(email)) {
            return 1; // Code 1: Email not found. In theory, should never be return if using UI as intended.
        }
        Account account = accountRepository.findByEmail(email);
        if (!BCrypt.checkpw(password, account.getPassword())) {
            return 2; // Code 2: Incorrect password.
        }
        accountRepository.delete(account); // TODO: Ensure records from supplemental tables also get deleted.
        return 0;
    }

    // Update user's display name.
    @Override
    public int updateName(String email, String firstName, String lastName) {
        if (!accountRepository.existsByEmail(email)) {
            return 1; // Code 1: Email not found. In theory, should never be return if using UI as intended.
        }
        Account account = accountRepository.findByEmail(email);
        account.setFirst(firstName);
        account.setLast(lastName);
        accountRepository.save(account);
        return 0;
    }

    // Update user's password.
    @Override
    public int updatePassword(String email, String currentPassword, String newPassword) {
        if (!accountRepository.existsByEmail(email)) {
            return 1; // Code 1: Email not found. In theory, should never be return if using UI as intended.
        }
        Account account = accountRepository.findByEmail(email);
        if (!BCrypt.checkpw(currentPassword, account.getPassword())) {
            return 2; // Code 2: Incorrect password.
        }
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12)); // Always a 60-char string.
        account.setPassword(hashedPassword);
        accountRepository.save(account);
        return 0;
    }

    // Get next account ID.
    @Override
    public long getNextAccountId() {
        return accountRepository.findMaxId() == null ? 1000 : accountRepository.findMaxId() + 1;
    }

    // Find account by associated email.
    @Override
    public Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    // Find account by associated email. Hide password.
    @Override
    public Account getAccountByEmailNoPassword(String email) {
        Account a = accountRepository.findByEmail(email);
        a.setPassword(null);
        return a;
    }

}
