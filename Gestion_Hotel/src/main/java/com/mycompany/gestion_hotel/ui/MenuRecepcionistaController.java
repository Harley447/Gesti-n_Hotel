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

public class MenuRecepcionistaController implements Initializable {

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
    private void abrirVentas(ActionEvent event) {
        abrirModuloVentas(event);
    }

    @FXML
    private void abrirInventario(ActionEvent event) {
        abrirModuloInventario(event);
    }

    @FXML
    private void abrirHistorial(ActionEvent event) {
        abrirModuloHistorial(event);
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

    private void abrirModuloVentas(ActionEvent event) {
        try {
            // Cargar el FXML de ventas
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventas.fxml"));
            Parent root = loader.load();
            
            // Obtener el controlador de ventas
            VentasController ventasController = loader.getController();
            // Si necesitas pasar el usuario a ventas, puedes hacerlo aquí
            // ventasController.setUsuario(usuario);
            
            Stage ventasStage = new Stage();
            ventasStage.setScene(new Scene(root));
            ventasStage.setTitle("Gestión Hotel - Módulo de Ventas");
            ventasStage.setMinWidth(1200);
            ventasStage.setMinHeight(800);
            ventasStage.show();
            
            // Opcional: Cerrar la ventana actual del menú
            // Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // currentStage.close();
            
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
    
}