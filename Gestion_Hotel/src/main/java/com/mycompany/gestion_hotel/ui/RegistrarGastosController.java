package com.mycompany.gestion_hotel.ui;

import com.mycompany.gestion_hotel.dao.EgresoDAO;
import com.mycompany.gestion_hotel.modelo.Egreso;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistrarGastosController {

    @FXML
    private ComboBox<String> cmbConcepto;

    @FXML
    private TextField txtDetalle;

    @FXML
    private TextField txtMonto;

    @FXML
    private TableView<Egreso> tblGastos;

    @FXML
    private TableColumn<Egreso, Integer> colId;

    @FXML
    private TableColumn<Egreso, String> colConcepto;

    @FXML
    private TableColumn<Egreso, String> colDetalle;

    @FXML
    private TableColumn<Egreso, Double> colMonto;

    @FXML
    private TableColumn<Egreso, String> colFecha;

    @FXML
    private Label lblTotal;

    private ObservableList<Egreso> listaGastos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        // Conceptos disponibles
        cmbConcepto.getItems().addAll(
                "Servicios",
                "Mantenimiento",
                "Compras",
                "Otros"
        );

        // Configurar columnas
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getIdEgreso()).asObject());
        colConcepto.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getConcepto()));
        colDetalle.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDetalle()));
        colMonto.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getMonto()).asObject());
        colFecha.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(obtenerFechaEgreso(data.getValue().getIdEgreso())));

        cargarGastos();
    }

    // Obtener fecha desde BD (en Egresos.fecha)
    private String obtenerFechaEgreso(int id) {
        try {
            com.mycompany.gestion_hotel.conexion.ConexionBD con = new com.mycompany.gestion_hotel.conexion.ConexionBD();
            ResultSet rs = con.consultarBD("SELECT fecha FROM Egresos WHERE idEgreso=" + id);

            if (rs != null && rs.next()) {
                return rs.getString("fecha");
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo fecha: " + e.getMessage());
        }
        return "";
    }

    /** Cargar lista de egresos */
    private void cargarGastos() {
        try {
            EgresoDAO dao = new EgresoDAO();
            listaGastos.setAll(dao.listarTodos());
            tblGastos.setItems(listaGastos);
            actualizarTotal();

        } catch (Exception e) {
            System.out.println("Error cargando gastos: " + e.getMessage());
        }
    }

    /** Acción del botón Agregar */
    @FXML
    public void agregarGasto() {

        // Validaciones
        if (cmbConcepto.getValue() == null) {
            mostrarAlerta("Debe seleccionar un concepto.");
            return;
        }
        if (txtDetalle.getText().trim().isEmpty()) {
            mostrarAlerta("Debe ingresar un detalle.");
            return;
        }
        if (txtMonto.getText().trim().isEmpty()) {
            mostrarAlerta("Debe ingresar un monto.");
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(txtMonto.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("El monto debe ser numérico.");
            return;
        }

        // Crear modelo Egreso
        Egreso eg = new Egreso(
                0,
                txtDetalle.getText(),
                cmbConcepto.getValue(),
                monto
        );

        // Guardar en BD
        EgresoDAO dao = new EgresoDAO();
        boolean ok = dao.insertarEgreso(eg);

        if (!ok) {
            mostrarAlerta("Error insertando el egreso.");
            return;
        }

        mostrarInfo("Gasto registrado correctamente.");

        limpiarFormulario(null);
        cargarGastos();
    }

    /** Limpia los campos */
    @FXML
    public void limpiarFormulario(javafx.event.ActionEvent event) {
        cmbConcepto.setValue(null);
        txtDetalle.clear();
        txtMonto.clear();
    }

    /** Suma los montos y lo coloca en lblTotal */
    private void actualizarTotal() {
        double total = listaGastos.stream().mapToDouble(Egreso::getMonto).sum();
        lblTotal.setText(String.format("%.2f", total));
    }

    private void mostrarAlerta(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}