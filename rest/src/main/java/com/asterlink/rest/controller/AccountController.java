package com.asterlink.rest.controller;

import com.asterlink.rest.model.Account;
import com.asterlink.rest.model.AccountDetails;
import com.asterlink.rest.service.AccountService;
import com.asterlink.rest.service.JWTService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
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
    private final JWTService jwtService;
    public AccountController(AccountService accountService, JWTService jwtService) {
        this.accountService = accountService;
        this.jwtService = jwtService;
    }

    // Create new account with bare minimum parameters.
    @PostMapping("/public/register")
    public ResponseEntity<?> addAccount(@RequestBody Map<String, String> json) {
        // Check if all fields exist
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

        // Validate password maximum length.
        if (password.length() > 64) {
            return ResponseEntity.badRequest().body("Password must be 64 characters or less.");
        }

        // Validate password strength.
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,}$")) {
            return ResponseEntity.badRequest().body("Password must be at least 8 characters and include at least " +
                    "one number, one special character, one uppercase letter, and one lowercase letter.");
        }

        // Save account entry.
        int result = accountService.addAccount(email, password, first, last);
        if (result == 0) {
            // Success.
            return ResponseEntity.status(HttpStatus.CREATED).body("Account created successfully!");
        } else if (result == 1) {
            // Email in use.
            return ResponseEntity.badRequest().body("Email already in use.");
        } else {
            // Any other error.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknown error occurred.");
        }
    }

    // Create new account with bare minimum parameters.
    @PostMapping("/public/login")
    public ResponseEntity<?> checkCredentials(@RequestBody Map<String, String> json) {
        // Check if all fields exist.
        String[] requiredFields = {"email", "password"};
        for (String field : requiredFields) {
            if (!json.containsKey(field) || json.get(field).trim().isEmpty()) {
                return ResponseEntity.badRequest().body("All fields are required.");
            }
        }

        String email = json.get("email").trim();
        String password = json.get("password");

        // Validate email format.
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            return ResponseEntity.badRequest().body("Invalid email address.");
        }

        // Validate password maximum length.
        if (password.length() > 64) {
            return ResponseEntity.badRequest().body("Password must be 64 characters or less.");
        }

        // Process login request.
        int result = accountService.checkCredentials(email, password);
        if (result == 0) {
            // Login success. Send token.
            return ResponseEntity.ok(jwtService.generateToken(email));
        } else if (result == 1 || result == 2) {
            // Email does not exist (1) OR incorrect password (2).
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
        } else {
            // Any other error.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknown error occurred.");
        }
    }

    // Sample code for returning account fields from the token.
    @GetMapping("/details")
    public ResponseEntity<?> getAccountDetails(@AuthenticationPrincipal AccountDetails userDetails) {
        Account a = accountService.getAccountByEmail(userDetails.getUsername());
        if (a == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found.");
        }
        return ResponseEntity.ok(a);
    }

    // Delete account. Requires an extra password check.
    @DeleteMapping()
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal AccountDetails userDetails,
                                           @RequestBody String password) {
        Account a = accountService.getAccountByEmail(userDetails.getUsername());
        if (a == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid token.");
        }
        if (accountService.deleteAccount(a.getEmail(), password) != 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid password.");
        }
        return ResponseEntity.ok("Account deleted successfully.");
    }
}
