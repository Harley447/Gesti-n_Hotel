package com.mycompany.gestion_hotel.modelo;

import javafx.scene.image.Image;

public class Producto {

    private int idProducto;
    private String nombre;
    private String categoria;
    private int cantidad;
    private double precio;
    private String ubicacion;
    private int idProveedor;
    private String imagenUrl;

    // Constructor completo
    public Producto(int idProducto, String nombre, String categoria, int cantidad, double precio, 
                   String ubicacion, int idProveedor, String imagenUrl) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.precio = precio;
        this.ubicacion = ubicacion;
        this.idProveedor = idProveedor;
        this.imagenUrl = imagenUrl;
    }

    // Constructor sin ID (para inserts)
    public Producto(String nombre, String categoria, int cantidad, double precio, 
                   String ubicacion, int idProveedor, String imagenUrl) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.precio = precio;
        this.ubicacion = ubicacion;
        this.idProveedor = idProveedor;
        this.imagenUrl = imagenUrl;
    }

    // Constructor sin imagen (para compatibilidad)
    public Producto(int idProducto, String nombre, String categoria, int cantidad, double precio, 
                   String ubicacion, int idProveedor) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.precio = precio;
        this.ubicacion = ubicacion;
        this.idProveedor = idProveedor;
        this.imagenUrl = null;
    }

    // Constructor sin imagen (para compatibilidad)
    public Producto(String nombre, String categoria, int cantidad, double precio, 
                   String ubicacion, int idProveedor) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.precio = precio;
        this.ubicacion = ubicacion;
        this.idProveedor = idProveedor;
        this.imagenUrl = null;
    }

    // GETTERS Y SETTERS

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    // Método para obtener la imagen como objeto Image de JavaFX
    public Image getImagen() {
        try {
            if (imagenUrl != null && !imagenUrl.isEmpty()) {
                return new Image(imagenUrl, 80, 60, true, true);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar imagen: " + e.getMessage());
        }
        return null;
    }

    // Método para verificar si tiene imagen
    public boolean tieneImagen() {
        return imagenUrl != null && !imagenUrl.isEmpty();
    }

    @Override
    public String toString() {
        return nombre + " - $" + precio + " (Stock: " + cantidad + ")";
    }
}