package com.mycompany.gestion_hotel.dao;

import com.mycompany.gestion_hotel.conexion.ConexionBD;
import com.mycompany.gestion_hotel.modelo.Egreso;
import com.mycompany.gestion_hotel.modelo.Transacciones;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EgresoDAO {

    private ConexionBD conexion;

    public EgresoDAO() {
        this.conexion = new ConexionBD();
    }

    // Insertar Egreso correctamente enlazado con Transacciones
    public boolean insertarEgreso(Egreso egreso) {

        // 1. Crear transacción
        TransaccionesDAO transDAO = new TransaccionesDAO();
        Transacciones t = new Transacciones();

        t.setFecha(new java.sql.Date(System.currentTimeMillis()));
        t.setMonto(egreso.getMonto());
        t.setDescripcion(egreso.getDetalle());
        t.setTipo("Egreso");
        t.setRegistradoPor(1); // Administradora

        int idTrans = transDAO.insertarTransaccionYObtenerID(t);

        if (idTrans <= 0) {
            System.out.println("❌ Error al crear transacción para el egreso.");
            return false;
        }

        // 2. Insertar egreso usando ese mismo ID
        String sql = "INSERT INTO Egresos (idEgreso, detalle, concepto, monto) VALUES ("
                + idTrans + ", "
                + "'" + escape(egreso.getDetalle()) + "', "
                + "'" + escape(egreso.getConcepto()) + "', "
                + egreso.getMonto()
                + ")";

        boolean ok = conexion.ejecutarActualizacion(sql);
        conexion.closeConnection();
        return ok;
    }

    // Actualizar egreso
    public boolean actualizarEgreso(Egreso egreso) {
        String sql = "UPDATE Egresos SET "
                + "detalle = '" + escape(egreso.getDetalle()) + "', "
                + "concepto = '" + escape(egreso.getConcepto()) + "', "
                + "monto = " + egreso.getMonto() + " "
                + "WHERE idEgreso = " + egreso.getIdEgreso();

        boolean ok = conexion.ejecutarActualizacion(sql);
        conexion.closeConnection();
        return ok;
    }

    // Eliminar
    public boolean eliminarEgreso(int idEgreso) {
        String sql = "DELETE FROM Egresos WHERE idEgreso = " + idEgreso;
        boolean ok = conexion.ejecutarActualizacion(sql);
        conexion.closeConnection();
        return ok;
    }

    // Buscar por ID
    public Egreso buscarPorId(int idEgreso) {
        Egreso egreso = null;
        String sql = "SELECT * FROM Egresos WHERE idEgreso = " + idEgreso;

        try {
            ResultSet rs = conexion.consultarBD(sql);
            if (rs != null && rs.next()) {
                egreso = new Egreso(
                        rs.getInt("idEgreso"),
                        rs.getString("detalle"),
                        rs.getString("concepto"),
                        rs.getDouble("monto")
                );
            }
            if (rs != null) rs.close();

        } catch (SQLException e) {
            System.out.println("Error buscarPorId Egreso: " + e.getMessage());
        }
        return egreso;
    }

    // Listar todos
    public List<Egreso> listarTodos() {
        List<Egreso> lista = new ArrayList<>();
        String sql = "SELECT * FROM Egresos ORDER BY idEgreso DESC";

        try {
            ResultSet rs = conexion.consultarBD(sql);
            if (rs != null) {
                while (rs.next()) {
                    lista.add(new Egreso(
                            rs.getInt("idEgreso"),
                            rs.getString("detalle"),
                            rs.getString("concepto"),
                            rs.getDouble("monto")
                    ));
                }
                rs.close();
            }
        } catch (SQLException e) {
            System.out.println("Error listarTodos Egreso: " + e.getMessage());
        }
        return lista;
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("'", "''");
    }
}