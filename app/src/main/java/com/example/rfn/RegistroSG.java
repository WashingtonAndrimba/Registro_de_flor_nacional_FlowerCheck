package com.example.rfn;

public class RegistroSG {
    private String id;
    private String enfermedad;
    private String finca;
    private String variedad;
    private String bloque;
    private String area;
    private String cantidad;
    private String fecha;

    public RegistroSG() {
    }

    public RegistroSG(String id, String enfermedad, String finca, String variedad, String bloque, String area, String cantidad, String fecha) {
        this.id = id;
        this.enfermedad = enfermedad;
        this.finca = finca;
        this.variedad = variedad;
        this.bloque = bloque;
        this.area = area;
        this.cantidad = cantidad;
        this.fecha = fecha;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnfermedad() {
        return enfermedad;
    }

    public void setEnfermedad(String enfermedad) {
        this.enfermedad = enfermedad;
    }

    public String getFinca() {
        return finca;
    }

    public void setFinca(String finca) {
        this.finca = finca;
    }

    public String getVariedad() {
        return variedad;
    }

    public void setVariedad(String variedad) {
        this.variedad = variedad;
    }

    public String getBloque() {
        return bloque;
    }

    public void setBloque(String bloque) {
        this.bloque = bloque;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
