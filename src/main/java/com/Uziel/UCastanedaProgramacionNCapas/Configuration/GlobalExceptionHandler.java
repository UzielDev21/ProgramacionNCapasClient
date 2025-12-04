package com.Uziel.UCastanedaProgramacionNCapas.Configuration;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public String handleHttpClientError(HttpClientErrorException ex, HttpSession session) {

        HttpStatus status = (HttpStatus) ex.getStatusCode();

        if (status == HttpStatus.UNAUTHORIZED) {
            session.invalidate();
            return "redirect:/Login?CredencialesInvalidas=true";
        }

        if (status == HttpStatus.FORBIDDEN) {
            session.invalidate();
            return "redirect:/Login?noAutorizado=true";
        }
        
        return "error";
    }

}
