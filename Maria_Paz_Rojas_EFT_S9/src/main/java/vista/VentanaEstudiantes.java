package vista;

import controlador.EstudianteControlador;
import modelo.Estudiante;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

/**
 * Ventana de gestión de estudiantes: registrar, editar, eliminar y listar.
 * Al registrar un estudiante también se crea su usuario (rol estudiante) para ingresar al sistema.
 */
public class VentanaEstudiantes extends JFrame {

    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtRut = new JTextField(12);
    private final JTextField txtCurso = new JTextField(20);
    private final JTextField txtCorreo = new JTextField(20);
    private final JPasswordField txtContrasena = new JPasswordField(20);
    private final JButton btnAgregar = new JButton("Agregar");
    private final JButton btnEditar = new JButton("Editar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnLimpiar = new JButton("Limpiar");
    private final JTable tblEstudiantes = new JTable();
    private DefaultTableModel model;

    private final EstudianteControlador controlador = new EstudianteControlador();
    private int filaSeleccionada = -1;

    public VentanaEstudiantes(Usuario usuario) {
        setTitle("Biblioteca Escolar - Estudiantes");
        setSize(750, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Formulario
        JPanel formulario = new JPanel(new GridLayout(5, 2, 5, 5));
        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);
        formulario.add(new JLabel("RUT (ej. 12345678-9):"));
        formulario.add(txtRut);
        formulario.add(new JLabel("Curso:"));
        formulario.add(txtCurso);
        formulario.add(new JLabel("Correo:"));
        formulario.add(txtCorreo);
        formulario.add(new JLabel("Contraseña (solo al registrar):"));
        formulario.add(txtContrasena);

        JPanel botones = new JPanel();
        botones.add(btnAgregar);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        JPanel superior = new JPanel(new BorderLayout());
        superior.add(new BarraNavegacion(this, usuario), BorderLayout.NORTH);
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);

        JPanel general = new JPanel(new BorderLayout(10, 10));
        general.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        general.add(superior, BorderLayout.NORTH);
        general.add(new JScrollPane(tblEstudiantes), BorderLayout.CENTER);
        setContentPane(general);

        inicializarTabla();
        inicializarBotones();
        cargarTabla();
    }

