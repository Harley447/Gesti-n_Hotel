package com.mycompany.gestion_hotel.dao;

import com.mycompany.gestion_hotel.conexion.ConexionBD;
import com.mycompany.gestion_hotel.modelo.Reporte;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    private ConexionBD conexion;

    public ReporteDAO() {
        this.conexion = new ConexionBD();
    }

    // Insertar reporte
    public boolean insertarReporte(Reporte reporte) {

        String sql = "INSERT INTO Reportes (tipo, fechaInicio, fechaFin, generadoPor) VALUES ("
                + "'" + escape(reporte.getTipo()) + "', "
                + (reporte.getFechaInicio() != null ? "'" + reporte.getFechaInicio() + "'" : "NULL") + ", "
                + (reporte.getFechaFin() != null ? "'" + reporte.getFechaFin() + "'" : "NULL") + ", "
                + (reporte.getGeneradoPor() != null ? reporte.getGeneradoPor() : "NULL")
                + ")";

        boolean ok = conexion.ejecutarActualizacion(sql);
        conexion.closeConnection();
        return ok;
    }

    // Actualizar reporte
    public boolean actualizarReporte(Reporte reporte) {

        String sql = "UPDATE Reportes SET "
                + "tipo = '" + escape(reporte.getTipo()) + "', "
                + "fechaInicio = " + (reporte.getFechaInicio() != null ? "'" + reporte.getFechaInicio() + "'" : "NULL") + ", "
                + "fechaFin = " + (reporte.getFechaFin() != null ? "'" + reporte.getFechaFin() + "'" : "NULL") + ", "
                + "generadoPor = " + (reporte.getGeneradoPor() != null ? reporte.getGeneradoPor() : "NULL")
                + " WHERE idReporte = " + reporte.getIdReporte();

        boolean ok = conexion.ejecutarActualizacion(sql);
        conexion.closeConnection();
        return ok;
    }

    // Eliminar reporte
    public boolean eliminarReporte(int idReporte) {
        String sql = "DELETE FROM Reportes WHERE idReporte = " + idReporte;
        boolean ok = conexion.ejecutarActualizacion(sql);
        conexion.closeConnection();
        return ok;
    }

    // Buscar por ID
    public Reporte buscarPorId(int idReporte) {
        Reporte reporte = null;
        String sql = "SELECT * FROM Reportes WHERE idReporte = " + idReporte;

        try {
            ResultSet rs = conexion.consultarBD(sql);
            if (rs != null && rs.next()) {

                reporte = new Reporte(
                        rs.getInt("idReporte"),
                        rs.getString("tipo"),
                        rs.getDate("fechaInicio"),
                        rs.getDate("fechaFin"),
                        rs.getObject("generadoPor") != null ? rs.getInt("generadoPor") : null
                );
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            System.out.println("Error buscarPorId Reporte: " + e.getMessage());
        }

        return reporte;
    }

    // Listar todos
    public List<Reporte> listarTodos() {

        List<Reporte> lista = new ArrayList<>();
        String sql = "SELECT * FROM Reportes ORDER BY idReporte DESC";

        try {
            ResultSet rs = conexion.consultarBD(sql);

            if (rs != null) {
                while (rs.next()) {
                    Reporte reporte = new Reporte(
                            rs.getInt("idReporte"),
                            rs.getString("tipo"),
                            rs.getDate("fechaInicio"),
                            rs.getDate("fechaFin"),
                            rs.getObject("generadoPor") != null ? rs.getInt("generadoPor") : null
                    );
                    lista.add(reporte);
                }
                rs.close();
            }
        } catch (SQLException e) {
            System.out.println("Error listarTodos Reporte: " + e.getMessage());
        }

        return lista;
    }

    // Utilidad para escapar caracteres peligrosos
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("'", "''");
    }
    
    // Insertar reporte y devolver ID generado (añadir en ReporteDAO)
    public int insertarReporteYObtenerID(Reporte reporte) {
        int idGenerado = -1;

        String sql = "INSERT INTO Reportes (tipo, fechaInicio, fechaFin, generadoPor) VALUES ("
            + "'" + escape(reporte.getTipo()) + "', "
            + (reporte.getFechaInicio() != null ? "'" + reporte.getFechaInicio() + "'" : "NULL") + ", "
            + (reporte.getFechaFin() != null ? "'" + reporte.getFechaFin() + "'" : "NULL") + ", "
            + (reporte.getGeneradoPor() != null ? reporte.getGeneradoPor() : "NULL")
            + ")";

        try {
            boolean ok = conexion.ejecutarActualizacion(sql);
            if (!ok) return -1;

            ResultSet rs = conexion.consultarBD("SELECT LAST_INSERT_ID() AS id");
            if (rs != null && rs.next()) {
            idGenerado = rs.getInt("id");
            }
            if (rs != null) rs.close();
            conexion.closeConnection();
        } catch (SQLException e) {
                System.out.println("Error insertarReporteYObtenerID: " + e.getMessage());
            }
        return idGenerado;
    }

}
