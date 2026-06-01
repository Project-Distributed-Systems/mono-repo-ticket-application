package com.tickets.event_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class InstanceController {

    @Value("${INSTANCE_ID:unknown}")
    private String instanceId;

    @GetMapping("/whoami")
    public Map<String, String> whoami() {
        return Map.of("instance", instanceId);
    }
}