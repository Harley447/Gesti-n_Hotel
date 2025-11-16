package com.mycompany.gestion_hotel.dao;

import com.mycompany.gestion_hotel.conexion.ConexionBD;
import com.mycompany.gestion_hotel.modelo.Transacciones;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransaccionesDAO {

    private ConexionBD conexion;

    public TransaccionesDAO() {
        this.conexion = new ConexionBD();
    }

    // Insertar transacción y obtener ID
    public int insertarTransaccionYObtenerID(Transacciones t) {

        int idGenerado = -1;

        String sql = "INSERT INTO Transacciones (fecha, monto, descripcion, tipo, registradoPor) VALUES ("
                + "'" + t.getFecha() + "', "
                + t.getMonto() + ", "
                + "'" + escape(t.getDescripcion()) + "', "
                + "'" + escape(t.getTipo()) + "', "
                + (t.getRegistradoPor() != null ? t.getRegistradoPor() : "NULL")
                + ")";

        try {
            boolean ok = conexion.ejecutarActualizacion(sql);
            if (!ok) return -1;

            // Obtener id generado
            ResultSet rs = conexion.consultarBD("SELECT LAST_INSERT_ID() AS id");
            if (rs != null && rs.next()) {
                idGenerado = rs.getInt("id");
            }

            if (rs != null) rs.close();
            conexion.closeConnection();

        } catch (SQLException e) {
            System.out.println("Error insertarTransaccionYObtenerID: " + e.getMessage());
        }
        return idGenerado;
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("'", "''");
    }
}
