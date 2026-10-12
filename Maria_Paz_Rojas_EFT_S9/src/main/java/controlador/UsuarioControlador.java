package controlador;

import dao.UsuarioDAO;
import modelo.Usuario;

import java.sql.SQLException;
import java.util.List;

/**
 * Controlador de la gestión de usuarios: intermediario entre VentanaUsuarios y UsuarioDAO.
 */
public class UsuarioControlador {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public List<Usuario> listarUsuarios() throws SQLException {
        return usuarioDAO.readAll();
    }

    public void registrar(Usuario usuario) throws SQLException {
        usuarioDAO.create(usuario);
    }

    public void actualizar(Usuario usuario) throws SQLException {
        usuarioDAO.update(usuario);
    }

    public void eliminar(int id) throws SQLException {
        usuarioDAO.delete(id);
    }
}
