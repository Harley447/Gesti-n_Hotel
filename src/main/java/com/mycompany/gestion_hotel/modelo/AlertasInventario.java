package com.mycompany.gestion_hotel.model;

import java.sql.Timestamp;

public class AlertasInventario {

    private int idAlerta;
    private int idProducto;
    private String mensaje;
    private Timestamp fecha;

    public AlertasInventario() {}

    public AlertasInventario(int idAlerta, int idProducto, String mensaje, Timestamp fecha) {
        this.idAlerta = idAlerta;
        this.idProducto = idProducto;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    public int getIdAlerta() {
        return idAlerta;
    }

    public void setIdAlerta(int idAlerta) {
        this.idAlerta = idAlerta;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return mensaje;
    }
}
