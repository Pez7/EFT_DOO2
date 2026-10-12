package controlador;

import dao.EstudianteDAO;
import dao.LibroDAO;
import dao.PrestamoDAO;
import modelo.Estudiante;
import modelo.Libro;
import modelo.Prestamo;
import sincronizacion.ResultadoTarea;
import sincronizacion.TareaDevolucion;
import sincronizacion.TareaPrestamo;

import java.sql.SQLException;
import java.util.List;

/**
 * Controlador de préstamos y devoluciones: intermediario entre VentanaPrestamos y el modelo.
 * Los préstamos y devoluciones se ejecutan en un hilo independiente para no bloquear la interfaz.
 */
public class PrestamoControlador {

    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();
    private final LibroDAO libroDAO = new LibroDAO();

    public List<Prestamo> listarPrestamos() throws SQLException {
        return prestamoDAO.readAll();
    }

    public List<Estudiante> listarEstudiantes() throws SQLException {
        return estudianteDAO.readAll();
    }

    public List<Libro> listarLibros() throws SQLException {
        return libroDAO.readAll();
    }

    /**
     * Lanza un hilo que registra el préstamo en segundo plano.
     * Al terminar, el resultado se entrega a la vista mediante ResultadoTarea.
     */
    public void prestarLibro(int idEstudiante, int idLibro, ResultadoTarea resultado) {
        Thread hilo = new Thread(new TareaPrestamo(idEstudiante, idLibro, resultado));
        hilo.setName("Prestamo-estudiante-" + idEstudiante + "-libro-" + idLibro);
        hilo.start();
    }

    /**
     * Lanza un hilo que registra la devolución en segundo plano.
     */
    public void devolverLibro(int idPrestamo, ResultadoTarea resultado) {
        Thread hilo = new Thread(new TareaDevolucion(idPrestamo, resultado));
        hilo.setName("Devolucion-prestamo-" + idPrestamo);
        hilo.start();
    }
}
