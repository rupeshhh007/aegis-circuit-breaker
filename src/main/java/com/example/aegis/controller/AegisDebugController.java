package com.example.aegis.controller;

import com.example.aegis.aegis.engine.AegisEngine;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AegisDebugController {

    private final AegisEngine aegis;

    public AegisDebugController(AegisEngine aegis) {
        this.aegis = aegis;
    }

    @GetMapping("/aegis/state")
    public String state() {
        return "AEGIS STATE = " + aegis.currentState();
    }
}
