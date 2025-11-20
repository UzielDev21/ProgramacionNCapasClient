package com.Uziel.UCastanedaProgramacionNCapas.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("Saludo")
public class SaludoController {

    @GetMapping("Hola/{nombre}")
    public String Saludo(@PathVariable("nombre") String nombre, Model model) {

        model.addAttribute("nombre", nombre);

        return "Saludo";
    }

}
