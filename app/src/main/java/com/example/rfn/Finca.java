package com.example.rfn;
public class Finca {

    private String id;
    private String nombre;
    private String dueno;
    private String area;
    private String fechaRegistro;
    private String tipoCultivo;

    public Finca() {
    }

    public Finca(String id, String nombre, String dueno, String area, String fechaRegistro, String tipoCultivo) {
        this.id = id;
        this.nombre = nombre;
        this.dueno = dueno;
        this.area = area;
        this.fechaRegistro = fechaRegistro;
        this.tipoCultivo = tipoCultivo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDueno() {
        return dueno;
    }

    public void setDueno(String dueno) {
        this.dueno = dueno;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getTipoCultivo() {
        return tipoCultivo;
    }

    public void setTipoCultivo(String tipoCultivo) {
        this.tipoCultivo = tipoCultivo;
    }
}
