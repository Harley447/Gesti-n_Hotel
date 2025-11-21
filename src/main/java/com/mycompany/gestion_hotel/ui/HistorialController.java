package com.mycompany.gestion_hotel.ui;

import com.mycompany.gestion_hotel.conexion.ConexionBD;
import com.mycompany.gestion_hotel.modelo.Transacciones;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class HistorialController implements Initializable {

    @FXML
    private TableView<Transacciones> tablaHistorial;

    @FXML
    private TableColumn<Transacciones, Date> colFecha;

    @FXML
    private TableColumn<Transacciones, Double> colMonto;

    @FXML
    private TableColumn<Transacciones, String> colDescripcion;

    @FXML
    private TableColumn<Transacciones, String> colTipo;

    @FXML
    private ComboBox<String> cbFiltroTipo;

    private ObservableList<Transacciones> listaTransacciones = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Enlazar columnas con el modelo
        colFecha.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getFecha()));
        colMonto.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getMonto()).asObject());
        colDescripcion.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescripcion()));
        colTipo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipo()));

        // Items del filtro
        cbFiltroTipo.getItems().addAll("Todos", "Ingreso", "Egreso");
        cbFiltroTipo.setValue("Todos");

        // Acción del filtro
        cbFiltroTipo.setOnAction(e -> cargarHistorial());

        // Cargar datos al abrir
        cargarHistorial();
    }

    private void cargarHistorial() {
        listaTransacciones.clear();

        String filtro = cbFiltroTipo.getValue();

        String sql = "SELECT idTransaccion, fecha, monto, descripcion, tipo, registradoPor FROM Transacciones";

        if (!filtro.equals("Todos")) {
            sql += " WHERE tipo = '" + filtro + "'";
        }

        try {
            ConexionBD conexion = new ConexionBD();
            ResultSet rs = conexion.consultarBD(sql);

            while (rs != null && rs.next()) {
                Transacciones t = new Transacciones(
                        rs.getInt("idTransaccion"),
                        rs.getDate("fecha"),
                        rs.getDouble("monto"),
                        rs.getString("descripcion"),
                        rs.getString("tipo"),
                        rs.getInt("registradoPor")
                );

                listaTransacciones.add(t);
            }

            if (rs != null) rs.close();
            conexion.closeConnection();

            tablaHistorial.setItems(listaTransacciones);

        } catch (SQLException e) {
            System.out.println("Error al cargar historial: " + e.getMessage());
        }
    }
}
