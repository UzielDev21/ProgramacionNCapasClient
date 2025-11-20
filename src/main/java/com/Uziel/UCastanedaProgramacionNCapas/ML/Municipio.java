package com.Uziel.UCastanedaProgramacionNCapas.ML;

public class Municipio {
    private int IdMunicipio;
    private String Nombre;
    public Estado Estado;
    
    
    public Municipio(){
        
    }
    
    public Municipio(String Nombre){
        this.Nombre = Nombre;
    }
    
    public void setIdMunicipio(int IdMunicipio){
        this.IdMunicipio = IdMunicipio;
    }
    
    public int getIdMunicipio(){
        return IdMunicipio;
    }
    
    public void setNombre(String Nombre){
        this.Nombre = Nombre;
    }
    
    public String getNombre(){
        return Nombre;
    }
    
    public void setEstado(Estado estado){
        this.Estado = estado;
    }
    
    public Estado getEstado(){
        return Estado;
    }
}
