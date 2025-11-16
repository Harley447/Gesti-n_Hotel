package com.mycompany.gestion_hotel.dao;

import com.mycompany.gestion_hotel.conexion.ConexionBD;
import com.mycompany.gestion_hotel.modelo.Ingresos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class IngresosDAO {

    private ConexionBD conexion;

    public IngresosDAO() {
        conexion = new ConexionBD();
    }

    // INSERTAR INGRESO (idIngreso viene de Transacciones)
    public boolean insertarIngreso(Ingresos ingreso) {

        String sql = "INSERT INTO Ingresos (idIngreso, metodoPago, concepto) VALUES ("
                + ingreso.getIdIngreso() + ", "
                + "'" + ingreso.getMetodoPago() + "', "
                + "'" + ingreso.getConcepto() + "'"
                + ")";

        boolean ok = conexion.ejecutarActualizacion(sql);
        conexion.closeConnection();
        return ok;
    }

    // LISTAR TODOS LOS INGRESOS
    public ArrayList<Ingresos> listarIngresos() {
        ArrayList<Ingresos> lista = new ArrayList<>();

        String sql = "SELECT * FROM Ingresos ORDER BY idIngreso DESC";

        try {
            ResultSet rs = conexion.consultarBD(sql);

            while (rs.next()) {
                Ingresos ingreso = new Ingresos(
                        rs.getInt("idIngreso"),
                        rs.getString("metodoPago"),
                        rs.getString("concepto")
                );
                lista.add(ingreso);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Error al listar ingresos: " + e.getMessage());
        }

        return lista;
    }

    // OBTENER INGRESO POR ID
    public Ingresos buscarIngreso(int idIngreso) {
        Ingresos ingreso = null;

        String sql = "SELECT * FROM Ingresos WHERE idIngreso = " + idIngreso;

        try {
            ResultSet rs = conexion.consultarBD(sql);

            if (rs.next()) {
                ingreso = new Ingresos(
                        rs.getInt("idIngreso"),
                        rs.getString("metodoPago"),
                        rs.getString("concepto")
                );
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Error al buscar ingreso: " + e.getMessage());
        }

        return ingreso;
    }

    // ELIMINAR INGRESO
    public boolean eliminarIngreso(int idIngreso) {
        String sql = "DELETE FROM Ingresos WHERE idIngreso = " + idIngreso;

        boolean ok = conexion.ejecutarActualizacion(sql);
        conexion.closeConnection();
        return ok;
    }
}
