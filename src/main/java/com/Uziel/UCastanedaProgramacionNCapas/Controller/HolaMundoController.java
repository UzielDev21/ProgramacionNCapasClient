package com.Uziel.UCastanedaProgramacionNCapas.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("Hola")
public class HolaMundoController {

    @GetMapping("HolaMundo")
    public String HolaMundo() {
        return "HolaMundo";
    }
    
    
    
}
