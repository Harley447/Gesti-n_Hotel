package com.mycompany.gestion_hotel.dao;

import com.mycompany.gestion_hotel.conexion.ConexionBD;
import com.mycompany.gestion_hotel.model.Proveedor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProveedorDAO {

    private ConexionBD conexion;

    public ProveedorDAO() {
        conexion = new ConexionBD();
    }

    // LISTAR TODOS
    public ArrayList<Proveedor> listarProveedores() {
        ArrayList<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM Proveedores";

        try {
            ResultSet rs = conexion.consultarBD(sql);
            while (rs.next()) {
                Proveedor p = new Proveedor(
                    rs.getInt("idProveedor"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("correo"),
                    rs.getString("tipoServicio")
                );
                lista.add(p);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Error listar proveedores: " + e.getMessage());
        }
        return lista;
    }

    // INSERTAR
    public boolean insertarProveedor(Proveedor p) {
        String sql = "INSERT INTO Proveedores(nombre, telefono, correo, tipoServicio) VALUES("
                + "'" + p.getNombre() + "', "
                + "'" + p.getTelefono() + "', "
                + "'" + p.getCorreo() + "', "
                + "'" + p.getTipoServicio() + "')";

        boolean ok = conexion.ejecutarActualizacion(sql);
        conexion.closeConnection();
        return ok;
    }

    // BUSCAR POR ID
    public Proveedor buscarPorId(int id) {
        String sql = "SELECT * FROM Proveedores WHERE idProveedor = " + id;
        Proveedor p = null;

        try {
            ResultSet rs = conexion.consultarBD(sql);
            if (rs.next()) {
                p = new Proveedor(
                    rs.getInt("idProveedor"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("correo"),
                    rs.getString("tipoServicio")
                );
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Error buscar proveedor: " + e.getMessage());
        }
        return p;
    }

    // ACTUALIZAR
    public boolean actualizarProveedor(Proveedor p) {
        String sql = "UPDATE Proveedores SET "
                + "nombre='" + p.getNombre() + "', "
                + "telefono='" + p.getTelefono() + "', "
                + "correo='" + p.getCorreo() + "', "
                + "tipoServicio='" + p.getTipoServicio() + "' "
                + "WHERE idProveedor=" + p.getIdProveedor();

        boolean ok = conexion.ejecutarActualizacion(sql);
        conexion.closeConnection();
        return ok;
    }

    // ELIMINAR
    public boolean eliminarProveedor(int id) {
        String sql = "DELETE FROM Proveedores WHERE idProveedor=" + id;

        boolean ok = conexion.ejecutarActualizacion(sql);
        conexion.closeConnection();
        return ok;
    }
}
