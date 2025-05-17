package com.asterlink.rest.service;

import com.asterlink.rest.model.Account;

/**
 * Service interface for Account.
 * Declares implemented methods.
 * @author gl3bert
 */

public interface AccountService {

    // Save new account entry.
    int addAccount(String email, String password, String firstName, String lastName);

    // Get next ID number.
    long getNextAccountId();
}
