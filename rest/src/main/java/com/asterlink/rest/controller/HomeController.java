package com.asterlink.rest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping(value = {"/", "/{path:[^\\.]*}"}) // ✅ Forward only non-file requests
    public String forwardReactRoutes() {
        return "forward:/index.html";
    }
}
