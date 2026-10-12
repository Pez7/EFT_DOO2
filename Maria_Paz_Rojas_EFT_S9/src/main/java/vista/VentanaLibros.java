package vista;

import controlador.LibroControlador;
import modelo.Categoria;
import modelo.Libro;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

/**
 * Ventana de gestión de libros: registrar, editar, eliminar y listar.
 * El bibliotecario tiene gestión total; el estudiante solo puede consultar el listado.
 */
public class VentanaLibros extends JFrame {

    private final JTextField txtTitulo = new JTextField(20);
    private final JTextField txtAutor = new JTextField(20);
    private final JTextField txtIsbn = new JTextField(20);
    private final JTextField txtEditorial = new JTextField(20);
    private final JTextField txtStock = new JTextField(5);
    private final JComboBox<Categoria> cmbCategoria = new JComboBox<>();
    private final JButton btnAgregar = new JButton("Agregar");
    private final JButton btnEditar = new JButton("Editar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnLimpiar = new JButton("Limpiar");
    private final JTable tblLibros = new JTable();
    private DefaultTableModel model;

    private final LibroControlador controlador = new LibroControlador();
    private final Usuario usuario;
    private int filaSeleccionada = -1;

    public VentanaLibros(Usuario usuario) {
        this.usuario = usuario;
        setTitle("Biblioteca Escolar - Libros");
        setSize(850, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel superior = new JPanel(new BorderLayout());
        superior.add(new BarraNavegacion(this, usuario), BorderLayout.NORTH);

        // El formulario y los botones solo se muestran al bibliotecario
        if (usuario.esBibliotecario()) {
            JPanel formulario = new JPanel(new GridLayout(6, 2, 5, 5));
            formulario.add(new JLabel("Título:"));
            formulario.add(txtTitulo);
            formulario.add(new JLabel("Autor:"));
            formulario.add(txtAutor);
            formulario.add(new JLabel("ISBN:"));
            formulario.add(txtIsbn);
            formulario.add(new JLabel("Editorial:"));
            formulario.add(txtEditorial);
            formulario.add(new JLabel("Stock:"));
            formulario.add(txtStock);
            formulario.add(new JLabel("Categoría:"));
            formulario.add(cmbCategoria);

            JPanel botones = new JPanel();
            botones.add(btnAgregar);
            botones.add(btnEditar);
            botones.add(btnEliminar);
            botones.add(btnLimpiar);

            superior.add(formulario, BorderLayout.CENTER);
            superior.add(botones, BorderLayout.SOUTH);
        } else {
            superior.add(new JLabel("Consulta de libros disponibles"), BorderLayout.CENTER);
        }

        JPanel general = new JPanel(new BorderLayout(10, 10));
        general.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        general.add(superior, BorderLayout.NORTH);
        general.add(new JScrollPane(tblLibros), BorderLayout.CENTER);
        setContentPane(general);

        inicializarTabla();
        if (usuario.esBibliotecario()) {
            inicializarBotones();
            cargarCategorias();
        }
        cargarTabla();
    }

    private void inicializarTabla() {
        model = UtilVista.crearModeloTabla("ID", "Título", "Autor", "ISBN", "Editorial", "Stock", "Categoría", "ID Categoría");
        tblLibros.setModel(model);
        tblLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UtilVista.ocultarColumna(tblLibros, 0);
        UtilVista.ocultarColumna(tblLibros, 7);

        // al hacer clic en una fila se cargan sus datos en el formulario (solo bibliotecario)
        if (usuario.esBibliotecario()) {
            tblLibros.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int fila = tblLibros.getSelectedRow();
                    if (fila >= 0) {
                        filaSeleccionada = fila;
                        txtTitulo.setText(model.getValueAt(fila, 1).toString());
                        txtAutor.setText(model.getValueAt(fila, 2).toString());
                        txtIsbn.setText(model.getValueAt(fila, 3).toString());
                        txtEditorial.setText(model.getValueAt(fila, 4).toString());
                        txtStock.setText(model.getValueAt(fila, 5).toString());
                        seleccionarCategoria(Integer.parseInt(model.getValueAt(fila, 7).toString()));
                    }
                }
            });
        }
    }

    private void inicializarBotones() {
        btnAgregar.addActionListener(e -> agregarLibro());
        btnEditar.addActionListener(e -> editarLibro());
        btnEliminar.addActionListener(e -> eliminarLibro());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
    }

    // Carga las categorías desde la base de datos en el combo
    private void cargarCategorias() {
        cmbCategoria.removeAllItems();
        try {
            for (Categoria c : controlador.listarCategorias()) {
                cmbCategoria.addItem(c);
            }
        } catch (SQLException e) {
            UtilVista.mostrarErrorSQL(this, "cargar las categorías", e);
        }
    }

    // Carga los libros desde la base de datos en la tabla
    private void cargarTabla() {
        model.setRowCount(0);
        try {
            for (Libro l : controlador.listarLibros()) {
                model.addRow(new Object[]{
                        l.getId(), l.getTitulo(), l.getAutor(), l.getIsbn(),
                        l.getEditorial(), l.getStock(), l.getNombreCategoria(), l.getIdCategoria()
                });
            }
        } catch (SQLException e) {
            UtilVista.mostrarErrorSQL(this, "cargar los libros", e);
        }
    }

    // Retorna true si el formulario es válido
    private boolean validarFormulario() {
        String titulo = txtTitulo.getText().trim();
        String autor = txtAutor.getText().trim();
        String isbn = txtIsbn.getText().trim();
        String editorial = txtEditorial.getText().trim();
        String stock = txtStock.getText().trim();

        if (titulo.isEmpty() || autor.isEmpty() || isbn.isEmpty() || editorial.isEmpty() || stock.isEmpty()) {
            UtilVista.mostrarAdvertencia(this, "Debes completar todos los campos del libro.");
            return false;
        }
        if (titulo.length() > 200) {
            UtilVista.mostrarAdvertencia(this, "El título no puede tener más de 200 caracteres.");
            return false;
        }
        if (autor.length() > 100 || editorial.length() > 100) {
            UtilVista.mostrarAdvertencia(this, "El autor y la editorial no pueden tener más de 100 caracteres.");
            return false;
        }
        if (isbn.length() > 20) {
            UtilVista.mostrarAdvertencia(this, "El ISBN no puede tener más de 20 caracteres.");
            return false;
        }
        try {
            if (Integer.parseInt(stock) < 0) {
                UtilVista.mostrarAdvertencia(this, "El stock no puede ser negativo.");
                return false;
            }
        } catch (NumberFormatException e) {
            UtilVista.mostrarAdvertencia(this, "El stock debe ser un número entero.");
            return false;
        }
        if (cmbCategoria.getSelectedItem() == null) {
            UtilVista.mostrarAdvertencia(this, "Debes seleccionar una categoría.");
            return false;
        }
        return true;
    }

    // Construye un Libro con los datos del formulario
    private Libro leerFormulario() {
        Categoria categoria = (Categoria) cmbCategoria.getSelectedItem();
        return new Libro(
                txtTitulo.getText().trim(),
                txtAutor.getText().trim(),
                txtIsbn.getText().trim(),
                txtEditorial.getText().trim(),
                Integer.parseInt(txtStock.getText().trim()),
                categoria.getId()
        );
    }

    private void agregarLibro() {
        if (!validarFormulario()) return;

        try {
            controlador.registrar(leerFormulario());
            UtilVista.mostrarMensaje(this, "Libro registrado correctamente.");
            cargarTabla();
            limpiarFormulario();
        } catch (SQLException e) {
            UtilVista.mostrarErrorSQL(this, "registrar el libro", e);
        }
    }

    private void editarLibro() {
        if (filaSeleccionada == -1) {
            UtilVista.mostrarAdvertencia(this, "Selecciona un libro para editar.");
            return;
        }
        if (!validarFormulario()) return;

        Libro libro = leerFormulario();
        libro.setId(Integer.parseInt(model.getValueAt(filaSeleccionada, 0).toString())); // ID está en la columna 0
        try {
            controlador.actualizar(libro);
            UtilVista.mostrarMensaje(this, "Libro actualizado correctamente.");
            cargarTabla();
            limpiarFormulario();
        } catch (SQLException e) {
            UtilVista.mostrarErrorSQL(this, "actualizar el libro", e);
        }
    }

    private void eliminarLibro() {
        if (filaSeleccionada == -1) {
            UtilVista.mostrarAdvertencia(this, "Selecciona un libro para eliminar.");
            return;
        }
        if (!UtilVista.confirmar(this, "¿Estás seguro de que deseas eliminar este libro?")) return;

        int id = Integer.parseInt(model.getValueAt(filaSeleccionada, 0).toString());
        try {
            controlador.eliminar(id);
            UtilVista.mostrarMensaje(this, "Libro eliminado correctamente.");
            cargarTabla();
            limpiarFormulario();
        } catch (SQLException e) {
            UtilVista.mostrarErrorSQL(this, "eliminar el libro", e);
        }
    }

    // Busca en el combo la categoría con ese id y la selecciona
    private void seleccionarCategoria(int id) {
        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
            if (cmbCategoria.getItemAt(i).getId() == id) {
                cmbCategoria.setSelectedIndex(i);
                return;
            }
        }
    }

    private void limpiarFormulario() {
        txtTitulo.setText("");
        txtAutor.setText("");
        txtIsbn.setText("");
        txtEditorial.setText("");
        txtStock.setText("");
        if (cmbCategoria.getItemCount() > 0) {
            cmbCategoria.setSelectedIndex(0);
        }
        filaSeleccionada = -1;
        tblLibros.clearSelection();
    }
}
