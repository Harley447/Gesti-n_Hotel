package com.mycompany.gestion_hotel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Gestion_Hotel extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        
        // Configurar la ventana de login
        primaryStage.setTitle("Hotel Terrazas - Login");
        primaryStage.setScene(new Scene(root, 800, 600));
        
        // Deshabilitar maximizar y redimensionar
        primaryStage.setResizable(false);
        
        // Opcional: Centrar la ventana en la pantalla
        primaryStage.centerOnScreen();
        
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}