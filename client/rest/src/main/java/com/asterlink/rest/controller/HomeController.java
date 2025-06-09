package com.asterlink.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 *
 */

@Controller
public class HomeController {

    @GetMapping(value = {"/", "/{path:[^\\.]*}"}) // Forward only non-file requests
    public String forwardReactRoutes(HttpServletRequest request) {
        // System.out.println("New Session: " + request.getSession().getId());
        return "forward:/index.html";
    }
}
