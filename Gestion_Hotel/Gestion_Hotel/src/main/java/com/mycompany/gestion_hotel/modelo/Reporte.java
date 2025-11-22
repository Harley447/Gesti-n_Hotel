package com.mycompany.gestion_hotel.modelo;

import java.sql.Date;

public class Reporte {

    private int idReporte;
    private String tipo;       // ENUM('Ingresos','Egresos','Inventario')
    private Date fechaInicio;
    private Date fechaFin;
    private Integer generadoPor; // puede ser null

    public Reporte() {
    }

    public Reporte(int idReporte, String tipo, Date fechaInicio, Date fechaFin, Integer generadoPor) {
        this.idReporte = idReporte;
        this.tipo = tipo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.generadoPor = generadoPor;
    }

    public int getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(int idReporte) {
        this.idReporte = idReporte;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Integer getGeneradoPor() {
        return generadoPor;
    }

    public void setGeneradoPor(Integer generadoPor) {
        this.generadoPor = generadoPor;
    }

    @Override
    public String toString() {
        return "Reporte{idReporte=" + idReporte + ", tipo=" + tipo + "}";
    }
}
