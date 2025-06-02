package com.asterlink.rest.service;

/**
 * Emailing features outline.
 * Declares implemented methods.
 * @author gl3bert
 */

public interface EmailService {

    // Sends basic emails.
    void sendEmail(String to, String subject, String text);
}
