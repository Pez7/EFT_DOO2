package dao;

import modelo.Estudiante;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de la tabla estudiantes.
 * Los errores SQL se lanzan para que la vista muestre un mensaje al usuario.
 */
public class EstudianteDAO implements CrudDAO<Estudiante> {

    // Inserta un nuevo estudiante
    @Override
    public void create(Estudiante estudiante) throws SQLException {
        String sql = "INSERT INTO estudiantes (nombre, rut, curso, correo) VALUES (?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estudiante.getNombre());
            stmt.setString(2, estudiante.getRut());
            stmt.setString(3, estudiante.getCurso());
            stmt.setString(4, estudiante.getCorreo());
            stmt.executeUpdate();
        }
    }

    // Retorna todos los estudiantes
    @Override
    public List<Estudiante> readAll() throws SQLException {
        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT * FROM estudiantes ORDER BY id";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Estudiante(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("rut"),
                        rs.getString("curso"),
                        rs.getString("correo")
                ));
            }
        }
        return lista;
    }

    // Actualiza los datos de un estudiante existente
    @Override
    public void update(Estudiante estudiante) throws SQLException {
        String sql = "UPDATE estudiantes SET nombre = ?, rut = ?, curso = ?, correo = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estudiante.getNombre());
            stmt.setString(2, estudiante.getRut());
            stmt.setString(3, estudiante.getCurso());
            stmt.setString(4, estudiante.getCorreo());
            stmt.setInt(5, estudiante.getId());
            stmt.executeUpdate();
        }
    }

    // Elimina un estudiante por su id (falla si tiene préstamos asociados)
    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM estudiantes WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
