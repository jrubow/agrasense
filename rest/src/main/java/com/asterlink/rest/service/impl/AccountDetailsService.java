package com.asterlink.rest.service.impl;

import com.asterlink.rest.model.Account;
import com.asterlink.rest.model.AccountDetails;
import com.asterlink.rest.repository.AccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * AccountDetailsService class that implements UserDetailsService
 * Necessary for JWT authentication.
 * @author gl3bert
 */

@Service
public class AccountDetailsService implements UserDetailsService {

    // Set up repository access.
    private AccountRepository accountRepository;
    public AccountDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // Implement required UserDetailService method.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(username);
        if (account == null) {
            throw new UsernameNotFoundException(username);
        }
        return new AccountDetails(account);
    }

}
