package controlador;

import dao.EstudianteDAO;
import dao.UsuarioDAO;
import modelo.Estudiante;
import modelo.RolUsuario;
import modelo.Usuario;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador de la gestión de estudiantes: intermediario entre VentanaEstudiantes y los DAO.
 */
public class EstudianteControlador {

    private final EstudianteDAO estudianteDAO = new EstudianteDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public List<Estudiante> listarEstudiantes() throws SQLException {
        return estudianteDAO.readAll();
    }

    /**
     * Registra al estudiante y lo crea como usuario del sistema (rol estudiante)
     * con el mismo nombre, rut y correo. Ambas inserciones se hacen en una transacción:
     * si una falla, no se guarda ninguna.
     */
    public void registrar(Estudiante estudiante, String contrasena) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        conn.setAutoCommit(false);
        try {
            estudianteDAO.create(estudiante);
            usuarioDAO.create(new Usuario(
                    estudiante.getNombre(),
                    estudiante.getRut(),
                    estudiante.getCorreo(),
                    contrasena,
                    RolUsuario.ESTUDIANTE
            ));
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public void actualizar(Estudiante estudiante) throws SQLException {
        estudianteDAO.update(estudiante);
    }

    public void eliminar(int id) throws SQLException {
        estudianteDAO.delete(id);
    }
}
