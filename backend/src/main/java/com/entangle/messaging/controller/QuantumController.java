package com.entangle.messaging.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.entangle.messaging.quantum.BB84SimulationResult;
import com.entangle.messaging.quantum.QKDKeyExchangeService;

@RestController
public class QuantumController {

    private final QKDKeyExchangeService qkdKeyExchangeService;

    public QuantumController(QKDKeyExchangeService qkdKeyExchangeService) {
        this.qkdKeyExchangeService = qkdKeyExchangeService;
    }

    @GetMapping("/quantum/bb84")
    public BB84SimulationResult runBB84Simulation(
            @RequestParam(defaultValue = "12") int bits,
            @RequestParam(defaultValue = "false") boolean eavesdropper) {

        return qkdKeyExchangeService.simulateBB84(bits, eavesdropper);
    }
}