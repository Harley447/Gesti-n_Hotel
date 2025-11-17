/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gestion_hotel.ui;

import com.mycompany.gestion_hotel.dao.ProductoDAO;
import com.mycompany.gestion_hotel.modelo.Producto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class InventarioController {

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, Integer> colId;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, Integer> colCantidad;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, String> colUbicacion;
    @FXML private TableColumn<Producto, Integer> colProveedor;

    @FXML private TextField txtNuevaCantidad;
    @FXML private Label lblAlerta;
    @FXML private Button btnActualizarCantidad;

    private ProductoDAO productoDAO = new ProductoDAO();
    private ObservableList<Producto> listaProductos;

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarProductos();
        detectarStockBajo();

        btnActualizarCantidad.setOnAction(e -> actualizarCantidad());
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(param -> new javafx.beans.property.SimpleIntegerProperty(param.getValue().getIdProducto()).asObject());
        colNombre.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getNombre()));
        colCategoria.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getCategoria()));
        colCantidad.setCellValueFactory(param -> new javafx.beans.property.SimpleIntegerProperty(param.getValue().getCantidad()).asObject());
        colPrecio.setCellValueFactory(param -> new javafx.beans.property.SimpleDoubleProperty(param.getValue().getPrecio()).asObject());
        colUbicacion.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getUbicacion()));
        colProveedor.setCellValueFactory(param -> new javafx.beans.property.SimpleIntegerProperty(param.getValue().getIdProveedor()).asObject());
    }

    private void cargarProductos() {
        listaProductos = FXCollections.observableArrayList(productoDAO.listarProductos());
        tablaProductos.setItems(listaProductos);
    }

    private void detectarStockBajo() {
        StringBuilder alerta = new StringBuilder();

        for (Producto p : listaProductos) {
            if (p.getCantidad() < 10) {
                alerta.append("⚠ Stock bajo → ").append(p.getNombre())
                        .append(" (").append(p.getCantidad()).append(" unidades)\n");
            }
        }

        lblAlerta.setText(alerta.toString());
    }

    private void actualizarCantidad() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Seleccione un producto primero.");
            return;
        }

        // Validación de cantidad
        int nuevaCantidad;
        try {
            nuevaCantidad = Integer.parseInt(txtNuevaCantidad.getText());
            if (nuevaCantidad < 0) {
                mostrarAlerta("La cantidad no puede ser negativa.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Ingrese un número válido.");
            return;
        }

        if (productoDAO.actualizarStock(seleccionado.getIdProducto(), nuevaCantidad)) {
            seleccionado.setCantidad(nuevaCantidad);
            tablaProductos.refresh();
            detectarStockBajo();
            txtNuevaCantidad.clear();
            mostrarConfirmacion("Cantidad actualizada correctamente.");
        } else {
            mostrarAlerta("Error actualizando la cantidad.");
        }
    }

    private void mostrarAlerta(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void mostrarConfirmacion(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}