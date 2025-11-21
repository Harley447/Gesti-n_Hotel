package com.mycompany.gestion_hotel.dao;

import com.mycompany.gestion_hotel.conexion.ConexionBD;
import com.mycompany.gestion_hotel.modelo.DetalleReporte;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetalleReporteDAO {

    private ConexionBD conexion;

    public DetalleReporteDAO() {
        conexion = new ConexionBD();
    }

    // Insertar registro
    public boolean insertarDetalle(DetalleReporte detalle) {

        String sql = "INSERT INTO DetalleReporte (idReporte, idTransaccion) VALUES ("
                + detalle.getIdReporte() + ", "
                + detalle.getIdTransaccion() + ")";

        boolean ok = conexion.ejecutarActualizacion(sql);
        conexion.closeConnection();
        return ok;
    }

    // Eliminar todos los detalles por idReporte
    public boolean eliminarPorReporte(int idReporte) {

        String sql = "DELETE FROM DetalleReporte WHERE idReporte = " + idReporte;

        boolean ok = conexion.ejecutarActualizacion(sql);
        conexion.closeConnection();
        return ok;
    }

    // Eliminar todos los detalles por idTransaccion
    public boolean eliminarPorTransaccion(int idTransaccion) {

        String sql = "DELETE FROM DetalleReporte WHERE idTransaccion = " + idTransaccion;

        boolean ok = conexion.ejecutarActualizacion(sql);
        conexion.closeConnection();
        return ok;
    }

    // Listar todos los detalles de un reporte
    public List<DetalleReporte> listarPorReporte(int idReporte) {

        List<DetalleReporte> lista = new ArrayList<>();
        String sql = "SELECT * FROM DetalleReporte WHERE idReporte = " + idReporte;

        try {
            ResultSet rs = conexion.consultarBD(sql);

            if (rs != null) {
                while (rs.next()) {
                    DetalleReporte detalle = new DetalleReporte(
                            rs.getInt("idReporte"),
                            rs.getInt("idTransaccion")
                    );
                    lista.add(detalle);
                }
                rs.close();
            }

        } catch (SQLException e) {
            System.out.println("Error listarPorReporte DetalleReporte: " + e.getMessage());
        }

        return lista;
    }

    // Listar todos los registros
    public List<DetalleReporte> listarTodos() {

        List<DetalleReporte> lista = new ArrayList<>();
        String sql = "SELECT * FROM DetalleReporte";

        try {
            ResultSet rs = conexion.consultarBD(sql);

            if (rs != null) {
                while (rs.next()) {
                    DetalleReporte detalle = new DetalleReporte(
                            rs.getInt("idReporte"),
                            rs.getInt("idTransaccion")
                    );
                    lista.add(detalle);
                }
                rs.close();
            }

        } catch (SQLException e) {
            System.out.println("Error listarTodos DetalleReporte: " + e.getMessage());
        }

        return lista;
    }
}
