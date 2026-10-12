package dao;

import modelo.Libro;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de la tabla libros (CRUD y manejo de stock).
 * La consulta de libros usa JOIN para obtener el nombre de la categoría.
 */
public class LibroDAO implements CrudDAO<Libro> {

    // Inserta un nuevo libro
    @Override
    public void create(Libro libro) throws SQLException {
        String sql = "INSERT INTO libros (titulo, autor, isbn, editorial, stock, id_categoria) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setString(3, libro.getIsbn());
            stmt.setString(4, libro.getEditorial());
            stmt.setInt(5, libro.getStock());
            stmt.setInt(6, libro.getIdCategoria());
            stmt.executeUpdate();
        }
    }

    // Retorna todos los libros con el nombre de su categoría
    @Override
    public List<Libro> readAll() throws SQLException {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT l.*, c.nombre AS categoria "
                + "FROM libros l "
                + "JOIN categorias c ON l.id_categoria = c.id "
                + "ORDER BY l.id";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Libro libro = new Libro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("isbn"),
                        rs.getString("editorial"),
                        rs.getInt("stock"),
                        rs.getInt("id_categoria")
                );
                libro.setNombreCategoria(rs.getString("categoria"));
                lista.add(libro);
            }
        }
        return lista;
    }

    // Actualiza los datos de un libro existente
    @Override
    public void update(Libro libro) throws SQLException {
        String sql = "UPDATE libros SET titulo = ?, autor = ?, isbn = ?, editorial = ?, stock = ?, id_categoria = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setString(3, libro.getIsbn());
            stmt.setString(4, libro.getEditorial());
            stmt.setInt(5, libro.getStock());
            stmt.setInt(6, libro.getIdCategoria());
            stmt.setInt(7, libro.getId());
            stmt.executeUpdate();
        }
    }

    // Elimina un libro por su id (falla si tiene préstamos asociados)
    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM libros WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Retorna el stock actual de un libro (-1 si el libro no existe)
    public int obtenerStock(int idLibro) throws SQLException {
        String sql = "SELECT stock FROM libros WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLibro);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("stock") : -1;
            }
        }
    }

    // Suma (cantidad positiva) o resta (cantidad negativa) unidades al stock de un libro
    public void modificarStock(int idLibro, int cantidad) throws SQLException {
        String sql = "UPDATE libros SET stock = stock + ? WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cantidad);
            stmt.setInt(2, idLibro);
            stmt.executeUpdate();
        }
    }
}
