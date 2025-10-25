package com.mycompany.gestion_hotel;

import com.mycompany.gestion_hotel.conexion.ConexionBD;

public class TestConexion {
    public static void main(String[] args) {
        ConexionBD conexion = new ConexionBD();
        conexion.closeConnection();
    }
}
