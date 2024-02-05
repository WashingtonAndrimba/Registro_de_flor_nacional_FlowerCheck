package com.example.rfn;

public class Variedad {
    private String nombreFlor, colorFlor, alturaFlor, temporadaFlor, condicionesCuidadosFlor, fechaIngresoFlor;

    public Variedad() {

    }

    public Variedad(String nombreFlor, String colorFlor, String alturaFlor, String temporadaFlor, String condicionesCuidadosFlor, String fechaIngresoFlor) {
        this.nombreFlor = nombreFlor;
        this.colorFlor = colorFlor;
        this.alturaFlor = alturaFlor;
        this.temporadaFlor = temporadaFlor;
        this.condicionesCuidadosFlor = condicionesCuidadosFlor;
        this.fechaIngresoFlor = fechaIngresoFlor;
    }

    public String getNombreFlor() {
        return nombreFlor;
    }

    public void setNombreFlor(String nombreFlor) {
        this.nombreFlor = nombreFlor;
    }

    public String getColorFlor() {
        return colorFlor;
    }

    public void setColorFlor(String colorFlor) {
        this.colorFlor = colorFlor;
    }

    public String getAlturaFlor() {
        return alturaFlor;
    }

    public void setAlturaFlor(String alturaFlor) {
        this.alturaFlor = alturaFlor;
    }

    public String getTemporadaFlor() {
        return temporadaFlor;
    }

    public void setTemporadaFlor(String temporadaFlor) {
        this.temporadaFlor = temporadaFlor;
    }

    public String getCondicionesCuidadosFlor() {
        return condicionesCuidadosFlor;
    }

    public void setCondicionesCuidadosFlor(String condicionesCuidadosFlor) {
        this.condicionesCuidadosFlor = condicionesCuidadosFlor;
    }

    public String getFechaIngresoFlor() {
        return fechaIngresoFlor;
    }

    public void setFechaIngresoFlor(String fechaIngresoFlor) {
        this.fechaIngresoFlor = fechaIngresoFlor;
    }
}
