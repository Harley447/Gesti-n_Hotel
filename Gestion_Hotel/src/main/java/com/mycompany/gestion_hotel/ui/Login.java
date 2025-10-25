package com.mycompany.gestion_hotel.ui;

import com.mycompany.gestion_hotel.dao.UsuarioDAO;
import com.mycompany.gestion_hotel.modelo.Usuario;
import javax.swing.*;

public class Login extends JFrame {

    private JTextField txtCorreo;
    private JPasswordField txtContrasena;
    private JButton btnLogin;

    public Login() {
        setTitle("Inicio de Sesión");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblCorreo = new JLabel("Usuario:");
        lblCorreo.setBounds(50, 50, 100, 25);
        add(lblCorreo);

        txtCorreo = new JTextField();
        txtCorreo.setBounds(150, 50, 180, 25);
        add(txtCorreo);

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setBounds(50, 90, 100, 25);
        add(lblContrasena);

        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(150, 90, 180, 25);
        add(txtContrasena);

        btnLogin = new JButton("Iniciar sesión");
        btnLogin.setBounds(130, 140, 120, 30);
        add(btnLogin);

        btnLogin.addActionListener(e -> autenticar());
    }

    private void autenticar() {
        String correo = txtCorreo.getText();
        String contrasena = new String(txtContrasena.getPassword());

        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuario = dao.autenticar(correo, contrasena);

        if (usuario != null) {
            JOptionPane.showMessageDialog(this, "Bienvenido " + usuario.getNombre() + " (" + usuario.getRol() + ")");
            // Aquí puedes abrir la interfaz principal según el rol
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
