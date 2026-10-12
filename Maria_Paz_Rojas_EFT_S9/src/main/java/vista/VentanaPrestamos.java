package vista;

import controlador.PrestamoControlador;
import modelo.Estudiante;
import modelo.Libro;
import modelo.Prestamo;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Ventana de préstamos y devoluciones.
 * El préstamo y la devolución se ejecutan en un hilo independiente (ver PrestamoControlador),
 * por lo que la ventana sigue respondiendo mientras se procesan.
 */
public class VentanaPrestamos extends JFrame {

    private final JComboBox<Estudiante> cmbEstudiante = new JComboBox<>();
    private final JComboBox<Libro> cmbLibro = new JComboBox<>();
    private final JLabel lblFechaPrestamo = new JLabel();
    private final JLabel lblVencimiento = new JLabel();
    private final JButton btnPrestar = new JButton("Prestar");
    private final JButton btnDevolver = new JButton("Devolver seleccionado");
    private final JLabel lblEstado = new JLabel(" ");
    private final JTable tblPrestamos = new JTable();
    private DefaultTableModel model;

    private final PrestamoControlador controlador = new PrestamoControlador();

    // Cantidad de préstamos/devoluciones que se están procesando en segundo plano.
    // Solo se modifica desde el hilo de Swing, por eso no necesita sincronización.
    private int operacionesEnCurso = 0;

    public VentanaPrestamos(Usuario usuario) {
        setTitle("Biblioteca Escolar - Préstamos y devoluciones");
        setSize(850, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Formulario
        JPanel formulario = new JPanel(new GridLayout(4, 2, 5, 5));
        formulario.add(new JLabel("Estudiante:"));
        formulario.add(cmbEstudiante);
        formulario.add(new JLabel("Libro:"));
        formulario.add(cmbLibro);
        formulario.add(new JLabel("Fecha de préstamo:"));
        formulario.add(lblFechaPrestamo);
        formulario.add(new JLabel("Fecha de vencimiento (" + Prestamo.DIAS_PRESTAMO + " días):"));
        formulario.add(lblVencimiento);

        JPanel botones = new JPanel();
        botones.add(btnPrestar);
        botones.add(btnDevolver);

        JPanel superior = new JPanel(new BorderLayout());
        superior.add(new BarraNavegacion(this, usuario), BorderLayout.NORTH);
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);

        JPanel general = new JPanel(new BorderLayout(10, 10));
        general.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        general.add(superior, BorderLayout.NORTH);
        general.add(new JScrollPane(tblPrestamos), BorderLayout.CENTER);
        general.add(lblEstado, BorderLayout.SOUTH);
        setContentPane(general);

        inicializarTabla();
        inicializarBotones();
        actualizarDatos();
    }

    private void inicializarTabla() {
        model = UtilVista.crearModeloTabla("ID", "Estudiante", "Libro", "Fecha préstamo", "Vencimiento", "Estado");
        tblPrestamos.setModel(model);
        tblPrestamos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UtilVista.ocultarColumna(tblPrestamos, 0);
    }

    private void inicializarBotones() {
        btnPrestar.addActionListener(e -> prestarLibro());
        btnDevolver.addActionListener(e -> devolverLibro());
    }

    // Carga los estudiantes y libros desde la base de datos en los combos,
    // manteniendo seleccionados los que estaban elegidos
    private void cargarCombos() {
        Estudiante estudianteSeleccionado = (Estudiante) cmbEstudiante.getSelectedItem();
        Libro libroSeleccionado = (Libro) cmbLibro.getSelectedItem();

        cmbEstudiante.removeAllItems();
        cmbLibro.removeAllItems();
        try {
            for (Estudiante es : controlador.listarEstudiantes()) {
                cmbEstudiante.addItem(es);
            }
            for (Libro l : controlador.listarLibros()) {
                cmbLibro.addItem(l);
            }
        } catch (SQLException e) {
            UtilVista.mostrarErrorSQL(this, "cargar los estudiantes y libros", e);
        }

        if (estudianteSeleccionado != null) {
            seleccionarEstudiante(estudianteSeleccionado.getId());
        }
        if (libroSeleccionado != null) {
            seleccionarLibro(libroSeleccionado.getId());
        }
    }

