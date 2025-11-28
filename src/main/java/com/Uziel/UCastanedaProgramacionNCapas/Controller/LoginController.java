package com.Uziel.UCastanedaProgramacionNCapas.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("Login")
public class LoginController {
    
    @GetMapping
    public String Login(){
        return "Login";
    }

}
