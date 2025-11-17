package com.mycompany.gestion_hotel.modelo;

public class Egreso {

    private int idEgreso;
    private String detalle;
    private String concepto;
    private Double monto;

    public Egreso() {
    }

    // Constructor completo con monto
    public Egreso(int idEgreso, String detalle, String concepto, double monto) {
        this.idEgreso = idEgreso;
        this.detalle = detalle;
        this.concepto = concepto;
        this.monto = monto;
    }

    // Constructor sin monto (opcional si lo necesitas)
    public Egreso(int idEgreso, String detalle, String concepto) {
        this.idEgreso = idEgreso;
        this.detalle = detalle;
        this.concepto = concepto;
    }

    public int getIdEgreso() {
        return idEgreso;
    }


    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    @Override
    public String toString() {
        return "Egreso{id=" + idEgreso +
               ", detalle='" + detalle + '\'' +
               ", concepto='" + concepto + '\'' +
               ", monto=" + monto +
               '}';
    }
}