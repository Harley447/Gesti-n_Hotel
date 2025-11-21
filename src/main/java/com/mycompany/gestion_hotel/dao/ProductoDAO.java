package com.mycompany.gestion_hotel.dao;

import com.mycompany.gestion_hotel.conexion.ConexionBD;
import com.mycompany.gestion_hotel.modelo.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    private final ConexionBD conexion;

    public ProductoDAO() {
        this.conexion = new ConexionBD();
    }

    // -------------------------------------------------------
    //                   INSERTAR PRODUCTO
    // -------------------------------------------------------
    public boolean insertarProducto(Producto p) {
        String sql = "INSERT INTO Productos (nombre, categoria, cantidad, precio, ubicacion, idProveedor, imagen_url) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {

            stmt.setString(1, p.getNombre());
            stmt.setString(2, p.getCategoria());
            stmt.setInt(3, p.getCantidad());
            stmt.setDouble(4, p.getPrecio());
            stmt.setString(5, p.getUbicacion());
            stmt.setInt(6, p.getIdProveedor());
            stmt.setString(7, p.getImagenUrl());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error insertando producto: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    //                   ACTUALIZAR PRODUCTO
    // -------------------------------------------------------
    public boolean actualizarProducto(Producto p) {
        String sql = "UPDATE Productos SET nombre=?, categoria=?, cantidad=?, precio=?, ubicacion=?, idProveedor=?, imagen_url=? " +
                "WHERE idProducto=?";

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {

            stmt.setString(1, p.getNombre());
            stmt.setString(2, p.getCategoria());
            stmt.setInt(3, p.getCantidad());
            stmt.setDouble(4, p.getPrecio());
            stmt.setString(5, p.getUbicacion());
            stmt.setInt(6, p.getIdProveedor());
            stmt.setString(7, p.getImagenUrl());
            stmt.setInt(8, p.getIdProducto());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error actualizando producto: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    //                   ELIMINAR PRODUCTO
    // -------------------------------------------------------
    public boolean eliminarProducto(int idProducto) {
        String sql = "DELETE FROM Productos WHERE idProducto=?";

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idProducto);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error eliminando producto: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    //                    LISTAR TODOS
    // -------------------------------------------------------
    public List<Producto> listarProductos() {
        List<Producto> lista = new ArrayList<>();

        String sql = "SELECT * FROM Productos";

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Producto p = new Producto(
                        rs.getInt("idProducto"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precio"),
                        rs.getString("ubicacion"),
                        rs.getInt("idProveedor"),
                        rs.getString("imagen_url")
                );
                lista.add(p);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error listando productos: " + e.getMessage());
        }

        return lista;
    }

    // -------------------------------------------------------
    //               BUSCAR POR ID
    // -------------------------------------------------------
    public Producto buscarPorId(int id) {
        String sql = "SELECT * FROM Productos WHERE idProducto=?";

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Producto(
                        rs.getInt("idProducto"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precio"),
                        rs.getString("ubicacion"),
                        rs.getInt("idProveedor"),
                        rs.getString("imagen_url")
                );
            }

        } catch (SQLException e) {
            System.out.println("❌ Error buscando producto: " + e.getMessage());
        }

        return null;
    }

    // -------------------------------------------------------
    //       LISTAR POR UBICACIÓN (vitrina, nevera...)
    // -------------------------------------------------------
    public List<Producto> listarPorUbicacion(String ubicacion) {
        List<Producto> lista = new ArrayList<>();

        String sql = "SELECT * FROM Productos WHERE ubicacion=?";

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {

            stmt.setString(1, ubicacion);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(new Producto(
                        rs.getInt("idProducto"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precio"),
                        rs.getString("ubicacion"),
                        rs.getInt("idProveedor"),
                        rs.getString("imagen_url")
                ));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error listando por ubicación: " + e.getMessage());
        }

        return lista;
    }

    // -------------------------------------------------------
    //               ACTUALIZAR SOLO IMAGEN
    // -------------------------------------------------------
    public boolean actualizarImagenProducto(int idProducto, String imagenUrl) {
        String sql = "UPDATE Productos SET imagen_url = ? WHERE idProducto = ?";

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {
            stmt.setString(1, imagenUrl);
            stmt.setInt(2, idProducto);
            
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error actualizando imagen: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    //               BUSCAR POR NOMBRE
    // -------------------------------------------------------
    public List<Producto> buscarPorNombre(String nombre) {
        List<Producto> lista = new ArrayList<>();

        String sql = "SELECT * FROM Productos WHERE nombre LIKE ?";

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {

            stmt.setString(1, "%" + nombre + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(new Producto(
                        rs.getInt("idProducto"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precio"),
                        rs.getString("ubicacion"),
                        rs.getInt("idProveedor"),
                        rs.getString("imagen_url")
                ));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error buscando por nombre: " + e.getMessage());
        }

        return lista;
    }

    // -------------------------------------------------------
    //               LISTAR POR CATEGORÍA
    // -------------------------------------------------------
    public List<Producto> listarPorCategoria(String categoria) {
        List<Producto> lista = new ArrayList<>();

        String sql = "SELECT * FROM Productos WHERE categoria=?";

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {

            stmt.setString(1, categoria);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(new Producto(
                        rs.getInt("idProducto"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precio"),
                        rs.getString("ubicacion"),
                        rs.getInt("idProveedor"),
                        rs.getString("imagen_url")
                ));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error listando por categoría: " + e.getMessage());
        }

        return lista;
    }

    // -------------------------------------------------------
    //               ACTUALIZAR STOCK
    // -------------------------------------------------------
    public boolean actualizarStock(int idProducto, int nuevaCantidad) {
        String sql = "UPDATE Productos SET cantidad = ? WHERE idProducto = ?";

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, nuevaCantidad);
            stmt.setInt(2, idProducto);
            
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error actualizando stock: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    //               REDUCIR STOCK (para ventas)
    // -------------------------------------------------------
    public boolean reducirStock(int idProducto, int cantidad) {
        String sql = "UPDATE Productos SET cantidad = cantidad - ? WHERE idProducto = ? AND cantidad >= ?";

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, cantidad);
            stmt.setInt(2, idProducto);
            stmt.setInt(3, cantidad);
            
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error reduciendo stock: " + e.getMessage());
            return false;
        }
    }
}