    // Carga los préstamos en la tabla
    private void cargarTabla() {
        model.setRowCount(0);
        try {
            for (Prestamo p : controlador.listarPrestamos()) {
                model.addRow(new Object[]{
                        p.getId(),
                        p.getNombreEstudiante(),
                        p.getTituloLibro(),
                        p.getFechaPrestamo().format(UtilVista.FORMATO_FECHA),
                        p.getFechaDevolucion().format(UtilVista.FORMATO_FECHA),
                        p.getEstado()
                });
            }
        } catch (SQLException e) {
            UtilVista.mostrarErrorSQL(this, "cargar los préstamos", e);
        }
    }

    // Muestra la fecha de hoy y la fecha de vencimiento calculada automáticamente
    private void mostrarFechas() {
        LocalDate hoy = LocalDate.now();
        lblFechaPrestamo.setText(hoy.format(UtilVista.FORMATO_FECHA));
        lblVencimiento.setText(Prestamo.calcularVencimiento(hoy).format(UtilVista.FORMATO_FECHA));
    }

    private void prestarLibro() {
        Estudiante estudiante = (Estudiante) cmbEstudiante.getSelectedItem();
        Libro libro = (Libro) cmbLibro.getSelectedItem();
        if (estudiante == null || libro == null) {
            UtilVista.mostrarAdvertencia(this, "Debes seleccionar un estudiante y un libro.");
            return;
        }

        iniciarOperacion();
        // se ejecuta en otro hilo; al terminar se llama a finalizarOperacion en el hilo de Swing
        controlador.prestarLibro(estudiante.getId(), libro.getId(), this::finalizarOperacion);
    }

    private void devolverLibro() {
        int fila = tblPrestamos.getSelectedRow();
        if (fila == -1) {
            UtilVista.mostrarAdvertencia(this, "Selecciona en la tabla el préstamo que se devuelve.");
            return;
        }
        if (model.getValueAt(fila, 5).toString().equals("DEVUELTO")) {
            UtilVista.mostrarAdvertencia(this, "Ese préstamo ya fue devuelto.");
            return;
        }

        int idPrestamo = Integer.parseInt(model.getValueAt(fila, 0).toString()); // ID está en la columna 0
        iniciarOperacion();
        controlador.devolverLibro(idPrestamo, this::finalizarOperacion);
    }

    // Se llama antes de lanzar el hilo: muestra que hay operaciones en proceso
    private void iniciarOperacion() {
        operacionesEnCurso++;
        lblEstado.setText("Procesando... operaciones en curso: " + operacionesEnCurso
                + " (la ventana sigue disponible)");
    }

    // Se llama cuando el hilo termina (desde SwingUtilities.invokeLater)
    private void finalizarOperacion(boolean exito, String mensaje) {
        operacionesEnCurso--;
        lblEstado.setText(operacionesEnCurso > 0
                ? "Procesando... operaciones en curso: " + operacionesEnCurso
                : " ");
        actualizarDatos();

        if (exito) {
            UtilVista.mostrarMensaje(this, mensaje);
        } else {
            UtilVista.mostrarAdvertencia(this, mensaje);
        }
    }

    // Después de cada operación se refrescan los combos (stock), la tabla y las fechas
    private void actualizarDatos() {
        cargarCombos();
        cargarTabla();
        mostrarFechas();
    }

    // Busca en el combo el estudiante con ese id y lo selecciona
    private void seleccionarEstudiante(int id) {
        for (int i = 0; i < cmbEstudiante.getItemCount(); i++) {
            if (cmbEstudiante.getItemAt(i).getId() == id) {
                cmbEstudiante.setSelectedIndex(i);
                return;
            }
        }
    }

    // Busca en el combo el libro con ese id y lo selecciona
    private void seleccionarLibro(int id) {
        for (int i = 0; i < cmbLibro.getItemCount(); i++) {
            if (cmbLibro.getItemAt(i).getId() == id) {
                cmbLibro.setSelectedIndex(i);
                return;
            }
        }
    }
}
