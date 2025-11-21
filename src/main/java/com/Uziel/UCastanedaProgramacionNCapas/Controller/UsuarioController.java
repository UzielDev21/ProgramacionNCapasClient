package com.Uziel.UCastanedaProgramacionNCapas.Controller;

import com.Uziel.UCastanedaProgramacionNCapas.ML.Colonia;
import com.Uziel.UCastanedaProgramacionNCapas.ML.Direccion;
import com.Uziel.UCastanedaProgramacionNCapas.ML.Estado;
import com.Uziel.UCastanedaProgramacionNCapas.ML.ErrorCarga;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
//import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("UsuarioIndex")
public class UsuarioController {

    private static final String urlBase = "http://localhost:8080";
//------------------------------------------------------------------INDEX------------------------------------------------------------------//

    @GetMapping
    public String Index(Model model) {

        RestTemplate restTemplateUsuario = new RestTemplate();
        ResponseEntity<Result<List<Usuario>>> responseEntityUsuario = restTemplateUsuario.exchange(urlBase + "/usuario",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Result<List<Usuario>>>() {
        });

        RestTemplate restTemplateRol = new RestTemplate();
        ResponseEntity<Result<List<Rol>>> responseEntityRol = restTemplateRol.exchange(urlBase + "/roles",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Result<List<Rol>>>() {
        });

        if (responseEntityUsuario.getStatusCode().value() == 200 && 
                responseEntityRol.getStatusCode().value() == 200) {

            Result resultUsuario = responseEntityUsuario.getBody();
            model.addAttribute("Usuarios", resultUsuario.object);
            
            Result resultRol = responseEntityRol.getBody();
            model.addAttribute("Roles", resultRol.object);
            model.addAttribute("Usuario", new Usuario());

        } else {
            return "Error";
        }

        return "UsuarioIndex";
    }

//    @GetMapping("/DeleteUsuario/{IdUsuario}")
//    public String DeleteUsuario(@PathVariable("IdUsuario") int IdUsuario, RedirectAttributes redirectAttributes) {
//
//        Result resultJPA = new Result();
//
//        if (resultJPA != null) {
//            resultJPA = usuarioJPADAOImplementation.DeleteJPA(IdUsuario);
//            redirectAttributes.addFlashAttribute("MsgDeleteSuccess", "Se elimino el dato correctamente");
//        } else {
//            redirectAttributes.addFlashAttribute("MsgDeleteError", "No se pudo eliminar el Usuario");
//        }
//
//        return "redirect:/UsuarioIndex";
//    }
////------------------------------------------------------------------BUSCAR USUARIO------------------------------------------------------------------//
//    @PostMapping()
//    public String BuscarUsuario(@ModelAttribute("Usuario") Usuario usuario, Model model) {
//
////        Result result = usuarioDAOImplementation.UsuariosBuscar(usuario);
////        model.addAttribute("Roles", rolDAOImplementation.GetAll().objects);
//        Result resultJPA = usuarioJPADAOImplementation.BuscarUsuarioJPA(usuario);
//        model.addAttribute("Usuarios", resultJPA.objects);
//        model.addAttribute("Roles", rolJPADAOImplementation.GetAllJPA().objects);
//        model.addAttribute("Usuario", usuario);
//
//        return "UsuarioIndex";
//    }
////------------------------------------------------------------------CARGA MASIVA------------------------------------------------------------------//
//    @GetMapping("/CargaMasiva")
//    public String CargaMasiva() {
//        return "CargaMasiva";
//    }
////------------------------------------------------------------------EJECUCIÓN DE CARGA MASIVA------------------------------------------------------------------//
//    @GetMapping("/CargaMasiva/Procesar")
//    public String CargaMasiva(HttpSession session, Model model) throws Exception {
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
//        return "CargaMasiva";
//    }
////------------------------------------------------------------------TRANSFERENCIA Y LECTURA DE ARCHIVO------------------------------------------------------------------//
//    @PostMapping("/CargaMasiva")
//    public String CargaMasiva(@RequestParam("archivo") MultipartFile archivo, Model model, HttpSession session) {
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
//        return "CargaMasiva";
//    }
////------------------------------------------------------------------VALIDAR DATOS DE ARCHIVOS------------------------------------------------------------------//
//    public List<ErrorCarga> ValidarDatosArchivo(List<Usuario> usuarios) {
//
//        List<ErrorCarga> erroresCarga = new ArrayList<>();
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
//        return erroresCarga;
//    }
////------------------------------------------------------------------LECTURA DE ARCHIVO TXT ------------------------------------------------------------------//
//    public List<Usuario> LecturaArchivoTXT(File archivo) {
//
//        List<Usuario> usuarios = new ArrayList<>();
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
//        return usuarios;
//    }
////------------------------------------------------------------------lECTURA ARCHIVO XLSX------------------------------------------------------------------//
//    public List<Usuario> LecturaArchivoXLSX(File archivo) {
//        List<Usuario> usuarios = new ArrayList<>();
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
//        return usuarios;
//    }
////------------------------------------------------------------------CARGA DETAILS------------------------------------------------------------------//
    @GetMapping("/Details/{IdUsuario}")
    public String Details(@PathVariable int IdUsuario, Model model) {
        
        RestTemplate restTemplate = new RestTemplate();
        
        ResponseEntity<Result<Usuario>> responseEntityUsuario = restTemplate.exchange(urlBase + "/usuario/" + IdUsuario,
                HttpMethod.GET,
                HttpEntity.EMPTY, 
                new ParameterizedTypeReference<Result<Usuario>>(){});
        
        ResponseEntity <Result<List<Rol>>> responseEntityRol = restTemplate.exchange(urlBase + "/roles", 
                HttpMethod.GET, 
                HttpEntity.EMPTY, 
                new ParameterizedTypeReference<Result<List<Rol>>>(){});

        if (responseEntityUsuario.getStatusCode().value() == 200 && 
                responseEntityRol.getStatusCode().value() == 200) {

            Result resultUsuario = responseEntityUsuario.getBody();
            model.addAttribute("UsuarioId", resultUsuario.object);
            
            Result resultRol = responseEntityRol.getBody();
            model.addAttribute("Roles", resultRol.object);
            
        } else {
            return "Error";
        }
        
//        model.addAttribute("Paises", paisJPADAOImplementation.GetAllJPA().objects);
        model.addAttribute("Direccion", new Direccion());

        return "UsuarioDetails";
    }
//    
////------------------------------------------------------------------ACTUALIZAR IMAGEN------------------------------------------------------------------//
//    @PostMapping("/Details/Imagen/{IdUsuario}")
//    public String UpdateImagen(@PathVariable int IdUsuario, RedirectAttributes redirectAttributes,
//                               @RequestParam("imagenFile") MultipartFile multipartFile){
//
//        try {
//            
//            if (multipartFile != null && !multipartFile.isEmpty()) {
//                String originalName = multipartFile.getOriginalFilename();
//                if (originalName != null && originalName.contains(".")) {
//                    
//                    String extension = originalName.split("\\.")[1];
//                    
//                    if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("png")) {
//                        byte[] byteImagen = multipartFile.getBytes();
//                        String imagenBase64 = Base64.getEncoder().encodeToString(byteImagen);
//                        
//                        usuarioJPADAOImplementation.UpdateImagenJPA(IdUsuario, imagenBase64);
//                        redirectAttributes.addFlashAttribute("MsgSuccessImagen", "La imagen se actualizo correctamente");
//                    }else{
//                        redirectAttributes.addFlashAttribute("MsgErrorImagen", "Solo se pueden ingresar png o jpg");
//                    }
//                    
//                }
//            }
//            
//        } catch (IOException ex) {
//            redirectAttributes.addFlashAttribute("MsgError", "Error al procesar la imagen" + ex.getMessage());
//        }
//        
//        return "redirect:/UsuarioIndex/Details/" + IdUsuario;
//    }
////------------------------------------------------------------------ACTUALIZAR USUARIO DETAILS------------------------------------------------------------------//
//    @PostMapping("/Details")
//    public String Update(@ModelAttribute("Usuario") Usuario usuario) {
//
////        Result result = usuarioDAOImplementation.Update(usuario);
//        Result resultJPA = usuarioJPADAOImplementation.UpdateJPA(usuario);
//
//        return "redirect:/UsuarioIndex/Details/" + usuario.getIdUsuario();
//    }
//    
//    
//    
////------------------------------------------------------------------INSERTAR O ACTUALIZAR NUEVA DIRECCION DETAILS------------------------------------------------------------------//
//    @PostMapping("/DetailsDireccion/{IdUsuario}")
//    public String ActionDireccion(@PathVariable("IdUsuario") int IdUsuario, @ModelAttribute("Direccion") Direccion direccion,
//            BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
//
//        if (direccion.getIdDireccion() == 0) {
//
////            Result result = DireccionDAOImplementation.DireccionAdd(direccion, IdUsuario);
//            Result resultJPA = direccionJPADAOImplementation.DireccionAddJPA(direccion, IdUsuario);
//
//            if (resultJPA.correct) {
//                redirectAttributes.addFlashAttribute("MsgExito", "Se agrego correctamente la Direccion");
//            } else {
//                redirectAttributes.addFlashAttribute("MsgError", "No se agrego la direccion " + resultJPA.errorMessage);
//            }
//
//        } else {
//            //Result result = DireccionDAOImplementation.DireccionUpdate(direccion, IdUsuario);
//            Result resultJPA = direccionJPADAOImplementation.DireccionUpdateJPA(direccion);
//
//            if (resultJPA.correct) {
//                redirectAttributes.addFlashAttribute("MsgExito", "Se edito correctamente la Direccion");
//            } else {
//                redirectAttributes.addFlashAttribute("MsgError", "No se pudo editar la direccion " + resultJPA.errorMessage);
//            }
//
//        }
//        return "redirect:/UsuarioIndex/Details/" + IdUsuario;
//    }
////------------------------------------------------------------------CARGA DIRECCIONES DETAILS------------------------------------------------------------------//
//    @GetMapping("Details/Direccion/{IdDireccion}")
//    @ResponseBody
//    public Result getDireccion(@PathVariable("IdDireccion") int IdDireccion) {
//        //Result result = direccionDAOImplementation.DireccionGetbyId(IdDireccion);
//        //return direccionDAOImplementation.DireccionGetbyId(IdDireccion);
//
//        Result resultJPA = direccionJPADAOImplementation.DireccionGetByIdJPA(IdDireccion);
//        return direccionJPADAOImplementation.DireccionGetByIdJPA(IdDireccion);
//    }
////------------------------------------------------------------------ELIMINAR DIRECCION DETAILS------------------------------------------------------------------//
//    @GetMapping("Details/Direccion/Delete/{IdDireccion}")
//    @ResponseBody
//    public Result DireccionDelete(@PathVariable("IdDireccion") int IdDireccion, Model model) {
//
//        //Result result = direccionDAOImplementation.DireccionDelete(IdDireccion);
//        Result resultJPA = direccionJPADAOImplementation.DireccionDeleteJPA(IdDireccion);
//        return resultJPA;
//    }
////------------------------------------------------------------------FORMULARIO------------------------------------------------------------------//
//    @GetMapping("/Add")
//    public String Form(Model model) {
//
//        Usuario usuario = new Usuario();
//        model.addAttribute("Usuario", usuario);
////        model.addAttribute("Roles", rolDAOImplementation.GetAll().objects);
////        model.addAttribute("Paises", paisDAOImplementation.GetAll().objects);
//        model.addAttribute("Roles", rolJPADAOImplementation.GetAllJPA().objects);
//        model.addAttribute("Paises", paisJPADAOImplementation.GetAllJPA().objects);
//
//        return "UsuarioForm";
//    }
////------------------------------------------------------------------CARGA DDL FORM------------------------------------------------------------------//
//    @GetMapping("Add/Estados/{IdPais}")
//    @ResponseBody
//    public Result EstadosGetByIdPais(@PathVariable("IdPais") int IdPais) {
//
////        return estadoDAOImplementation.EstadosGetByIdPais(IdPais);
//        return estadoJPADAOImplementation.GetByIdPaisJPA(IdPais);
//    }
//
//    @GetMapping("Add/Municipios/{IdEstado}")
//    @ResponseBody
//    public Result MunicipiosGetByIdEstado(@PathVariable("IdEstado") int IdEstado) {
//
////        return municipioDAOImplementation.MunicipioGetByIdEstado(IdEstado);
//        return municipioJPADAOImplementation.GetByIdEstadoJPA(IdEstado);
//    }
//
//    @GetMapping("Add/Colonias/{IdMunicipio}")
//    @ResponseBody
//    public Result ColoniasGetByIdMunicipio(@PathVariable("IdMunicipio") int IdMunicipio) {
//
////        return coloniaDAOImplementation.ColoniasGetByIdMunicipio(IdMunicipio);
//        return coloniaJPADAOImplementation.GetByIdMunicipioJPA(IdMunicipio);
//    }
//
//    @GetMapping("Add/DireccionByCP/{CodigoPostal}")
//    @ResponseBody
//    public Result CodigoPostalGetDatos(@PathVariable("CodigoPostal") String CodigoPostal) {
////        return codigoPostalDAOImplementation.CodigoPostalGetDatos(CodigoPostal);
//        return codigoPostalJPADAOImplementation.CodigoPostalGetDatosJPA(CodigoPostal);
//    }
////------------------------------------------------------------------POST DEL FORMULARIO------------------------------------------------------------------//
//    @PostMapping("/Add")
//    public String Add(@Valid
//            @ModelAttribute("Usuario") Usuario usuario,
//            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes,
//            @RequestParam("imagenFile") MultipartFile multipartFile) {
//
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("Usuario", usuario);
////            model.addAttribute("Roles", rolDAOImplementation.GetAll().objects);
////            model.addAttribute("Paises", paisDAOImplementation.GetAll().objects);
//            model.addAttribute("Roles", rolJPADAOImplementation.GetAllJPA().objects);
//            model.addAttribute("Paises", paisJPADAOImplementation.GetAllJPA().objects);
//            if (usuario.Direcciones.get(0).Colonia.Municipio.Estado.Pais.getIdPais() > 0) {
//                model.addAttribute("Estados", estadoJPADAOImplementation.GetByIdPaisJPA(usuario.Direcciones.get(0).Colonia.Municipio.Estado.Pais.getIdPais()).objects);
//                if (usuario.Direcciones.get(0).Colonia.Municipio.Estado.getIdEstado() > 0) {
//                    model.addAttribute("Municipios", municipioJPADAOImplementation.GetByIdEstadoJPA(usuario.Direcciones.get(0).Colonia.Municipio.Estado.getIdEstado()).objects);
//                    if (usuario.Direcciones.get(0).Colonia.Municipio.getIdMunicipio() > 0) {
//                        model.addAttribute("Colonias", coloniaJPADAOImplementation.GetByIdMunicipioJPA(usuario.Direcciones.get(0).Colonia.Municipio.getIdMunicipio()).objects);
//                    }
//                }
//            }
//            return "UsuarioForm";
//        }
//        if (multipartFile != null && !multipartFile.isEmpty()) {
//            try {
//                String originalName = multipartFile.getOriginalFilename();
//                if (originalName != null && originalName.contains(".")) {
//
//                    String extension = originalName.split("\\.")[1];
//
//                    if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("png")) {
//                        byte[] byteImagen = multipartFile.getBytes();
//                        String imagenBase64 = Base64.getEncoder().encodeToString(byteImagen);
//                        usuario.setImagen(imagenBase64);
//                        usuario.setExtension(extension);
//                    }
//                }
//            } catch (IOException ex) {
//                Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        //Result result = usuarioDAOImplementation.Add(usuario);
//        Result resultJPA = usuarioJPADAOImplementation.AddJPA(usuario);
//        redirectAttributes.addFlashAttribute("successMessage", "El usuario " + usuario.getUserName() + " se creo con exito.");
//        return "redirect:/UsuarioIndex";
//    }
}
