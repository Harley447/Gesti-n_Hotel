package com.mycompany.gestion_hotel.modelo;

public class DetalleReporte {

    private int idReporte;
    private int idTransaccion;

    public DetalleReporte() {
    }

    public DetalleReporte(int idReporte, int idTransaccion) {
        this.idReporte = idReporte;
        this.idTransaccion = idTransaccion;
    }

    public int getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(int idReporte) {
        this.idReporte = idReporte;
    }

    public int getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(int idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    @Override
    public String toString() {
        return "DetalleReporte{idReporte=" + idReporte +
                ", idTransaccion=" + idTransaccion + "}";
    }
}