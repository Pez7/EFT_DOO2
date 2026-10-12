package dao;

import modelo.RolUsuario;
import modelo.Usuario;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de la tabla usuarios (CRUD y autenticación).
 * Los errores SQL se lanzan para que la vista muestre un mensaje al usuario.
 */
public class UsuarioDAO implements CrudDAO<Usuario> {

    // Inserta un nuevo usuario
    @Override
    public void create(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, rut, correo, contraseña, rol) VALUES (?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getRut());
            stmt.setString(3, usuario.getCorreo());
            stmt.setString(4, usuario.getContrasena());
            stmt.setString(5, usuario.getRol().name().toLowerCase());
            stmt.executeUpdate();
        }
    }

    // Retorna todos los usuarios
    @Override
    public List<Usuario> readAll() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY id";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(leerUsuario(rs));
            }
        }
        return lista;
    }

    // Actualiza los datos de un usuario existente
    @Override
    public void update(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nombre = ?, rut = ?, correo = ?, contraseña = ?, rol = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getRut());
            stmt.setString(3, usuario.getCorreo());
            stmt.setString(4, usuario.getContrasena());
            stmt.setString(5, usuario.getRol().name().toLowerCase());
            stmt.setInt(6, usuario.getId());
            stmt.executeUpdate();
        }
    }

    // Elimina un usuario por su id
    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Busca el usuario con ese rut y contraseña.
     * @return el usuario encontrado, o null si los datos no coinciden
     */
    public Usuario autenticar(String rut, String contrasena) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE rut = ? AND contraseña = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rut);
            stmt.setString(2, contrasena);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return leerUsuario(rs);
                }
            }
        }
        return null;
    }

    // Convierte la fila actual del ResultSet en un objeto Usuario
    private Usuario leerUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("rut"),
                rs.getString("correo"),
                rs.getString("contraseña"),
                RolUsuario.valueOf(rs.getString("rol").toUpperCase())
        );
    }
}
