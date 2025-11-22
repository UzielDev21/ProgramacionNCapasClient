package com.Uziel.UCastanedaProgramacionNCapas.ML;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public class Usuario {

    private int IdUsuario; //Propiedad
    private String UserName;
    private String Nombre;
    private String ApellidoPaterno;
    private String ApellidoMaterno;
    private String Email;
    private String Password;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date FechaNacimiento;
    private String Sexo;
    private String Telefono;
    private String Celular;
    private String Curp;
    private String Imagen;
    private String extension;

    @JsonProperty("RolJPA")
    public Rol Rol; //Propiedad de navegación

    @JsonProperty("DireccionesJPA")
    public List<Direccion> Direcciones;

    public Usuario() {

    }

    public Usuario(String UserName, String Nombre, String ApellidoPaterno, String ApellidoMaterno, String Email, String Password, Date FechaNacimiento, String Sexo, String Telefono, String Celular, String Curp, String Imagen) {
        this.UserName = UserName;
        this.Nombre = Nombre;
        this.ApellidoPaterno = ApellidoPaterno;
        this.ApellidoMaterno = ApellidoMaterno;
        this.Email = Email;
        this.Password = Password;
        this.FechaNacimiento = FechaNacimiento;
        this.Sexo = Sexo;
        this.Celular = Celular;
        this.Curp = Curp;
        this.Imagen = Imagen;
    }

    public void setIdUsuario(int IdUsuario) {
        this.IdUsuario = IdUsuario;
    }

    public int getIdUsuario() {
        return IdUsuario;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setApellidoPaterno(String ApellidoPaterno) {
        this.ApellidoPaterno = ApellidoPaterno;
    }

    public String getApellidoPaterno() {
        return ApellidoPaterno;
    }

    public void setApellidoMaterno(String ApellidoMaterno) {
        this.ApellidoMaterno = ApellidoMaterno;
    }

    public String getApellidoMaterno() {
        return ApellidoMaterno;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getEmail() {
        return Email;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public String getPassword() {
        return Password;
    }

    public void setFechaNacimiento(Date FechaNacimiento) {
        this.FechaNacimiento = FechaNacimiento;
    }

    public Date getFechaNacimiento() {
        return FechaNacimiento;
    }

    public void setSexo(String Sexo) {
        this.Sexo = Sexo;
    }

    public String getSexo() {
        return Sexo;
    }

    public void setTelefono(String Telefono) {
        this.Telefono = Telefono;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setCelular(String Celular) {
        this.Celular = Celular;
    }

    public String getCelular() {
        return Celular;
    }

    public void setCurp(String Curp) {
        this.Curp = Curp;
    }

    public String getCurp() {
        return Curp;
    }

    public void setImagen(String Imagen) {
        this.Imagen = Imagen;
    }

    public String getImagen() {
        return Imagen;
    }

    public Rol getRol() {
        return Rol;
    }

    public void setRol(Rol Rol) {
        this.Rol = Rol;
    }

    public List<Direccion> getDirecciones() {
        return Direcciones;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setDirecciones(List<Direccion> Direcciones) {
        this.Direcciones = Direcciones;
    }

}
