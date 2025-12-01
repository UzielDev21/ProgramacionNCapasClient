package com.Uziel.UCastanedaProgramacionNCapas.Controller;

import com.Uziel.UCastanedaProgramacionNCapas.ML.Result;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("Login")
public class LoginController {

    private static final String urlBase = "http://localhost:8080";

    @GetMapping
    public String Login() {

        return "Login";
    }

    @PostMapping()
    public String procesarLogin(@RequestParam("username") String userName,
            @RequestParam("password") String password,
            Model model, HttpSession session) {

        Map<String, String> datos = new HashMap<>();
        datos.put("userName", userName);
        datos.put("password", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(datos, headers);

        try {

            ResponseEntity<Result<String>> responseEntity = restTemplate.exchange(urlBase + "/api/Login",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<Result<String>>() {
            });

            int status = responseEntity.getStatusCode().value();

            if (status == 200) {

                Result<String> result = responseEntity.getBody();

                if (result != null && Boolean.TRUE.equals(result.correct)) {
                    String jwt = result.object;

                    session.setAttribute("jwtToken", jwt);
                    session.setAttribute("loggedUsername", userName);
                    return "redirect:/UsuarioIndex";

                } else {

                    String msgError;

                    if (result != null && result.errorMessage != null) {

                        msgError = result.errorMessage;

                    } else {

                        msgError = "Error la iniciar sesión";

                    }

                    model.addAttribute("error", msgError);
                    return "Login";

                }

            } else if (status == 401) {
                Result<String> result = responseEntity.getBody();
                String msgError;

                if (result != null && result.errorMessage != null) {

                    msgError = result.errorMessage;

                } else {

                    msgError = "Credenciales Inactivas";

                }

                model.addAttribute("error", msgError);
                return "Login";

            } else {
                String msgError = "Error al comunicarse al servidor, codigo:" + status;
                model.addAttribute("error", msgError);
                return "Login";
            }

        } catch (Exception ex) {
            String msgError = "Error al Comunicarse al servidor: " + ex.getLocalizedMessage();
            model.addAttribute("error", msgError);
            return "Login";
        }
    }

    @PostMapping("/Logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {

        String token = (String) session.getAttribute("jwtToken");

        if (token == null) {
            redirectAttributes.addFlashAttribute("error", "No hay sesión activa");
            return "redirect:/Login";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        try {

            ResponseEntity<Result<String>> responseEntity = restTemplate.exchange(urlBase + "/api/Logout",
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<Result<String>>() {
            });

            if (responseEntity.getStatusCode().value() == 200) {
                Result<String> result = responseEntity.getBody();

                if (result != null && Boolean.TRUE.equals(result.correct)) {

                    session.invalidate();
                    redirectAttributes.addFlashAttribute("msgLogout", "Sesión cerrada");

                    return "redirect:/Login";
                }
            }

            redirectAttributes.addFlashAttribute("msgError", "No se pudo cerrar sesión");

        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("msgError", "Error logout; " + ex.getLocalizedMessage());
        }

        return "redirect:/Login";
    }
}
