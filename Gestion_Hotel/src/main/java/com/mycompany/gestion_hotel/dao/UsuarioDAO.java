package com.mycompany.gestion_hotel.dao;

import com.mycompany.gestion_hotel.conexion.ConexionBD;
import com.mycompany.gestion_hotel.modelo.Usuario;
import java.sql.*;

public class UsuarioDAO {

    private ConexionBD conexion;

    public UsuarioDAO() {
        conexion = new ConexionBD();
    }

    public Usuario autenticar(String correo, String contrasena) {
        Usuario usuario = null;
        String query = "SELECT * FROM Usuarios WHERE correo = ? AND contrasena = ?";

        try (PreparedStatement ps = conexion.getConnection().prepareStatement(query)) {
            ps.setString(1, correo);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                usuario = new Usuario(
                    rs.getInt("idUsuario"),
                    rs.getString("nombre"),
                    rs.getString("correo"),
                    rs.getString("contrasena"),
                    rs.getString("rol")
                );
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Error en autenticación: " + e.getMessage());
        }

        return usuario;
    }
}
