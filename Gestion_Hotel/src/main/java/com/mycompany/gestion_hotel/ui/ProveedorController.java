package com.mycompany.gestion_hotel.ui;

import com.mycompany.gestion_hotel.dao.ProveedorDAO;
import com.mycompany.gestion_hotel.model.Proveedor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProveedorController implements Initializable {

    @FXML
    private TableView<Proveedor> tablaProveedores;

    @FXML
    private TableColumn<Proveedor, Integer> colId;

    @FXML
    private TableColumn<Proveedor, String> colNombre;

    @FXML
    private TableColumn<Proveedor, String> colTelefono;

    @FXML
    private TableColumn<Proveedor, String> colCorreo;

    @FXML
    private TableColumn<Proveedor, String> colTipoServicio;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtCorreo;

    @FXML
    private TextField txtTipoServicio;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnEliminar;

    private ObservableList<Proveedor> lista = FXCollections.observableArrayList();

    private ProveedorDAO proveedorDAO = new ProveedorDAO();

    private Proveedor proveedorSeleccionado = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getIdProveedor()).asObject());
        colNombre.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNombre()));
        colTelefono.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTelefono()));
        colCorreo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCorreo()));
        colTipoServicio.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTipoServicio()));

        cargarProveedores();

        tablaProveedores.setOnMouseClicked(event -> seleccionarProveedorDeTabla());
    }

    private void cargarProveedores() {
        lista.clear();
        lista.addAll(proveedorDAO.listarProveedores());
        tablaProveedores.setItems(lista);
    }

    private void seleccionarProveedorDeTabla() {
        proveedorSeleccionado = tablaProveedores.getSelectionModel().getSelectedItem();

        if (proveedorSeleccionado != null) {
            txtNombre.setText(proveedorSeleccionado.getNombre());
            txtTelefono.setText(proveedorSeleccionado.getTelefono());
            txtCorreo.setText(proveedorSeleccionado.getCorreo());
            txtTipoServicio.setText(proveedorSeleccionado.getTipoServicio());
        }
    }

    @FXML
    private void guardarProveedor() {

        if (txtNombre.getText().isEmpty()) {
            mostrarAlerta("Error", "El nombre es obligatorio.");
            return;
        }

        Proveedor p = new Proveedor();
        p.setNombre(txtNombre.getText());
        p.setTelefono(txtTelefono.getText());
        p.setCorreo(txtCorreo.getText());
        p.setTipoServicio(txtTipoServicio.getText());

        boolean ok = proveedorDAO.insertarProveedor(p);

        if (ok) {
            mostrarAlerta("Éxito", "Proveedor registrado correctamente.");
            limpiarCampos();
            cargarProveedores();
        } else {
            mostrarAlerta("Error", "No se pudo registrar el proveedor.");
        }
    }

    @FXML
    private void editarProveedor() {

        if (proveedorSeleccionado == null) {
            mostrarAlerta("Error", "Selecciona un proveedor.");
            return;
        }

        proveedorSeleccionado.setNombre(txtNombre.getText());
        proveedorSeleccionado.setTelefono(txtTelefono.getText());
        proveedorSeleccionado.setCorreo(txtCorreo.getText());
        proveedorSeleccionado.setTipoServicio(txtTipoServicio.getText());

        boolean ok = proveedorDAO.actualizarProveedor(proveedorSeleccionado);

        if (ok) {
            mostrarAlerta("Éxito", "Proveedor actualizado.");
            cargarProveedores();
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "No se pudo actualizar el proveedor.");
        }
    }

    @FXML
    private void eliminarProveedor() {

        if (proveedorSeleccionado == null) {
            mostrarAlerta("Error", "Selecciona un proveedor.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("¿Eliminar proveedor?");
        confirm.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean ok = proveedorDAO.eliminarProveedor(proveedorSeleccionado.getIdProveedor());
            if (ok) {
                mostrarAlerta("Éxito", "Proveedor eliminado.");
                cargarProveedores();
                limpiarCampos();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar.");
            }
        }
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        txtTipoServicio.clear();
        proveedorSeleccionado = null;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
