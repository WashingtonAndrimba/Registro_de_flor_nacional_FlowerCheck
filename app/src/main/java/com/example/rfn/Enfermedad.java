package com.example.rfn;

public class Enfermedad {
    private String id;
    private String nombre;
    private String descripcion;
    private String cultivosAfectados;
    private String fechaRegistro;
    private String tipoCultivo;
    private String gravedad;

    public Enfermedad() {
    }

    public Enfermedad(String id, String nombre, String descripcion, String cultivosAfectados, String fechaRegistro, String tipoCultivo, String gravedad) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.cultivosAfectados = cultivosAfectados;
        this.fechaRegistro = fechaRegistro;
        this.tipoCultivo = tipoCultivo;
        this.gravedad = gravedad;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCultivosAfectados() {
        return cultivosAfectados;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public String getTipoCultivo() {
        return tipoCultivo;
    }

    public String getGravedad() {
        return gravedad;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setCultivosAfectados(String cultivosAfectados) {
        this.cultivosAfectados = cultivosAfectados;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public void setTipoCultivo(String tipoCultivo) {
        this.tipoCultivo = tipoCultivo;
    }

    public void setGravedad(String gravedad) {
        this.gravedad = gravedad;
    }
}

