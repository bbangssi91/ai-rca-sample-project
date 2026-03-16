package com.example.airca;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class LogGeneratorController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${n8n.webhook.url}")
    private String n8nWebhookUrl;

    @GetMapping("/simulate-error")
    public String simulateError(HttpServletRequest request) {
        String errorMessage = "Database Connection Timeout";
        String stackTrace = "java.sql.SQLException: Cannot get connection...";
        
        log.error("Error occurred at [{} {}]: {}", request.getMethod(), request.getRequestURI(), errorMessage);
        
        sendToN8n(errorMessage, stackTrace, request.getRequestURI(), request.getMethod());
        
        return "Error simulated and pushed to n8n with context!";
    }

    private void sendToN8n(String message, String detail, String endpoint, String httpMethod) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new HashMap<>();
            body.put("level", "ERROR");
            body.put("message", message);
            body.put("detail", detail);
            body.put("endpoint", endpoint);
            body.put("http_method", httpMethod);
            body.put("timestamp", String.valueOf(System.currentTimeMillis()));
            body.put("service", "ai-rca-sample-project");

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
            restTemplate.postForObject(n8nWebhookUrl, request, String.class);
            log.info("Successfully sent error log with context to n8n");
        } catch (Exception e) {
            log.error("Failed to send webhook to n8n: {}", e.getMessage());
        }
    }
}
