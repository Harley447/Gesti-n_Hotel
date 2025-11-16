package com.mycompany.gestion_hotel.modelo;

import java.sql.Date;

public class Transacciones {

    private int idTransaccion;
    private Date fecha;
    private double monto;
    private String descripcion;
    private String tipo; // Ingreso / Egreso
    private Integer registradoPor; // permite NULL

    public Transacciones() {
    }

    public Transacciones(int idTransaccion, Date fecha, double monto, String descripcion, String tipo, Integer registradoPor) {
        this.idTransaccion = idTransaccion;
        this.fecha = fecha;
        this.monto = monto;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.registradoPor = registradoPor;
    }

    public int getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(int idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(Integer registradoPor) {
        this.registradoPor = registradoPor;
    }

    @Override
    public String toString() {
        return tipo + " - $" + monto + " (" + fecha + ")";
    }
}
