package vista;

import controlador.UsuarioControlador;
import modelo.RolUsuario;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

/**
 * Ventana de gestión de usuarios del sistema: registrar, editar, eliminar y listar.
 */
public class VentanaUsuarios extends JFrame {

    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtRut = new JTextField(12);
    private final JTextField txtCorreo = new JTextField(20);
    private final JPasswordField txtContrasena = new JPasswordField(20);
    private final JComboBox<RolUsuario> cmbRol = new JComboBox<>(RolUsuario.values());
    private final JButton btnAgregar = new JButton("Agregar");
    private final JButton btnEditar = new JButton("Editar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnLimpiar = new JButton("Limpiar");
    private final JTable tblUsuarios = new JTable();
    private DefaultTableModel model;

    private final UsuarioControlador controlador = new UsuarioControlador();
    private int filaSeleccionada = -1;

    public VentanaUsuarios(Usuario usuario) {
        setTitle("Biblioteca Escolar - Usuarios");
        setSize(750, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Formulario
        JPanel formulario = new JPanel(new GridLayout(5, 2, 5, 5));
        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);
        formulario.add(new JLabel("RUT (ej. 12345678-9):"));
        formulario.add(txtRut);
        formulario.add(new JLabel("Correo:"));
        formulario.add(txtCorreo);
        formulario.add(new JLabel("Contraseña:"));
        formulario.add(txtContrasena);
        formulario.add(new JLabel("Rol:"));
        formulario.add(cmbRol);

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
        general.add(new JScrollPane(tblUsuarios), BorderLayout.CENTER);
        setContentPane(general);

        inicializarTabla();
        inicializarBotones();
        cargarTabla();
    }

    private void inicializarTabla() {
        model = UtilVista.crearModeloTabla("ID", "Nombre", "RUT", "Correo", "Rol", "Contraseña");
        tblUsuarios.setModel(model);
        tblUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UtilVista.ocultarColumna(tblUsuarios, 0);
        UtilVista.ocultarColumna(tblUsuarios, 5); // la contraseña no se muestra

        // al hacer clic en una fila se cargan sus datos en el formulario
        tblUsuarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tblUsuarios.getSelectedRow();
                if (fila >= 0) {
                    filaSeleccionada = fila;
                    txtNombre.setText(model.getValueAt(fila, 1).toString());
                    txtRut.setText(model.getValueAt(fila, 2).toString());
                    txtCorreo.setText(model.getValueAt(fila, 3).toString());
                    cmbRol.setSelectedItem(RolUsuario.valueOf(model.getValueAt(fila, 4).toString()));
                    txtContrasena.setText(model.getValueAt(fila, 5).toString());
                }
            }
        });
    }

    private void inicializarBotones() {
        btnAgregar.addActionListener(e -> agregarUsuario());
        btnEditar.addActionListener(e -> editarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
    }

    // Carga los usuarios desde la base de datos en la tabla
    private void cargarTabla() {
        model.setRowCount(0);
        try {
            for (Usuario u : controlador.listarUsuarios()) {
                model.addRow(new Object[]{u.getId(), u.getNombre(), u.getRut(), u.getCorreo(), u.getRol(), u.getContrasena()});
            }
        } catch (SQLException e) {
            UtilVista.mostrarErrorSQL(this, "cargar los usuarios", e);
        }
    }

    // Retorna true si el formulario es válido
    private boolean validarFormulario() {
        String nombre = txtNombre.getText().trim();
        String rut = txtRut.getText().trim();
        String correo = txtCorreo.getText().trim();
        String contrasena = new String(txtContrasena.getPassword()).trim();

        if (nombre.isEmpty() || rut.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            UtilVista.mostrarAdvertencia(this, "Debes completar todos los campos del usuario.");
            return false;
        }
        if (nombre.length() > 100 || correo.length() > 100 || contrasena.length() > 100) {
            UtilVista.mostrarAdvertencia(this, "El nombre, correo y contraseña no pueden tener más de 100 caracteres.");
            return false;
        }
        if (UtilVista.contieneNumeros(nombre)) {
            UtilVista.mostrarAdvertencia(this, "El nombre del usuario no puede contener números.");
            return false;
        }
        if (rut.length() > 12) {
            UtilVista.mostrarAdvertencia(this, "El RUT no puede tener más de 12 caracteres.");
            return false;
        }
        if (cmbRol.getSelectedItem() == null) {
            UtilVista.mostrarAdvertencia(this, "Debes seleccionar el rol del usuario.");
            return false;
        }
        return true;
    }

    // Construye un Usuario con los datos del formulario
    private Usuario leerFormulario() {
        return new Usuario(
                txtNombre.getText().trim(),
                txtRut.getText().trim(),
                txtCorreo.getText().trim(),
                new String(txtContrasena.getPassword()).trim(),
                (RolUsuario) cmbRol.getSelectedItem()
        );
    }

    private void agregarUsuario() {
        if (!validarFormulario()) return;

        try {
            controlador.registrar(leerFormulario());
            UtilVista.mostrarMensaje(this, "Usuario registrado correctamente.");
            cargarTabla();
            limpiarFormulario();
        } catch (SQLException e) {
            UtilVista.mostrarErrorSQL(this, "registrar el usuario", e);
        }
    }

    private void editarUsuario() {
        if (filaSeleccionada == -1) {
            UtilVista.mostrarAdvertencia(this, "Selecciona un usuario para editar.");
            return;
        }
        if (!validarFormulario()) return;

        Usuario usuario = leerFormulario();
        usuario.setId(Integer.parseInt(model.getValueAt(filaSeleccionada, 0).toString())); // ID está en la columna 0
        try {
            controlador.actualizar(usuario);
            UtilVista.mostrarMensaje(this, "Usuario actualizado correctamente.");
            cargarTabla();
            limpiarFormulario();
        } catch (SQLException e) {
            UtilVista.mostrarErrorSQL(this, "actualizar el usuario", e);
        }
    }

    private void eliminarUsuario() {
        if (filaSeleccionada == -1) {
            UtilVista.mostrarAdvertencia(this, "Selecciona un usuario para eliminar.");
            return;
        }
        if (!UtilVista.confirmar(this, "¿Estás seguro de que deseas eliminar este usuario?")) return;

        int id = Integer.parseInt(model.getValueAt(filaSeleccionada, 0).toString());
        try {
            controlador.eliminar(id);
            UtilVista.mostrarMensaje(this, "Usuario eliminado correctamente.");
            cargarTabla();
            limpiarFormulario();
        } catch (SQLException e) {
            UtilVista.mostrarErrorSQL(this, "eliminar el usuario", e);
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtRut.setText("");
        txtCorreo.setText("");
        txtContrasena.setText("");
        cmbRol.setSelectedIndex(0);
        filaSeleccionada = -1;
        tblUsuarios.clearSelection();
    }
}
