package com.Uziel.UCastanedaProgramacionNCapas.ML;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Estado {
    private int IdEstado;
    private String Nombre;
    
    @JsonProperty("PaisJPA")
    public Pais Pais;
    
    public Estado(){
        
    }
    
    public Estado(String Nombre){
        this.Nombre = Nombre;
    }
    
    public void setIdEstado(int IdEstado){
        this.IdEstado = IdEstado;
    }
    
    public int getIdEstado(){
        return IdEstado;
    }
    
    public void setNombre(String Nombre){
        this.Nombre = Nombre;
    }
    
    public String getNombre(){
        return Nombre;
    }
    
    public void setPais(Pais pais){
        this.Pais = pais;
    }
    
    public Pais getPais(){
        return Pais;
    }
}
