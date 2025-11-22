package com.mycompany.gestion_hotel.ui;

import com.mycompany.gestion_hotel.modelo.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuAdministradorController implements Initializable {

    @FXML
    private Label lblUsuario;
    
    private Usuario usuario;

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (lblUsuario != null && usuario != null) {
            lblUsuario.setText("Bienvenida, " + usuario.getNombre());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización si es necesaria
    }

    @FXML
    private void abrirGastos(ActionEvent event) {
        abrirModuloGastos(event);
    }

    @FXML
    private void abrirVentas(ActionEvent event) {
        abrirModuloVentas(event);
    }

    @FXML
    private void abrirHistorial(ActionEvent event) {
        abrirModuloHistorial(event);
    }

    @FXML
    private void abrirInventario(ActionEvent event) {
        abrirModuloInventario(event);
    }

    @FXML
    private void abrirReportes(ActionEvent event) {
        abrirModuloReportes(event);
    }

    @FXML
    private void abrirProveedores(ActionEvent event) {
        abrirModuloProveedores(event);
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setTitle("Gestión Hotel - Login");
            loginStage.setResizable(false);
            loginStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo cargar la pantalla de login");
        }
    }

    private void abrirModuloGastos(ActionEvent event) {
        try {
            // Cargar el FXML de gastos
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RegistrarGastos.fxml"));
            Parent root = loader.load();
            
            // Obtener el controlador de gastos si necesitas pasar datos
            RegistrarGastosController gastosController = loader.getController();
            // Si necesitas pasar el usuario a gastos, puedes hacerlo aquí:
            // gastosController.setUsuario(usuario);
            
            Stage gastosStage = new Stage();
            gastosStage.setScene(new Scene(root));
            gastosStage.setTitle("Gestión Hotel - Registrar Gastos");
            gastosStage.setMinWidth(900);
            gastosStage.setMinHeight(700);
            gastosStage.show();
            
            // Opcional: Cerrar la ventana actual del menú
            // Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // currentStage.close();
            
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo cargar el módulo de gastos: " + e.getMessage());
        }
    }

    private void abrirModuloVentas(ActionEvent event) {
        try {
            // Cargar el FXML de ventas
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventas.fxml"));
            Parent root = loader.load();
            
            // Obtener el controlador de ventas si necesitas pasar datos
            VentasController ventasController = loader.getController();
            // Si necesitas pasar el usuario a ventas, puedes hacerlo aquí:
            // ventasController.setUsuario(usuario);
            
            Stage ventasStage = new Stage();
            ventasStage.setScene(new Scene(root));
            ventasStage.setTitle("Gestión Hotel - Módulo de Ventas");
            ventasStage.setMinWidth(1200);
            ventasStage.setMinHeight(800);
            ventasStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo cargar el módulo de ventas: " + e.getMessage());
        }
    }

    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void abrirModuloInventario(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/inventario.fxml"));
            Parent root = loader.load();

            // Obtener el controller del módulo inventario, si necesitas pasarle datos:
            // InventarioController controller = loader.getController();
            // controller.setUsuario(usuario);

            Stage inventarioStage = new Stage();
            inventarioStage.setScene(new Scene(root));
            inventarioStage.setTitle("Gestión Hotel - Inventario de Productos");
            inventarioStage.setMinWidth(1000);
            inventarioStage.setMinHeight(700);
            inventarioStage.show();

            // Si deseas cerrar el menú al abrir inventario, descomenta:
            // Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo cargar el módulo de inventario: " + e.getMessage());
        }
    }
    
    
    public void abrirModuloHistorial(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/historial.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Historial de Transacciones");
            stage.setMinWidth(900);
            stage.setMinHeight(700);
            stage.show();

        } catch (IOException e) {
                e.printStackTrace();
                mostrarMensaje("Error", "No se pudo abrir el historial");
            }
    }
    
    
    
    private void abrirModuloProveedores(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GestionProveedores.fxml"));
            Parent root = loader.load();

            // Obtener el controlador si deseas pasar parámetros
            ProveedorController controller = loader.getController();
            // controller.setUsuario(usuario);  // si lo necesitas

            Stage proveedorStage = new Stage();
            proveedorStage.setScene(new Scene(root));
            proveedorStage.setTitle("Gestión Hotel - Proveedores");
            proveedorStage.setMinWidth(900);
            proveedorStage.setMinHeight(700);
            proveedorStage.show();

        } catch (IOException e) {
                e.printStackTrace();
                mostrarMensaje("Error", "No se pudo cargar el módulo de proveedores: " + e.getMessage());
            }
    }
    
    
    
    
    private void abrirModuloReportes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/generarReporte.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Generar Reporte");
            stage.setMinWidth(900);
            stage.setMinHeight(700);
            stage.show();

        } catch (IOException e) {
                e.printStackTrace();
                mostrarMensaje("Error", "No se pudo cargar el módulo de reportes: " + e.getMessage());
            }
    }



    
}