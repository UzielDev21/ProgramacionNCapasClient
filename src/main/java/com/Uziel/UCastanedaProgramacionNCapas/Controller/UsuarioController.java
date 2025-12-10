package com.Uziel.UCastanedaProgramacionNCapas.Controller;

import com.Uziel.UCastanedaProgramacionNCapas.ML.Colonia;
import com.Uziel.UCastanedaProgramacionNCapas.ML.Direccion;
import com.Uziel.UCastanedaProgramacionNCapas.ML.Estado;
import com.Uziel.UCastanedaProgramacionNCapas.ML.ErrorCarga;
import com.Uziel.UCastanedaProgramacionNCapas.ML.Municipio;
import com.Uziel.UCastanedaProgramacionNCapas.ML.Pais;
import com.Uziel.UCastanedaProgramacionNCapas.ML.Result;
import com.Uziel.UCastanedaProgramacionNCapas.ML.Rol;
import com.Uziel.UCastanedaProgramacionNCapas.ML.Usuario;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
//import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("UsuarioIndex")
public class UsuarioController {

    private static final String urlBase = "http://localhost:8080";
//------------------------------------------------------------------INDEX------------------------------------------------------------------//

    @GetMapping
    public String Index(Model model, HttpSession session) {

        String token = (String) session.getAttribute("jwtToken");

        if (token == null) {
            return "redirect:/Login";
        }
        
        String rol = (String) session.getAttribute("rol");
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        
        if (rol == null) {
            return "redirect:/Login";
        }
        
        if ("Colaborador".equalsIgnoreCase(rol)) {
            return "redirect:/UsuarioIndex/Add";
        }
        
        if ("Tercero".equalsIgnoreCase(rol)) {
            if (idUsuario != null) {
                return "redirect:/UsuarioIndex/Details/" + idUsuario;
            } else {
                return "redirect:/Login";
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        RestTemplate restTemplateUsuario = new RestTemplate();
        ResponseEntity<Result<Usuario>> responseEntityUsuario = restTemplateUsuario.exchange(urlBase + "/usuario",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Result<Usuario>>() {
        });

        RestTemplate restTemplateRol = new RestTemplate();
        ResponseEntity<Result<Rol>> responseEntityRol = restTemplateRol.exchange(urlBase + "/roles",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Result<Rol>>() {
        });

        if (responseEntityUsuario.getStatusCode().value() == 200
                && responseEntityRol.getStatusCode().value() == 200) {

            Result resultUsuario = responseEntityUsuario.getBody();
            model.addAttribute("Usuarios", resultUsuario.objects);

            Result resultRol = responseEntityRol.getBody();
            model.addAttribute("Roles", resultRol.objects);

            Usuario usuario = new Usuario();
            usuario.setStatus(2);
            model.addAttribute("Usuario", usuario);

            String user = (String) session.getAttribute("loggedUsername");
            model.addAttribute("UsuarioLogueado", user);

            return "UsuarioIndex";

        } else {
            return "Error";
        }

    }

////------------------------------------------------------------------ELIMINAR USUARIO------------------------------------------------------------------//
    @GetMapping("/DeleteUsuario/{IdUsuario}")
    public String DeleteUsuario(@PathVariable("IdUsuario") int IdUsuario, RedirectAttributes redirectAttributes, HttpSession session) {

        Result result = new Result();

        if (IdUsuario != 0) {

            String token = (String) session.getAttribute("jwtToken");

            if (token == null) {
                return "redirect:/Login";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Result<Usuario>> responseEntity = restTemplate.exchange(urlBase + "/usuario/" + IdUsuario,
                    HttpMethod.DELETE,
                    entity,
                    new ParameterizedTypeReference<Result<Usuario>>() {
            });

            if (responseEntity.getStatusCode().value() == 200) {
                result = responseEntity.getBody();
            }

            return "redirect:/UsuarioIndex";

        } else {
            result.correct = false;
            result.errorMessage = "No se pudo eliminar al usuario";
        }
        return "redirect:/UsuarioIndex";
    }

////------------------------------------------------------------------BUSCAR USUARIO------------------------------------------------------------------//
    @PostMapping()
    public String BuscarUsuario(@ModelAttribute("Usuario") Usuario usuario, Model model, HttpSession session) {

        String token = (String) session.getAttribute("jwtToken");

        if (token == null) {
            return "redirect:/Login";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        RestTemplate restTemplateUsuarios = new RestTemplate();
        HttpEntity<Usuario> usuarioBuscar = new HttpEntity<>(usuario, headers);
        ResponseEntity<Result<Usuario>> responseEntityUsuarioB = restTemplateUsuarios.exchange(urlBase + "/usuario/buscar",
                HttpMethod.POST,
                usuarioBuscar,
                new ParameterizedTypeReference<Result<Usuario>>() {
        });

        HttpEntity<?> entityTokenRol = new HttpEntity<>(headers);
        RestTemplate restTemplateRol = new RestTemplate();
        ResponseEntity<Result<Rol>> responseEntityRol = restTemplateRol.exchange(
                urlBase + "/roles",
                HttpMethod.GET,
                entityTokenRol,
                new ParameterizedTypeReference<Result<Rol>>() {
        });

        if (responseEntityUsuarioB.getStatusCode().value() == 200
                && responseEntityRol.getStatusCode().value() == 200) {

            Result resultUsuario = responseEntityUsuarioB.getBody();
            model.addAttribute("Usuarios", resultUsuario.objects);

            Result resultRol = responseEntityRol.getBody();
            model.addAttribute("Roles", resultRol.objects);

            model.addAttribute("Usuario", usuario);

            return "UsuarioIndex";

        } else {
            return "error";
        }
    }

////------------------------------------------------------------------CARGA MASIVA------------------------------------------------------------------//
    @GetMapping("/CargaMasiva")
    public String CargaMasiva() {
        return "CargaMasiva";
    }

////------------------------------------------------------------------EJECUCIÓN DE CARGA MASIVA------------------------------------------------------------------//
    @GetMapping("/CargaMasiva/Procesar")
    public String CargaMasiva(HttpSession session, Model model) throws Exception {
//        String Path = session.getAttribute("archivoCargaMasiva").toString();
//        session.removeAttribute("archivoCargaMasiva");
//
//        File archivo = new File(Path);
//        String extension = Path.split("\\.")[1];
//
//        List<Usuario> usuarios = new ArrayList<>();
//
//        if (extension.equals("txt")) {
//            usuarios = LecturaArchivoTXT(archivo);
//        } else if (extension.equals("xlsx")) {
//            usuarios = LecturaArchivoXLSX(archivo);
//        }
//        try {
//
////            Result result = usuarioDAOImplementation.AddAll(usuarios);
//            Result resultJP = usuarioJPADAOImplementation.AddAllJPA(usuarios);
//            model.addAttribute("MsgCorrecto", "Carga Masiva Realizada con exito");
//
//        } catch (Exception ex) {
//            model.addAttribute("MsgError", "Error en la Carga Masiva");
//            throw ex;
//        }
        return "CargaMasiva";
    }

////------------------------------------------------------------------TRANSFERENCIA Y LECTURA DE ARCHIVO------------------------------------------------------------------//
    @PostMapping("/CargaMasiva")
    public String CargaMasiva(@RequestParam("archivo") MultipartFile archivo, Model model, HttpSession session) {
//
//        String extension = archivo.getOriginalFilename().split("\\.")[1];
//
//        String path = System.getProperty("user.dir");
//        String pathArchivo = "src/main/resources/ArchivosCarga";
//        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmSS"));
//        String pathDefinitivo = path + "/" + pathArchivo + "/" + fecha + archivo.getOriginalFilename();
//
//        try {
//            archivo.transferTo(new File(pathDefinitivo));
//        } catch (Exception ex) {
//            return null;
//        }
//
//        List<Usuario> usuarios = new ArrayList<>();
//
//        if (extension.equals("txt")) {
//            usuarios = LecturaArchivoTXT(new File(pathDefinitivo));
//
//        } else if (extension.equals("xlsx")) {
//            usuarios = LecturaArchivoXLSX(new File(pathDefinitivo));
//
//        } else {
//            model.addAttribute("MsgFallido", "ingresa un archivo correcto");
//        }
//
//        List<ErrorCarga> errores = ValidarDatosArchivo(usuarios);
//        if (errores.isEmpty()) {
//
//            model.addAttribute("errores", false);
//            model.addAttribute("Usuarios", usuarios);
//            model.addAttribute("MsgCorrecto", "Se pueden procesar los datos");
//            session.setAttribute("archivoCargaMasiva", pathDefinitivo);
//        } else {
//            model.addAttribute("errores", true);
//            model.addAttribute("errores", errores);
//            model.addAttribute("MsgError", "Se encontraron errores");
//        }
//
        return "CargaMasiva";
    }

////------------------------------------------------------------------VALIDAR DATOS DE ARCHIVOS------------------------------------------------------------------//
    public List<ErrorCarga> ValidarDatosArchivo(List<Usuario> usuarios) {
//
        List<ErrorCarga> erroresCarga = new ArrayList<>();
//        int lineaError = 0;
//
//        for (Usuario usuario : usuarios) {
//
//            lineaError++;
//            System.out.println("validar linea " + lineaError + " ->" + usuario);
//            BindingResult bindingResult = validationService.validateObject(usuario);
//            List<ObjectError> errors = bindingResult.getAllErrors();
//
//            for (ObjectError error : errors) {
//                FieldError fieldError = (FieldError) error;
//                ErrorCarga errorCarga = new ErrorCarga();
//                errorCarga.campo = fieldError.getField();
//                errorCarga.descripcion = fieldError.getDefaultMessage();
//                errorCarga.linea = lineaError;
//                erroresCarga.add(errorCarga);
//            }
//        }
        return erroresCarga;
    }

////------------------------------------------------------------------LECTURA DE ARCHIVO TXT ------------------------------------------------------------------//
    public List<Usuario> LecturaArchivoTXT(File archivo) {
//
        List<Usuario> usuarios = new ArrayList<>();
//
//        try (InputStream inputStream = new FileInputStream(archivo); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));) {
//            String linea = "";
//
//            while ((linea = bufferedReader.readLine()) != null) {
//
//                String[] campos = linea.split("\\|");
//
//                Usuario usuario = new Usuario();
//                usuario.setUserName(campos[0].trim());
//                usuario.setNombre(campos[1].trim());
//                usuario.setApellidoPaterno(campos[2].trim());
//                usuario.setApellidoMaterno(campos[3].trim());
//                usuario.setEmail(campos[4].trim());
//                usuario.setPassword(campos[5].trim());
//                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//                String fechaIngresada = campos[6];
//                Date fechaFormateda = formatter.parse(fechaIngresada);
//                usuario.setFechaNacimiento(fechaFormateda);
//                usuario.setSexo(campos[7].trim());
//                usuario.setTelefono(campos[8].trim());
//                usuario.setCelular(campos[9].trim());
//                usuario.setCurp(campos[10].trim());
//                usuario.Rol = new Rol();
//                usuario.Rol.setIdRol(Integer.parseInt(campos[11].trim()));
//
//                usuarios.add(usuario);
//            }
//        } catch (Exception ex) {
//            System.out.println(ex.getLocalizedMessage());
//            return null;
//        }
//        System.out.println(usuarios.isEmpty());
        return usuarios;
    }

////------------------------------------------------------------------lECTURA ARCHIVO XLSX------------------------------------------------------------------//
    public List<Usuario> LecturaArchivoXLSX(File archivo) {
        List<Usuario> usuarios = new ArrayList<>();
//
//        try (InputStream inputStream = new FileInputStream(archivo); XSSFWorkbook workBook = new XSSFWorkbook(inputStream)) {
//
//            XSSFSheet workSheet = workBook.getSheetAt(0);
//
//            for (Row row : workSheet) {
//                Usuario usuario = new Usuario();
//                usuario.setUserName(row.getCell(0).toString().trim());
//                usuario.setNombre(row.getCell(1).toString().trim());
//                usuario.setApellidoPaterno(row.getCell(2).toString().trim());
//                usuario.setApellidoMaterno(row.getCell(3).toString().trim());
//                usuario.setEmail(row.getCell(4).toString().trim());
//                usuario.setPassword(row.getCell(5).toString().trim());
//                usuario.setFechaNacimiento(row.getCell(6).getDateCellValue());
//                usuario.setSexo(row.getCell(7).toString().trim());
//
//                DataFormatter formatter = new DataFormatter();
//                Cell cellPhone = row.getCell(8);
//                Cell cellCelular = row.getCell(9);
//                String phone = formatter.formatCellValue(cellPhone);
//                String celular = formatter.formatCellValue(cellCelular);
//
//                usuario.setTelefono(phone);
//                usuario.setCelular(celular);
//                usuario.setCurp(row.getCell(10).toString().trim());
//
//                usuario.Rol = new Rol();
//                usuario.Rol.setIdRol((int) row.getCell(11).getNumericCellValue());
//                usuarios.add(usuario);
//            }
//
//        } catch (Exception ex) {
//            System.out.println(ex.getLocalizedMessage());
//        }
        return usuarios;
    }

////------------------------------------------------------------------CARGA DETAILS------------------------------------------------------------------//
    @GetMapping("/Details/{IdUsuario}")
    public String Details(@PathVariable int IdUsuario, Model model, HttpSession session) {

        String token = (String) session.getAttribute("jwtToken");

        if (token == null) {
            return "redirect:/Login";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Result<Usuario>> responseEntityUsuario = restTemplate.exchange(urlBase + "/usuario/" + IdUsuario,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Result<Usuario>>() {
        });

        ResponseEntity<Result<Rol>> responseEntityRol = restTemplate.exchange(urlBase + "/roles",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Result<Rol>>() {
        });

        ResponseEntity<Result<Pais>> responseEntityPais = restTemplate.exchange(urlBase + "/pais",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Result<Pais>>() {
        });
        if (responseEntityUsuario.getStatusCode().value() == 200
                && responseEntityRol.getStatusCode().value() == 200
                && responseEntityPais.getStatusCode().value() == 200) {

            Result resultUsuario = responseEntityUsuario.getBody();
            model.addAttribute("UsuarioId", resultUsuario.object);
            
            Result resultRol = responseEntityRol.getBody();
            model.addAttribute("Roles", resultRol.objects);

            Result resultPais = responseEntityPais.getBody();
            model.addAttribute("Paises", resultPais.objects);
            model.addAttribute("Direccion", new Direccion());
            
            String user = (String) session.getAttribute("loggedUsername");
            model.addAttribute("UsuarioLogeado", user);
            
            return "UsuarioDetails";
        } else {
            return "Error";
        }
    }

////------------------------------------------------------------------ACTUALIZAR USUARIO DETAILS------------------------------------------------------------------//
    @PostMapping("/Details")
    public String Update(@ModelAttribute("Usuario") Usuario usuario, HttpSession session) {

        String token = (String) session.getAttribute("jwtToken");

        if (token == null) {
            return "redirect:/Login";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Usuario> usuarioUpdate = new HttpEntity<>(usuario, headers);
        ResponseEntity<Result<Usuario>> responseEntityUsuario = restTemplate.exchange(urlBase + "/usuario",
                HttpMethod.PUT,
                usuarioUpdate,
                new ParameterizedTypeReference<Result<Usuario>>() {
        });

        if (responseEntityUsuario.getStatusCode().value() == 200) {
            Result result = responseEntityUsuario.getBody();

        } else {
            return "error";
        }

        return "redirect:/UsuarioIndex/Details/" + usuario.getIdUsuario();
    }

////------------------------------------------------------------------INSERTAR O ACTUALIZAR NUEVA DIRECCION DETAILS------------------------------------------------------------------//
    @PostMapping("/DetailsDireccion/{IdUsuario}")
    public String ActionDireccion(@PathVariable("IdUsuario") int IdUsuario,
            @ModelAttribute("Direccion") Direccion direccion,
            BindingResult bindingResult, RedirectAttributes redirectAttributes,
            Model model, HttpSession session) {

        String token = (String) session.getAttribute("jwtToken");

        if (token == null) {
            return "redirect:/Login";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        if (direccion.getIdDireccion() == 0) {

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Direccion> direccionAdd = new HttpEntity<>(direccion, headers);
            ResponseEntity<Result<Direccion>> responseEntity = restTemplate.exchange(urlBase + "/direccion/" + IdUsuario,
                    HttpMethod.POST,
                    direccionAdd,
                    new ParameterizedTypeReference<Result<Direccion>>() {
            });

            if (responseEntity.getStatusCode().value() == 201) {
                Result result = responseEntity.getBody();
            } else {
                return "Error";
            }

        } else {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Direccion> direccionUpdate = new HttpEntity<>(direccion, headers);
            ResponseEntity<Result<Direccion>> responseEntityDireccion = restTemplate.exchange(urlBase + "/direccion/" + direccion.getIdDireccion(),
                    HttpMethod.PUT,
                    direccionUpdate,
                    new ParameterizedTypeReference<Result<Direccion>>() {
            });

            if (responseEntityDireccion.getStatusCode().value() == 200) {
                Result result = responseEntityDireccion.getBody();
            } else {
                return "error";
            }
//            if (resultJPA.correct) {
//                redirectAttributes.addFlashAttribute("MsgExito", "Se edito correctamente la Direccion");
//            } else {
//                redirectAttributes.addFlashAttribute("MsgError", "No se pudo editar la direccion " + resultJPA.errorMessage);
//            }
        }
        return "redirect:/UsuarioIndex/Details/" + IdUsuario;
    }
////------------------------------------------------------------------CARGA DIRECCIONES DETAILS------------------------------------------------------------------//

    @GetMapping("Details/Direccion/{IdDireccion}")
    @ResponseBody
    public Result getDireccion(@PathVariable("IdDireccion") int IdDireccion,
            HttpSession session) {

        String token = (String) session.getAttribute("jwtToken");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Result<Direccion>> responseEntityDireccion = restTemplate.exchange(urlBase + "/direccion/" + IdDireccion,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Result<Direccion>>() {
        });

        if (responseEntityDireccion.getStatusCode().value() == 200) {
            Result resultDireccion = responseEntityDireccion.getBody();
        } else {
            return null;
        }
        return responseEntityDireccion.getBody();
    }
////------------------------------------------------------------------ELIMINAR DIRECCION DETAILS------------------------------------------------------------------//

    @GetMapping("Details/Direccion/Delete/{IdDireccion}")
    @ResponseBody
    public Result DireccionDelete(@PathVariable("IdDireccion") int IdDireccion,
            Model model, HttpSession session) {

        Result result = new Result();

        if (IdDireccion != 0) {

            String token = (String) session.getAttribute("jwtToken");
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Result<Direccion>> responseEntity = restTemplate.exchange(urlBase + "/direccion/" + IdDireccion,
                    HttpMethod.DELETE,
                    entity,
                    new ParameterizedTypeReference<Result<Direccion>>() {
            });

            if (responseEntity.getStatusCode().value() == 200) {
                result = responseEntity.getBody();
            } else {
                return null;
            }

        } else {
            result.correct = false;
            result.errorMessage = "No se pudo eliminar la direccion";
        }
        return result;
    }

////------------------------------------------------------------------FORMULARIO------------------------------------------------------------------//
    @GetMapping("/Add")
    public String Form(Model model, HttpSession session) {

        String token = (String) session.getAttribute("jwtToken");

        if (token == null) {
            return "redirect:/Login";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Result<Rol>> responseEntityRol = restTemplate.exchange(urlBase + "/roles",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Result<Rol>>() {
        });

        ResponseEntity<Result<Pais>> responseEntityPais = restTemplate.exchange(urlBase + "/pais",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Result<Pais>>() {
        });

        if (responseEntityRol.getStatusCode().value() == 200
                && responseEntityPais.getStatusCode().value() == 200) {

            Result resultRol = responseEntityRol.getBody();
            model.addAttribute("Roles", resultRol.objects);

            Result resultPais = responseEntityPais.getBody();
            model.addAttribute("Paises", resultPais.objects);

            Usuario usuario = new Usuario();
            model.addAttribute("Usuario", usuario);
            
            String user = (String) session.getAttribute("loggedUsername");
            model.addAttribute("UsuarioLogeado", user);
        } else {
            return "error";
        }

        return "UsuarioForm";
    }

////------------------------------------------------------------------Se quito la carga de DDL, se llenan conforme viene de la serializacion------------------------------------------------------------------//
//    @GetMapping("Add/DireccionByCP/{CodigoPostal}")
//    @ResponseBody
//    public Result CodigoPostalGetDatos(@PathVariable("CodigoPostal") String CodigoPostal) {
////        return codigoPostalDAOImplementation.CodigoPostalGetDatos(CodigoPostal);
//        return codigoPostalJPADAOImplementation.CodigoPostalGetDatosJPA(CodigoPostal);
//    }
////------------------------------------------------------------------POST DEL FORMULARIO------------------------------------------------------------------//
    @PostMapping("/Add")
    public String Add(@ModelAttribute("Usuario") Usuario usuario, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes, @RequestParam("imagenFile") MultipartFile multipartFile,
            HttpSession session) {

        String token = (String) session.getAttribute("jwtToken");

        if (token == null) {
            return "redirect:/Login";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Result<Rol>> responseEntityRol = restTemplate.exchange(urlBase + "/roles",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Result<Rol>>() {
        });

        ResponseEntity<Result<Pais>> responseEntityPais = restTemplate.exchange(urlBase + "/pais",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Result<Pais>>() {
        });

        if (bindingResult.hasErrors()) {
            model.addAttribute("Usuario", usuario);

            Result resultRol = responseEntityRol.getBody();
            model.addAttribute("Roles", resultRol.objects);

            Result resultPais = responseEntityPais.getBody();
            model.addAttribute("Paises", resultPais.objects);

            if (usuario.Direcciones.get(0).Colonia.Municipio.Estado.Pais.getIdPais() > 0) {

                ResponseEntity<Result<Estado>> responseEntityEstado = restTemplate.exchange(urlBase + "/estados/" + usuario.Direcciones.get(0).Colonia.Municipio.Estado.Pais.getIdPais(),
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<Result<Estado>>() {
                });

                Result resultEstado = responseEntityEstado.getBody();
                model.addAttribute("Estados", resultEstado.objects);

                if (usuario.Direcciones.get(0).Colonia.Municipio.Estado.getIdEstado() > 0) {

                    ResponseEntity<Result<Municipio>> responseEntityMunicipio = restTemplate.exchange(urlBase + "/municipios/" + usuario.Direcciones.get(0).Colonia.Municipio.Estado.getIdEstado(),
                            HttpMethod.GET,
                            entity,
                            new ParameterizedTypeReference<Result<Municipio>>() {
                    });

                    Result resultMunicipio = responseEntityMunicipio.getBody();
                    model.addAttribute("Municipios", resultMunicipio.objects);

                    if (usuario.Direcciones.get(0).Colonia.Municipio.getIdMunicipio() > 0) {

                        ResponseEntity<Result<Colonia>> responseEntityColonia = restTemplate.exchange(urlBase + "/estados/" + usuario.Direcciones.get(0).Colonia.Municipio.getIdMunicipio(),
                                HttpMethod.GET,
                                entity,
                                new ParameterizedTypeReference<Result<Colonia>>() {
                        });

                        Result resultColonia = responseEntityColonia.getBody();
                        model.addAttribute("Colonias", resultColonia.objects);

                    }
                }
            }
            return "UsuarioForm";
        }
        if (multipartFile != null && !multipartFile.isEmpty()) {
            try {
                String originalName = multipartFile.getOriginalFilename();
                if (originalName != null && originalName.contains(".")) {

                    String extension = originalName.split("\\.")[1];

                    if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("png")) {
                        byte[] byteImagen = multipartFile.getBytes();
                        String imagenBase64 = Base64.getEncoder().encodeToString(byteImagen);
                        usuario.setImagen(imagenBase64);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        HttpEntity<Usuario> usuarioAdd = new HttpEntity<>(usuario, headers);
        HttpEntity<Result<Usuario>> responseEntityUsuario = restTemplate.exchange(urlBase + "/usuario",
                HttpMethod.POST,
                usuarioAdd,
                new ParameterizedTypeReference<Result<Usuario>>() {
        });

        Result resultUsuario = responseEntityUsuario.getBody();
        redirectAttributes.addFlashAttribute("successMessage", "El usuario " + usuario.getUserName() + " se creo con exito.");
        return "redirect:/UsuarioIndex";
    }
}
