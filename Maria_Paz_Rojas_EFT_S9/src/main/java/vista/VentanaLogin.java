package vista;

import controlador.LoginControlador;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Ventana de autenticación: el usuario ingresa con su RUT y contraseña.
 * Según su rol se habilitan las funciones correspondientes.
 */
public class VentanaLogin extends JFrame {

    private final JTextField txtRut = new JTextField(15);
    private final JPasswordField txtContrasena = new JPasswordField(15);
    private final JButton btnIngresar = new JButton("Ingresar");

    private final LoginControlador controlador = new LoginControlador();

    public VentanaLogin() {
        setTitle("Biblioteca Escolar - Ingreso");
        setSize(350, 180);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Formulario
        JPanel formulario = new JPanel(new GridLayout(2, 2, 5, 5));
        formulario.add(new JLabel("RUT:"));
        formulario.add(txtRut);
        formulario.add(new JLabel("Contraseña:"));
        formulario.add(txtContrasena);

        JPanel botones = new JPanel();
        botones.add(btnIngresar);

        JPanel general = new JPanel(new BorderLayout(10, 10));
        general.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        general.add(new JLabel("Ingresa tus datos (RUT con guion, ej. 12345678-9)"), BorderLayout.NORTH);
        general.add(formulario, BorderLayout.CENTER);
        general.add(botones, BorderLayout.SOUTH);
        setContentPane(general);

        btnIngresar.addActionListener(e -> ingresar());
        // al presionar Enter se ejecuta "Ingresar"
        getRootPane().setDefaultButton(btnIngresar);
    }

    private void ingresar() {
        String rut = txtRut.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());
        if (rut.isEmpty() || contrasena.isEmpty()) {
            UtilVista.mostrarAdvertencia(this, "Debes ingresar tu RUT y contraseña.");
            return;
        }

        try {
            Usuario usuario = controlador.autenticar(rut, contrasena);
            if (usuario == null) {
                UtilVista.mostrarAdvertencia(this, "RUT o contraseña incorrectos.");
                txtContrasena.setText("");
                return;
            }
            new VentanaLibros(usuario).setVisible(true);
            dispose();
        } catch (SQLException e) {
            UtilVista.mostrarErrorSQL(this, "validar el usuario", e);
        }
    }
}
