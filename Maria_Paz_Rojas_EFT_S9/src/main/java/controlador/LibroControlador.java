package controlador;

import dao.CategoriaDAO;
import dao.LibroDAO;
import modelo.Categoria;
import modelo.Libro;

import java.sql.SQLException;
import java.util.List;

/**
 * Controlador de la gestión de libros: intermediario entre VentanaLibros y los DAO.
 */
public class LibroControlador {

    private final LibroDAO libroDAO = new LibroDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    public List<Libro> listarLibros() throws SQLException {
        return libroDAO.readAll();
    }

    public List<Categoria> listarCategorias() throws SQLException {
        return categoriaDAO.readAll();
    }

    public void registrar(Libro libro) throws SQLException {
        libroDAO.create(libro);
    }

    public void actualizar(Libro libro) throws SQLException {
        libroDAO.update(libro);
    }

    public void eliminar(int id) throws SQLException {
        libroDAO.delete(id);
    }
}
