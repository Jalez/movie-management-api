package com.movieapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
public class RootController {
    @GetMapping("/")
    public ResponseEntity<String> root() {
        return ResponseEntity.ok("Movie Management API is running. See /swagger-ui.html for docs.");
    }
}
