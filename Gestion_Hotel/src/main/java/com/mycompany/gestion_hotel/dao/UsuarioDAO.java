package com.mycompany.gestion_hotel.dao;

import com.mycompany.gestion_hotel.conexion.ConexionBD;
import com.mycompany.gestion_hotel.modelo.Usuario;
import java.sql.*;

public class UsuarioDAO {

    private final ConexionBD conexionBD;

    // ✅ Constructor que recibe la conexión
    public UsuarioDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    // ✅ Método de autenticación
    public Usuario autenticar(String correo, String contrasena) {
        Usuario usuario = null;
        String sql = "SELECT * FROM Usuarios WHERE correo = ? AND contrasena = ?";

        try (PreparedStatement stmt = conexionBD.getConnection().prepareStatement(sql)) {
            stmt.setString(1, correo);
            stmt.setString(2, contrasena);
            ResultSet rs = stmt.executeQuery();

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
            System.out.println("Error al autenticar usuario: " + e.getMessage());
        }

        return usuario;
    }
}