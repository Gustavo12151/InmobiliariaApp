package com.example.inmobiliariaapp.models;

import java.io.Serializable;

public class Inmueble implements Serializable {

    private int idInmueble;
    private String direccion;
    private String uso;
    private String tipo;
    private int ambientes;
    private double precio;
    private double superficie;
    private double latitud;
    private double longitud;
    private int propietarioId;
    private String imagenUrl;
    private boolean disponible;

    // ✅ Agregar el contrato asociado (viene en la API)
    private Contrato contrato;


    public int getIdInmueble() {
        return idInmueble;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getUso() {
        return uso;
    }

    public String getTipo() {
        return tipo;
    }

    public int getAmbientes() {
        return ambientes;
    }

    public double getPrecio() {
        return precio;
    }

    public double getSuperficie() {
        return superficie;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public int getPropietarioId() {
        return propietarioId;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public boolean isDisponible() {
        return disponible;
    }

    // ✅ GETTERS Y SETTERS para contrato
    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }
}
