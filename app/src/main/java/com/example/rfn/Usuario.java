package com.example.rfn;

public class Usuario {

    private String correo;
    private String rol;

    public Usuario() {

    }

    public Usuario(String correo, String rol) {
        this.correo = correo;
        this.rol = rol;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
