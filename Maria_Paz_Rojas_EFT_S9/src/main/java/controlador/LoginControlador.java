package controlador;

import dao.UsuarioDAO;
import modelo.Usuario;

import java.sql.SQLException;

/**
 * Controlador de la autenticación: valida al usuario contra la base de datos.
 */
public class LoginControlador {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * @return el usuario autenticado, o null si el rut o la contraseña no coinciden
     */
    public Usuario autenticar(String rut, String contrasena) throws SQLException {
        return usuarioDAO.autenticar(rut, contrasena);
    }
}
