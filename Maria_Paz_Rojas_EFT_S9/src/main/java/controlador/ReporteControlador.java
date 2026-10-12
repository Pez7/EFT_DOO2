package controlador;

import dao.EstudianteDAO;
import dao.PrestamoDAO;
import modelo.Estudiante;
import modelo.LibroMasPrestado;
import modelo.Prestamo;

import java.sql.SQLException;
import java.util.List;

/**
 * Controlador de reportes: intermediario entre VentanaReportes y los DAO.
 */
public class ReporteControlador {

    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();

    public List<LibroMasPrestado> librosMasPrestados() throws SQLException {
        return prestamoDAO.readLibrosMasPrestados();
    }

    public List<Prestamo> historialPorEstudiante(int idEstudiante) throws SQLException {
        return prestamoDAO.readByEstudiante(idEstudiante);
    }

    public List<Prestamo> librosEnPrestamo() throws SQLException {
        return prestamoDAO.readNoDevueltos();
    }

    public List<Estudiante> listarEstudiantes() throws SQLException {
        return estudianteDAO.readAll();
    }
}
