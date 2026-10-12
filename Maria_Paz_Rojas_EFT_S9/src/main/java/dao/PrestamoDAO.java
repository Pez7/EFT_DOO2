package dao;

import modelo.LibroMasPrestado;
import modelo.Prestamo;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de la tabla prestamos: registro, devolución y consultas para reportes.
 * Las consultas usan JOIN para obtener el nombre del estudiante y el título del libro.
 */
public class PrestamoDAO {

    private static final String SELECT_PRESTAMOS =
            "SELECT p.*, e.nombre AS estudiante, l.titulo AS libro "
                    + "FROM prestamos p "
                    + "JOIN estudiantes e ON p.id_estudiante = e.id "
                    + "JOIN libros l ON p.id_libro = l.id ";

    // Inserta un nuevo préstamo
    public void create(Prestamo prestamo) throws SQLException {
        String sql = "INSERT INTO prestamos (id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto) VALUES (?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, prestamo.getIdEstudiante());
            stmt.setInt(2, prestamo.getIdLibro());
            stmt.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            stmt.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
            stmt.setBoolean(5, prestamo.isDevuelto());
            stmt.executeUpdate();
        }
    }

    // Retorna todos los préstamos (los más recientes primero)
    public List<Prestamo> readAll() throws SQLException {
        String sql = SELECT_PRESTAMOS + "ORDER BY p.id DESC";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            return leerPrestamos(stmt);
        }
    }

    // Retorna un préstamo por su id, o null si no existe
    public Prestamo readById(int id) throws SQLException {
        String sql = SELECT_PRESTAMOS + "WHERE p.id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            List<Prestamo> lista = leerPrestamos(stmt);
            return lista.isEmpty() ? null : lista.get(0);
        }
    }

    // Marca un préstamo como devuelto
    public void marcarDevuelto(int id) throws SQLException {
        String sql = "UPDATE prestamos SET devuelto = TRUE WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Reporte: historial de préstamos de un estudiante
    public List<Prestamo> readByEstudiante(int idEstudiante) throws SQLException {
        String sql = SELECT_PRESTAMOS + "WHERE p.id_estudiante = ? ORDER BY p.fecha_prestamo DESC";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEstudiante);
            return leerPrestamos(stmt);
        }
    }

    // Reporte: préstamos que aún no han sido devueltos (libros actualmente en préstamo)
    public List<Prestamo> readNoDevueltos() throws SQLException {
        String sql = SELECT_PRESTAMOS + "WHERE p.devuelto = FALSE ORDER BY p.fecha_devolucion";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            return leerPrestamos(stmt);
        }
    }

    // Reporte: libros ordenados por cantidad de veces que han sido prestados
    public List<LibroMasPrestado> readLibrosMasPrestados() throws SQLException {
        List<LibroMasPrestado> lista = new ArrayList<>();
        String sql = "SELECT l.titulo, l.autor, COUNT(p.id) AS cantidad "
                + "FROM prestamos p "
                + "JOIN libros l ON p.id_libro = l.id "
                + "GROUP BY l.id, l.titulo, l.autor "
                + "ORDER BY cantidad DESC, l.titulo";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new LibroMasPrestado(
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getInt("cantidad")
                ));
            }
        }
        return lista;
    }

    // Ejecuta la consulta y convierte cada fila del ResultSet en un objeto Prestamo
    private List<Prestamo> leerPrestamos(PreparedStatement stmt) throws SQLException {
        List<Prestamo> lista = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Prestamo prestamo = new Prestamo(
                        rs.getInt("id"),
                        rs.getInt("id_estudiante"),
                        rs.getInt("id_libro"),
                        rs.getDate("fecha_prestamo").toLocalDate(),
                        rs.getDate("fecha_devolucion").toLocalDate(),
                        rs.getBoolean("devuelto")
                );
                prestamo.setNombreEstudiante(rs.getString("estudiante"));
                prestamo.setTituloLibro(rs.getString("libro"));
                lista.add(prestamo);
            }
        }
        return lista;
    }
}
