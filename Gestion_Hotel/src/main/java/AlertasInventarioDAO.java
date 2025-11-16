package com.mycompany.gestion_hotel.dao;

import com.mycompany.gestion_hotel.conexion.ConexionBD;
import com.mycompany.gestion_hotel.model.AlertasInventario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AlertasInventarioDAO {

    private ConexionBD conexion;

    public AlertasInventarioDAO() {
        conexion = new ConexionBD();
    }

    // INSERTAR ALERTA
    public boolean insertarAlerta(AlertasInventario alerta) {
        String sql = "INSERT INTO AlertasInventario(idProducto, mensaje) VALUES("
                + alerta.getIdProducto() + ", "
                + "'" + alerta.getMensaje() + "')";

        boolean ok = conexion.ejecutarActualizacion(sql);
        conexion.closeConnection();
        return ok;
    }

    // LISTAR TODAS LAS ALERTAS
    public ArrayList<AlertasInventario> listarAlertas() {
        ArrayList<AlertasInventario> lista = new ArrayList<>();
        String sql = "SELECT * FROM AlertasInventario ORDER BY fecha DESC";

        try {
            ResultSet rs = conexion.consultarBD(sql);
            while (rs.next()) {
                AlertasInventario alerta = new AlertasInventario(
                        rs.getInt("idAlerta"),
                        rs.getInt("idProducto"),
                        rs.getString("mensaje"),
                        rs.getTimestamp("fecha")
                );
                lista.add(alerta);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Error listar alertas: " + e.getMessage());
        }

        return lista;
    }

    // OBTENER ALERTAS POR PRODUCTO
    public ArrayList<AlertasInventario> listarAlertasPorProducto(int idProducto) {
        ArrayList<AlertasInventario> lista = new ArrayList<>();
        String sql = "SELECT * FROM AlertasInventario WHERE idProducto = " + idProducto
                   + " ORDER BY fecha DESC";

        try {
            ResultSet rs = conexion.consultarBD(sql);
            while (rs.next()) {
                AlertasInventario alerta = new AlertasInventario(
                        rs.getInt("idAlerta"),
                        rs.getInt("idProducto"),
                        rs.getString("mensaje"),
                        rs.getTimestamp("fecha")
                );
                lista.add(alerta);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Error listar alertas por producto: " + e.getMessage());
        }

        return lista;
    }

    // ELIMINAR ALERTA
    public boolean eliminarAlerta(int idAlerta) {
        String sql = "DELETE FROM AlertasInventario WHERE idAlerta = " + idAlerta;

        boolean ok = conexion.ejecutarActualizacion(sql);
        conexion.closeConnection();
        return ok;
    }
}
