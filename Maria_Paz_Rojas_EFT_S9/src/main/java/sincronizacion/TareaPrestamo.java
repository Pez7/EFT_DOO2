package sincronizacion;

import modelo.Prestamo;

import javax.swing.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

/**
 * Tarea que registra un préstamo en un hilo independiente, para no bloquear la interfaz.
 * Registra el préstamo, actualiza el stock y deja el registro en el historial del estudiante
 * a través de GestorPrestamos (métodos synchronized).
 */
public class TareaPrestamo implements Runnable {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final int idEstudiante;
    private final int idLibro;
    private final ResultadoTarea resultado;

    public TareaPrestamo(int idEstudiante, int idLibro, ResultadoTarea resultado) {
        this.idEstudiante = idEstudiante;
        this.idLibro = idLibro;
        this.resultado = resultado;
    }

    @Override
    public void run() {
        boolean exito;
        String mensaje;
        try {
            Prestamo prestamo = GestorPrestamos.getInstance().registrarPrestamo(idEstudiante, idLibro);
            if (prestamo != null) {
                exito = true;
                mensaje = "Préstamo registrado correctamente.\nFecha de vencimiento: "
                        + prestamo.getFechaDevolucion().format(FORMATO_FECHA);
            } else {
                exito = false;
                mensaje = "No fue posible registrar el préstamo.\nEl libro no tiene stock disponible.";
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al registrar el préstamo: " + e.getMessage());
            exito = false;
            mensaje = "No fue posible registrar el préstamo.\n\n"
                    + "Ocurrió un problema con la base de datos.\n"
                    + "Verifica que MySQL esté encendido e inténtalo nuevamente.";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            exito = false;
            mensaje = "El registro del préstamo fue interrumpido.";
        }

        // La interfaz solo se modifica desde el hilo de Swing
        final boolean exitoFinal = exito;
        final String mensajeFinal = mensaje;
        SwingUtilities.invokeLater(() -> resultado.alTerminar(exitoFinal, mensajeFinal));
    }
}