    private void inicializarTabla() {
        model = UtilVista.crearModeloTabla("ID", "Nombre", "RUT", "Curso", "Correo");
        tblEstudiantes.setModel(model);
        tblEstudiantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UtilVista.ocultarColumna(tblEstudiantes, 0);

        // al hacer clic en una fila se cargan sus datos en el formulario
        tblEstudiantes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tblEstudiantes.getSelectedRow();
                if (fila >= 0) {
                    filaSeleccionada = fila;
                    txtNombre.setText(model.getValueAt(fila, 1).toString());
                    txtRut.setText(model.getValueAt(fila, 2).toString());
                    txtCurso.setText(model.getValueAt(fila, 3).toString());
                    txtCorreo.setText(model.getValueAt(fila, 4).toString());
                    txtContrasena.setText("");
                }
            }
        });
    }

    private void inicializarBotones() {
        btnAgregar.addActionListener(e -> agregarEstudiante());
        btnEditar.addActionListener(e -> editarEstudiante());
        btnEliminar.addActionListener(e -> eliminarEstudiante());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
    }

    // Carga los estudiantes desde la base de datos en la tabla
    private void cargarTabla() {
        model.setRowCount(0);
        try {
            for (Estudiante es : controlador.listarEstudiantes()) {
                model.addRow(new Object[]{es.getId(), es.getNombre(), es.getRut(), es.getCurso(), es.getCorreo()});
            }
        } catch (SQLException e) {
            UtilVista.mostrarErrorSQL(this, "cargar los estudiantes", e);
        }
    }

    // Retorna true si el formulario es válido
    private boolean validarFormulario() {
        String nombre = txtNombre.getText().trim();
        String rut = txtRut.getText().trim();
        String curso = txtCurso.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (nombre.isEmpty() || rut.isEmpty() || curso.isEmpty() || correo.isEmpty()) {
            UtilVista.mostrarAdvertencia(this, "Debes completar nombre, RUT, curso y correo.");
            return false;
        }
        if (nombre.length() > 100 || correo.length() > 100) {
            UtilVista.mostrarAdvertencia(this, "El nombre y el correo no pueden tener más de 100 caracteres.");
            return false;
        }
        if (UtilVista.contieneNumeros(nombre)) {
            UtilVista.mostrarAdvertencia(this, "El nombre del estudiante no puede contener números.");
            return false;
        }
        if (rut.length() > 12) {
            UtilVista.mostrarAdvertencia(this, "El RUT no puede tener más de 12 caracteres.");
            return false;
        }
        if (curso.length() > 20) {
            UtilVista.mostrarAdvertencia(this, "El curso no puede tener más de 20 caracteres.");
            return false;
        }
        return true;
    }

    // Construye un Estudiante con los datos del formulario
    private Estudiante leerFormulario() {
        return new Estudiante(
                txtNombre.getText().trim(),
                txtRut.getText().trim(),
                txtCurso.getText().trim(),
                txtCorreo.getText().trim()
        );
    }

    private void agregarEstudiante() {
        if (!validarFormulario()) return;

        String contrasena = new String(txtContrasena.getPassword()).trim();
        if (contrasena.isEmpty()) {
            UtilVista.mostrarAdvertencia(this, "Debes ingresar una contraseña para que el estudiante pueda ingresar al sistema.");
            return;
        }
        if (contrasena.length() > 100) {
            UtilVista.mostrarAdvertencia(this, "La contraseña no puede tener más de 100 caracteres.");
            return;
        }

        try {
            controlador.registrar(leerFormulario(), contrasena);
            UtilVista.mostrarMensaje(this, "Estudiante registrado correctamente.\nYa puede ingresar al sistema con su RUT y contraseña.");
            cargarTabla();
            limpiarFormulario();
        } catch (SQLException e) {
            UtilVista.mostrarErrorSQL(this, "registrar el estudiante", e);
        }
    }

    private void editarEstudiante() {
        if (filaSeleccionada == -1) {
            UtilVista.mostrarAdvertencia(this, "Selecciona un estudiante para editar.");
            return;
        }
        if (!validarFormulario()) return;

        Estudiante estudiante = leerFormulario();
        estudiante.setId(Integer.parseInt(model.getValueAt(filaSeleccionada, 0).toString())); // ID está en la columna 0
        try {
            controlador.actualizar(estudiante);
            UtilVista.mostrarMensaje(this, "Estudiante actualizado correctamente.");
            cargarTabla();
            limpiarFormulario();
        } catch (SQLException e) {
            UtilVista.mostrarErrorSQL(this, "actualizar el estudiante", e);
        }
    }

    private void eliminarEstudiante() {
        if (filaSeleccionada == -1) {
            UtilVista.mostrarAdvertencia(this, "Selecciona un estudiante para eliminar.");
            return;
        }
        if (!UtilVista.confirmar(this, "¿Estás seguro de que deseas eliminar este estudiante?")) return;

        int id = Integer.parseInt(model.getValueAt(filaSeleccionada, 0).toString());
        try {
            controlador.eliminar(id);
            UtilVista.mostrarMensaje(this, "Estudiante eliminado correctamente.");
            cargarTabla();
            limpiarFormulario();
        } catch (SQLException e) {
            UtilVista.mostrarErrorSQL(this, "eliminar el estudiante", e);
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtRut.setText("");
        txtCurso.setText("");
        txtCorreo.setText("");
        txtContrasena.setText("");
        filaSeleccionada = -1;
        tblEstudiantes.clearSelection();
    }
}
