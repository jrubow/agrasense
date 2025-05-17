package com.asterlink.rest.controller;

import com.asterlink.rest.model.Account;
import com.asterlink.rest.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    // Create new account with bare minimum parameters.
    @PostMapping("/public/register")
    public ResponseEntity<?> addAccount(@RequestBody Map<String, String> json) {
        // Check that all fields exist
        String[] requiredFields = {"first", "last", "email", "password"};
        for (String field : requiredFields) {
            if (!json.containsKey(field) || json.get(field).trim().isEmpty()) {
                return ResponseEntity.badRequest().body("All fields are required.");
            }
        }

        String first = json.get("first").trim();
        String last = json.get("last").trim();
        String email = json.get("email").trim();
        String password = json.get("password");

        // Check length of first and last name.
        if (first.length() > 32) {
            return ResponseEntity.badRequest().body("First name must be 32 characters or less.");
        }

        if (last.length() > 32) {
            return ResponseEntity.badRequest().body("Last name must be 32 characters or less.");
        }

        // Validate email format.
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            return ResponseEntity.badRequest().body("Invalid email address.");
        }

        // Validate password strength.
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,}$")) {
            return ResponseEntity.badRequest().body("Password must be at least 8 characters and include at least " +
                    "one number, one special character, one uppercase letter, and one lowercase letter.");
        }

        // Save account entry.
        int result = accountService.addAccount(email, password, first, last);
        if (result == 0) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Account created successfully.");
        } else if (result == 1) {
            return ResponseEntity.badRequest().body("Email already in use.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknown error occurred.");
        }
    }
}
