package com.Uziel.UCastanedaProgramacionNCapas.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("Suma")
public class SumaController {

    @GetMapping("SumaTotal")
    public String Suma(@RequestParam("NumeroA") int NumeroA,
                                 @RequestParam("NumeroB") int NumeroB, Model model){
        
        int resultado = NumeroA + NumeroB;
        model.addAttribute("NumeroA", NumeroA);
        model.addAttribute("NumeroB", NumeroB);
        model.addAttribute("resultado", resultado); 
        
        return "SumaParametros";
    }
    
}
