package com.asterlink.rest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Account class.
 * Stores information for user and admin accounts.
 * @author gl3bert
 */

@Entity
@Table(name="account")
public class Account {

    // Identifying column. Users use email to log in.
    @Id
    @Column(name="email")
    private String email;

    // Internal ID variable for simplifying internal operations.
    @Column(name="id")
    private long id;

    // Hashed password.
    @Column(name="password")
    private String password;

    // User's first name.
    @Column(name="first")
    private String first;

    // User's last name.
    @Column(name="last")
    private String last;

    // User's last login timestamp.
    @Column(name="last_login")
    private LocalDateTime lastLogin;

    // Counts attempts; used for blocking access.
    @Column(name="login_attempts")
    private int loginAttempts;

    // If email has been verified.
    @Column(name="verified")
    private boolean verified;

    // If this is an admin account.
    @Column(name="admin")
    private boolean admin;

    // Associated active subscription on file with the account.
    @Column(name="network")
    private int network;

    // Parameterized constructor: Account creation.
    public Account(String email, String password, String first, String last) {
        this.email = email;
        this.password = password; // Stores hashed value, 60 characters.
        this.first = first;
        this.last = last;
        // Set defaults - placeholders.
        this.lastLogin = LocalDateTime.now(); // TODO consider changing this later.
        this.loginAttempts = 0;
        this.verified = false;
        this.admin = false;
        this.network = 0;
    }

    // Parameterized constructor: Sending details.

    // Default empty constructor.
    public Account() {}

    // Getters and Setters.
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public long getI() { return id; }
    public void setId(long id) { this.id = id; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFirst() { return first; }
    public void setFirst(String first) { this.first = first; }
    public String getLast() { return last; }
    public void setLast(String last) { this.last = last; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    public int getLoginAttempts() { return loginAttempts; }
    public void setLoginAttempts(int loginAttempts) { this.loginAttempts = loginAttempts; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }
    public int getNetwork() { return network; }
    public void setNetwork(int network) { this.network = network; }
}
