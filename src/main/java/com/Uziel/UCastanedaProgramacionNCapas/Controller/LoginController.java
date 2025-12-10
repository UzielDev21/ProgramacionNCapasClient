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

                    Map<String, Object> claims = decodeJwt(jwt);

                    if (claims != null) {

                        String rol = (String) claims.get("rol");
                        Integer idUsuario = null;
                        Object idObject = claims.get("idUsuario");

                        if (idObject instanceof Integer) {

                            idUsuario = (Integer) idObject;

                        } else if (idObject instanceof Number) {

                            idUsuario = ((Number) (idObject)).intValue();

                        }

                        session.setAttribute("rol", rol);
                        session.setAttribute("idUsuario", idUsuario);

                        if ("administrador".equalsIgnoreCase(rol)
                                || "Gerente".equalsIgnoreCase(rol)
                                || "Lider".equalsIgnoreCase(rol)) {

                            return "redirect:/UsuarioIndex";

                        } else if ("Colaborador".equalsIgnoreCase(rol)) {

                            return "redirect:/UsuarioIndex/Add";

                        } else if ("Tercero".equalsIgnoreCase(rol)) {

                            if (idUsuario != null) {

                                return "redirect:/UsuarioIndex/Details/" + idUsuario;

                            } else {
                                model.addAttribute("error", "no se pudo obtener id del usuario del token");
                                return "Login";
                            }

                        } else {
                            model.addAttribute("error", "El rol del usuario no es reconocido");
                            return "Login";
                        }
                    } else {
                        model.addAttribute("error", "no se pudo leer el token correctamente");
                        return "Login";
                    }

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

    private Map<String, Object> decodeJwt(String jwt) {

        try {
            String[] partes = jwt.split("\\.");

            if (partes.length < 2) {
                return null;
            }

            String payload = new String(
                    java.util.Base64.getUrlDecoder().decode(partes[1]),
                    java.nio.charset.StandardCharsets.UTF_8
            );

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(payload, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            return null;
        }
    }
}
