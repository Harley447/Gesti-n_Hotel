package com.mycompany.gestion_hotel.conexion;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexionBD {
    private final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private final String HOST = "localhost:3306";
    private final String DB = "Hotel_Terrazas";
    private final String URL = "jdbc:mysql://" + HOST + "/" + DB + "?serverTimezone=UTC";
    private final String USERNAME = "root";
    private final String PASSWORD = "Arley123!";
    
    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    public ConexionBD() {
        try {
            Class.forName(DB_DRIVER);
            con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            System.out.println("✅ Conexión exitosa a la base de datos");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ConexionBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Connection getConnection() {
        return con;
    }

    public void closeConnection() {
        try {
            if (con != null) con.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConexionBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ResultSet consultarBD(String sentencia) {
        try {
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(sentencia);
        } catch (SQLException ex) {
            System.out.println("Error en consulta: " + ex.getMessage());
        }
        return rs;
    }

    public boolean ejecutarActualizacion(String sentencia) {
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sentencia);
            return true;
        } catch (SQLException ex) {
            System.out.println("Error en actualización: " + ex.getMessage());
            return false;
        }
    }

    public boolean commitBD() {
        try {
            con.commit();
            return true;
        } catch (SQLException ex) {
            System.out.println("Error en commit: " + ex.getMessage());
            return false;
        }
    }

    public boolean rollbackBD() {
        try {
            con.rollback();
            return true;
        } catch (SQLException ex) {
            System.out.println("Error en rollback: " + ex.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        ConexionBD c = new ConexionBD();
        c.closeConnection();
    }
}
