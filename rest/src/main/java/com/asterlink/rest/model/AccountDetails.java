package com.asterlink.rest.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Own implementation of required UserDetails class.
 * Necessary for proper JWT authentication.
 * @author gl3bert
 */

public class AccountDetails implements UserDetails {

    // Set up Account access.
    private final Account account;
    public AccountDetails(Account account) {
        this.account = account;
    }

    // Account email.
    @Override
    public String getUsername() {
        return account.getEmail();
    }

    // Account password, encrypted.
    @Override
    public String getPassword() {
        return account.getPassword();
    }

    // Authorities. TODO: Improve authorities once necessary.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (account.isAdmin()) {
            return Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /*
     * Below are functions mandated by the interface. Placeholders for now.
     * TODO: implement proper management at a later time.
     */

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
