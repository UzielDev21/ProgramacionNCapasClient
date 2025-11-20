package com.Uziel.UCastanedaProgramacionNCapas.ML;

public class Rol {

    private int IdRol;
    private String NombreRol;
    private String Descripcion;
    
    public Rol(){
        
    }
    
    public Rol(String NombreRol, String Descripcion){
        this.NombreRol = NombreRol;
        this.Descripcion = Descripcion;
    }
    
    public void setIdRol(int IdRol){
        this.IdRol = IdRol;
    }
    
    public int getIdRol(){
        return IdRol;
    }
    
    public void setNombreRol(String NombreRol){
        this.NombreRol = NombreRol;
    }
    
    public String getNombreRol(){
        return NombreRol;
    }
    
    public void setDescripcion(String Descripcion){
        this.Descripcion = Descripcion;
    }
    
    public String getDescripcion(){
        return Descripcion;
    }
    
}
