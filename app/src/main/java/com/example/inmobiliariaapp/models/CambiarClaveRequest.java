package com.example.inmobiliariaapp.models;

public class CambiarClaveRequest {
    private String ClaveActual;
    private String NuevaClave;

    public CambiarClaveRequest(String claveActual, String claveNueva) {
        this.ClaveActual = claveActual;
        this.NuevaClave = claveNueva;
    }

    public String getClaveActual() {
        return ClaveActual;
    }

    public String getClaveNueva() {
        return NuevaClave;
    }
}
