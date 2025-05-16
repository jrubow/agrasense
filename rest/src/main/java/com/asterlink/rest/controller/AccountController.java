package com.asterlink.rest.controller;

import com.asterlink.rest.service.AccountService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller layer for Account
 * Declares and implements Account API routes.
 * @author gl3bert
 */

@RestController
@RequestMapping("/api/account")
public class AccountController {

    // Set up service access.
    private final AccountService accountService;
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
}
