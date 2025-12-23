package com.Uziel.UCastanedaProgramacionNCapas.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api")
public class PasswordController {

    private static final String urlBase = "http://localhost:8080";
    
    @GetMapping("/ResetPassword")
    public String PasswordVista(){
        return "ResetPassword";
    }
    
    @GetMapping("/ChangePassword")
    public String ChangePassword(){
        return "ChangePassword";
    }
}
