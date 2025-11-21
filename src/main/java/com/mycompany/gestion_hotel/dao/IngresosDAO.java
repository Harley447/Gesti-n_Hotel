package com.mycompany.gestion_hotel.dao;

import com.mycompany.gestion_hotel.conexion.ConexionBD;
import com.mycompany.gestion_hotel.modelo.Ingresos;
import com.mycompany.gestion_hotel.modelo.Transacciones;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class IngresosDAO {

    private ConexionBD conexion;

    public IngresosDAO() {
        conexion = new ConexionBD();
    }

    // INSERTAR INGRESO CON TRANSACCIÓN (NUEVA LÓGICA)
    public boolean insertarIngreso(Ingresos ingreso) {
        // 1. Primero crear la transacción
        TransaccionesDAO transDAO = new TransaccionesDAO();
        Transacciones transaccion = new Transacciones();

        transaccion.setFecha(new java.sql.Date(System.currentTimeMillis()));
        transaccion.setMonto(ingreso.getMonto());
        transaccion.setDescripcion("Ingreso - " + ingreso.getConcepto());
        transaccion.setTipo("Ingreso");
        transaccion.setRegistradoPor(1); // ID del usuario administrador

        int idTransaccion = transDAO.insertarTransaccionYObtenerID(transaccion);

        if (idTransaccion <= 0) {
            System.out.println("❌ Error al crear transacción para el ingreso.");
            return false;
        }

        // 2. Ahora insertar el ingreso usando el ID de la transacción
        String sql = "INSERT INTO Ingresos (idIngreso, metodoPago, concepto, monto) VALUES ("
                + idTransaccion + ", "
                + "'" + escape(ingreso.getMetodoPago()) + "', "
                + "'" + escape(ingreso.getConcepto()) + "', "
                + ingreso.getMonto()
                + ")";

        boolean ok = conexion.ejecutarActualizacion(sql);
        conexion.closeConnection();
        return ok;
    }

    // LISTAR TODOS LOS INGRESOS (ACTUALIZADO CON MONTO)
    public ArrayList<Ingresos> listarIngresos() {
        ArrayList<Ingresos> lista = new ArrayList<>();

        String sql = "SELECT * FROM Ingresos ORDER BY idIngreso DESC";

        try {
            ResultSet rs = conexion.consultarBD(sql);

            while (rs.next()) {
                Ingresos ingreso = new Ingresos(
                        rs.getInt("idIngreso"),
                        rs.getString("metodoPago"),
                        rs.getString("concepto"),
                        rs.getDouble("monto")  // Agregar monto
                );
                lista.add(ingreso);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Error al listar ingresos: " + e.getMessage());
        }

        return lista;
    }

    // OBTENER INGRESO POR ID (ACTUALIZADO CON MONTO)
    public Ingresos buscarIngreso(int idIngreso) {
        Ingresos ingreso = null;

        String sql = "SELECT * FROM Ingresos WHERE idIngreso = " + idIngreso;

        try {
            ResultSet rs = conexion.consultarBD(sql);

            if (rs.next()) {
                ingreso = new Ingresos(
                        rs.getInt("idIngreso"),
                        rs.getString("metodoPago"),
                        rs.getString("concepto"),
                        rs.getDouble("monto")  // Agregar monto
                );
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Error al buscar ingreso: " + e.getMessage());
        }

        return ingreso;
    }

    // ACTUALIZAR INGRESO (NUEVO MÉTODO)
    public boolean actualizarIngreso(Ingresos ingreso) {
        String sql = "UPDATE Ingresos SET "
                + "metodoPago = '" + escape(ingreso.getMetodoPago()) + "', "
                + "concepto = '" + escape(ingreso.getConcepto()) + "', "
                + "monto = " + ingreso.getMonto() + " "
                + "WHERE idIngreso = " + ingreso.getIdIngreso();

        boolean ok = conexion.ejecutarActualizacion(sql);
        conexion.closeConnection();
        return ok;
    }

    // ELIMINAR INGRESO
    public boolean eliminarIngreso(int idIngreso) {
        String sql = "DELETE FROM Ingresos WHERE idIngreso = " + idIngreso;

        boolean ok = conexion.ejecutarActualizacion(sql);
        conexion.closeConnection();
        return ok;
    }

    // MÉTODO PARA ESCAPAR STRINGS (PREVENIR SQL INJECTION)
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("'", "''");
    }
}