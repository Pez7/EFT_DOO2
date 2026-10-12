package vista;

import controlador.ReporteControlador;
import modelo.Estudiante;
import modelo.LibroMasPrestado;
import modelo.Prestamo;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

/**
 * Ventana de reportes: libros más prestados, historial por estudiante
 * y listado de libros actualmente en préstamo.
 */
public class VentanaReportes extends JFrame {

    private static final String LIBROS_MAS_PRESTADOS = "Libros más prestados";
    private static final String HISTORIAL_ESTUDIANTE = "Historial por estudiante";
    private static final String LIBROS_EN_PRESTAMO = "Libros actualmente en préstamo";

    private final JComboBox<String> cmbReporte = new JComboBox<>(
            new String[]{LIBROS_MAS_PRESTADOS, HISTORIAL_ESTUDIANTE, LIBROS_EN_PRESTAMO});
    private final JComboBox<Estudiante> cmbEstudiante = new JComboBox<>();
    private final JButton btnGenerar = new JButton("Generar");
    private final JTable tblReporte = new JTable();

    private final ReporteControlador controlador = new ReporteControlador();

    public VentanaReportes(Usuario usuario) {
        setTitle("Biblioteca Escolar - Reportes");
        setSize(850, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Opciones del reporte
        JPanel formulario = new JPanel(new GridLayout(2, 2, 5, 5));
        formulario.add(new JLabel("Reporte:"));
        formulario.add(cmbReporte);
        formulario.add(new JLabel("Estudiante (solo historial):"));
        formulario.add(cmbEstudiante);

        JPanel botones = new JPanel();
        botones.add(btnGenerar);

        JPanel superior = new JPanel(new BorderLayout());
        superior.add(new BarraNavegacion(this, usuario), BorderLayout.NORTH);
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);

        JPanel general = new JPanel(new BorderLayout(10, 10));
        general.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        general.add(superior, BorderLayout.NORTH);
        general.add(new JScrollPane(tblReporte), BorderLayout.CENTER);
        setContentPane(general);

        cargarEstudiantes();
        // el combo de estudiantes solo se usa en el historial
        cmbReporte.addActionListener(e ->
                cmbEstudiante.setEnabled(HISTORIAL_ESTUDIANTE.equals(cmbReporte.getSelectedItem())));
        cmbEstudiante.setEnabled(false);
        btnGenerar.addActionListener(e -> generarReporte());

        generarReporte();
    }

    // Carga los estudiantes desde la base de datos en el combo
    private void cargarEstudiantes() {
        cmbEstudiante.removeAllItems();
        try {
            for (Estudiante es : controlador.listarEstudiantes()) {
                cmbEstudiante.addItem(es);
            }
        } catch (SQLException e) {
            UtilVista.mostrarErrorSQL(this, "cargar los estudiantes", e);
        }
    }

    private void generarReporte() {
        String reporte = (String) cmbReporte.getSelectedItem();
        try {
            if (LIBROS_MAS_PRESTADOS.equals(reporte)) {
                mostrarLibrosMasPrestados();
            } else if (HISTORIAL_ESTUDIANTE.equals(reporte)) {
                mostrarHistorialEstudiante();
            } else {
                mostrarLibrosEnPrestamo();
            }
        } catch (SQLException e) {
            UtilVista.mostrarErrorSQL(this, "generar el reporte", e);
        }
    }

    private void mostrarLibrosMasPrestados() throws SQLException {
        DefaultTableModel model = UtilVista.crearModeloTabla("Título", "Autor", "Veces prestado");
        for (LibroMasPrestado l : controlador.librosMasPrestados()) {
            model.addRow(new Object[]{l.getTitulo(), l.getAutor(), l.getCantidadPrestamos()});
        }
        tblReporte.setModel(model);
    }

    private void mostrarHistorialEstudiante() throws SQLException {
        Estudiante estudiante = (Estudiante) cmbEstudiante.getSelectedItem();
        if (estudiante == null) {
            UtilVista.mostrarAdvertencia(this, "Debes seleccionar un estudiante.");
            return;
        }
        DefaultTableModel model = UtilVista.crearModeloTabla("Libro", "Fecha préstamo", "Vencimiento", "Estado");
        for (Prestamo p : controlador.historialPorEstudiante(estudiante.getId())) {
            model.addRow(new Object[]{
                    p.getTituloLibro(),
                    p.getFechaPrestamo().format(UtilVista.FORMATO_FECHA),
                    p.getFechaDevolucion().format(UtilVista.FORMATO_FECHA),
                    p.getEstado()
            });
        }
        tblReporte.setModel(model);
    }

    private void mostrarLibrosEnPrestamo() throws SQLException {
        DefaultTableModel model = UtilVista.crearModeloTabla("Libro", "Estudiante", "Fecha préstamo", "Vencimiento", "Estado");
        for (Prestamo p : controlador.librosEnPrestamo()) {
            model.addRow(new Object[]{
                    p.getTituloLibro(),
                    p.getNombreEstudiante(),
                    p.getFechaPrestamo().format(UtilVista.FORMATO_FECHA),
                    p.getFechaDevolucion().format(UtilVista.FORMATO_FECHA),
                    p.getEstado()
            });
        }
        tblReporte.setModel(model);
    }
}
