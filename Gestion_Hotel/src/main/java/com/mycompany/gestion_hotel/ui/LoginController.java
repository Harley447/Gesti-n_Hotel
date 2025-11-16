package com.mycompany.gestion_hotel.ui;

import com.mycompany.gestion_hotel.conexion.ConexionBD;
import com.mycompany.gestion_hotel.dao.UsuarioDAO;
import com.mycompany.gestion_hotel.modelo.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField txtCorreo;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private Button btnLogin;
    @FXML
    private Label lblMensaje;
    @FXML
    private ImageView backgroundImage;
    @FXML
    private AnchorPane anchorPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarImagenFondo();
    }

    private void cargarImagenFondo() {
        try {
            System.out.println("🔍 Cargando imagen de fondo...");
            
            URL imageUrl = getClass().getResource("/imagenes/Login.png");
            if (imageUrl != null) {
                Image image = new Image(imageUrl.toString());
                backgroundImage.setImage(image);
                System.out.println("✅ Imagen de fondo cargada exitosamente");
            } else {
                System.err.println("❌ No se pudo cargar la imagen de fondo");
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String correo = txtCorreo.getText().trim();
        String contrasena = txtContrasena.getText().trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            lblMensaje.setText("Por favor, complete todos los campos.");
            return;
        }

        ConexionBD conexionBD = new ConexionBD();
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO(conexionBD);
            Usuario usuario = usuarioDAO.autenticar(correo, contrasena);

            if (usuario != null) {
                lblMensaje.setText("✓ Bienvenido " + usuario.getNombre());
                lblMensaje.setStyle("-fx-text-fill: #27ae60;");

                boolean menuAbierto = abrirMenu(usuario);

                if (menuAbierto) {
                    Stage stage = (Stage) btnLogin.getScene().getWindow();
                    stage.close();
                } else {
                    lblMensaje.setText("Error: No se pudo abrir el menú.");
                    lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
                }
            } else {
                lblMensaje.setText("✗ Usuario o contraseña incorrectos.");
                lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            }

        } catch (Exception e) {
            e.printStackTrace();
            lblMensaje.setText("Error al conectar con la base de datos.");
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
        } finally {
            conexionBD.closeConnection();
        }
    }

    private boolean abrirMenu(Usuario usuario) {
        String fxmlPath;

        if ("Administradora".equalsIgnoreCase(usuario.getRol())) {
            fxmlPath = "/fxml/menu_administrador.fxml";
        } else if ("Recepcionista".equalsIgnoreCase(usuario.getRol())) {
            fxmlPath = "/fxml/menu_recepcionista.fxml";
        } else {
            lblMensaje.setText("Rol no reconocido: " + usuario.getRol());
            return false;
        }

        try {
            if (getClass().getResource(fxmlPath) == null) {
                lblMensaje.setText("Error: No se encuentra el archivo " + fxmlPath);
                return false;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof MenuAdministradorController adminCtrl) {
                adminCtrl.setUsuario(usuario);
            } else if (controller instanceof MenuRecepcionistaController recepCtrl) {
                recepCtrl.setUsuario(usuario);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestión Hotel - " + usuario.getRol());
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
            stage.show();
            
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            lblMensaje.setText("Error al abrir el menú de " + usuario.getRol());
            return false;
        }
    }
}