package com.mycompany.gestion_hotel.modelo;

public class Ingresos {

    private int idIngreso;  // Igual al idTransaccion
    private String metodoPago; // Efectivo / Transferencia / Datafono
    private String concepto;   // Habitacion / Cafeteria / ConsumoExterno
    private double monto;      // Monto del ingreso

    public Ingresos() {
    }

    public Ingresos(int idIngreso, String metodoPago, String concepto, double monto) {
        this.idIngreso = idIngreso;
        this.metodoPago = metodoPago;
        this.concepto = concepto;
        this.monto = monto;
    }

    public int getIdIngreso() {
        return idIngreso;
    }

    public void setIdIngreso(int idIngreso) {
        this.idIngreso = idIngreso;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    @Override
    public String toString() {
        return "Ingreso ID=" + idIngreso + ", " + concepto + ", Pago: " + metodoPago + ", Monto: $" + monto;
    }
}