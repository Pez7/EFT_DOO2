package sincronizacion;

import dao.LibroDAO;
import dao.PrestamoDAO;
import modelo.Prestamo;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Recurso compartido por todos los hilos de préstamo y devolución.
 * Sus métodos que modifican el stock son synchronized: solo un hilo a la vez
 * puede verificar y modificar el stock, evitando condiciones de carrera
 * (por ejemplo, prestar dos veces la última unidad de un libro).
 *
 * Existe una única instancia, para que todos los hilos usen el mismo bloqueo.
 */
public class GestorPrestamos {

    // Tiempo que se simula de procesamiento de cada operación (ms)
    private static final int TIEMPO_PROCESO = 1500;

    private static final GestorPrestamos INSTANCIA = new GestorPrestamos();

    private final LibroDAO libroDAO = new LibroDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    private GestorPrestamos() {
    }

    public static GestorPrestamos getInstance() {
        return INSTANCIA;
    }

    /**
     * Registra un préstamo: verifica el stock, lo descuenta y registra el préstamo
     * en el historial del estudiante (tabla prestamos), todo en una transacción.
     *
     * @return el préstamo registrado, o null si el libro no tiene stock disponible
     */
    public synchronized Prestamo registrarPrestamo(int idEstudiante, int idLibro)
            throws SQLException, InterruptedException {
        System.out.println("[" + Thread.currentThread().getName() + "] Procesando préstamo del libro " + idLibro);
        Thread.sleep(TIEMPO_PROCESO); // simula el tiempo de procesamiento

        Connection conn = DatabaseConnection.getInstance().getConnection();
        conn.setAutoCommit(false);
        try {
            int stock = libroDAO.obtenerStock(idLibro);
            if (stock <= 0) {
                conn.rollback();
                System.out.println("[" + Thread.currentThread().getName() + "] Rechazado: libro " + idLibro + " sin stock");
                return null;
            }

            libroDAO.modificarStock(idLibro, -1);

            LocalDate hoy = LocalDate.now();
            Prestamo prestamo = new Prestamo(idEstudiante, idLibro, hoy, Prestamo.calcularVencimiento(hoy), false);
            prestamoDAO.create(prestamo);

            conn.commit();
            System.out.println("[" + Thread.currentThread().getName() + "] Préstamo registrado. Stock restante: " + (stock - 1));
            return prestamo;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /**
     * Registra la devolución: marca el préstamo como devuelto y suma 1 al stock del libro,
     * todo en una transacción.
     *
     * @return el préstamo devuelto (con sus días de atraso), o null si ya estaba devuelto
     */
    public synchronized Prestamo registrarDevolucion(int idPrestamo)
            throws SQLException, InterruptedException {
        System.out.println("[" + Thread.currentThread().getName() + "] Procesando devolución del préstamo " + idPrestamo);
        Thread.sleep(TIEMPO_PROCESO); // simula el tiempo de procesamiento

        Connection conn = DatabaseConnection.getInstance().getConnection();
        conn.setAutoCommit(false);
        try {
            // se vuelve a leer dentro del bloqueo para no devolver dos veces el mismo préstamo
            Prestamo prestamo = prestamoDAO.readById(idPrestamo);
            if (prestamo == null || prestamo.isDevuelto()) {
                conn.rollback();
                return null;
            }

            prestamoDAO.marcarDevuelto(idPrestamo);
            libroDAO.modificarStock(prestamo.getIdLibro(), 1);

            conn.commit();
            System.out.println("[" + Thread.currentThread().getName() + "] Devolución registrada.");
            return prestamo;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
