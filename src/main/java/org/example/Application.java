package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Main entry point for the Spring Boot application.
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

/**
 * Swagger Configuration Class.
 * This class sets up the Swagger UI and scans the REST API for documentation.
 */
@Configuration
@EnableSwagger2
class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.example"))
                .paths(PathSelectors.any())
                .build();
    }
}

/**
 * REST Controller for handling Webhooks.
 */
@RestController
class WebhookController {

    /**
     * Handles incoming POST requests for webhooks.
     * Validates the request and processes the data.
     *
     * @param request The request object containing the API key and data.
     */
    @PostMapping("/webhook")
    public void handleWebhook(@Valid @RequestBody WebhookRequest request) {
        String apiKey = request.getApiKey();
        if (!isValidApiKey(apiKey)) {
            throw new SecurityException("Invalid API Key");
        }
        processWebhookData(request.getData());
        System.out.println("Successfully processed webhook request.");
    }

    /**
     * Validates the API key.
     *
     * @param apiKey The API key to validate.
     * @return True if the API key is valid, False otherwise.
     */
    private boolean isValidApiKey(String apiKey) {
        return "your-api-key".equals(apiKey);
    }

    /**
     * Processes the webhook data.
     * Add your business logic here.
     *
     * @param data The data payload from the webhook.
     */
    private void processWebhookData(Object data) {
        // Implement your business logic here
    }
}

/**
 * Global Exception Handler for REST Controllers.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * Handles SecurityException and returns an appropriate API error response.
     *
     * @param ex The thrown SecurityException.
     * @return A response entity containing the API error response.
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Object> handleSecurityException(SecurityException ex) {
        ApiErrorResponse response = new ApiErrorResponse();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setMessage(ex.getMessage());
        response.setTimestamp(System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles all other exceptions and returns a generic API error response.
     *
     * @param ex The thrown Exception.
     * @return A response entity containing the API error response.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex) {
        ApiErrorResponse response = new ApiErrorResponse();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage("An unexpected error occurred. Please contact support.");
        response.setTimestamp(System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

/**
 * Data Transfer Object for webhook requests.
 */
class WebhookRequest {
    @NotNull
    private String apiKey;
    private Object data;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

/**
 * Data Transfer Object for API error responses.
 */
class ApiErrorResponse {
    private int status;
    private String message;
    private long timestamp;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
