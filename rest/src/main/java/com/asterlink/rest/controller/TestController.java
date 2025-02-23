package com.asterlink.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/") public String health()
    {
        return "AsterLink: TestController";
    }
}