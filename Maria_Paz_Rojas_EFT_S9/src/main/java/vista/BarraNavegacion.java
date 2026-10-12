package vista;

import modelo.Usuario;

import javax.swing.*;

/**
 * Barra de herramientas para navegar entre las ventanas del sistema.
 * Muestra solo las opciones permitidas según el rol del usuario:
 * - bibliotecario: gestión total (libros, estudiantes, usuarios, préstamos y reportes)
 * - estudiante: consulta de libros y préstamos/devoluciones
 * Al cambiar de ventana se abre la nueva y se cierra la actual.
 * "Cerrar sesión" vuelve a la ventana de ingreso.
 */
public class BarraNavegacion extends JToolBar {

    public BarraNavegacion(JFrame ventanaActual, Usuario usuario) {
        setFloatable(false);

        JButton btnLibros = new JButton("Libros");
        JButton btnPrestamos = new JButton("Préstamos");
        add(btnLibros);

        btnLibros.addActionListener(e -> {
            new VentanaLibros(usuario).setVisible(true);
            ventanaActual.dispose();
        });
        btnPrestamos.addActionListener(e -> {
            new VentanaPrestamos(usuario).setVisible(true);
            ventanaActual.dispose();
        });

        if (usuario.esBibliotecario()) {
            JButton btnEstudiantes = new JButton("Estudiantes");
            JButton btnUsuarios = new JButton("Usuarios");
            JButton btnReportes = new JButton("Reportes");
            add(btnEstudiantes);
            add(btnUsuarios);
            add(btnPrestamos);
            add(btnReportes);

            btnEstudiantes.addActionListener(e -> {
                new VentanaEstudiantes(usuario).setVisible(true);
                ventanaActual.dispose();
            });
            btnUsuarios.addActionListener(e -> {
                new VentanaUsuarios(usuario).setVisible(true);
                ventanaActual.dispose();
            });
            btnReportes.addActionListener(e -> {
                new VentanaReportes(usuario).setVisible(true);
                ventanaActual.dispose();
            });
        } else {
            add(btnPrestamos);
        }

        // muestra quién está conectado y permite cerrar la sesión
        add(Box.createHorizontalGlue());
        add(new JLabel("Sesión: " + usuario.getDescripcion() + "  "));

        JButton btnCerrarSesion = new JButton("Cerrar sesión");
        add(btnCerrarSesion);
        btnCerrarSesion.addActionListener(e -> {
            new VentanaLogin().setVisible(true);
            ventanaActual.dispose();
        });
    }
}
