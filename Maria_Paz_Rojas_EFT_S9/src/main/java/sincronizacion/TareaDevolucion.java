package sincronizacion;

import modelo.Prestamo;

import javax.swing.*;
import java.sql.SQLException;

/**
 * Tarea que registra la devolución de un préstamo en un hilo independiente,
 * para no bloquear la interfaz. Detecta si la devolución se hizo con atraso.
 */
public class TareaDevolucion implements Runnable {

    private final int idPrestamo;
    private final ResultadoTarea resultado;

    public TareaDevolucion(int idPrestamo, ResultadoTarea resultado) {
        this.idPrestamo = idPrestamo;
        this.resultado = resultado;
    }

    @Override
    public void run() {
        boolean exito;
        String mensaje;
        try {
            Prestamo prestamo = GestorPrestamos.getInstance().registrarDevolucion(idPrestamo);
            if (prestamo == null) {
                exito = false;
                mensaje = "El préstamo ya había sido devuelto.";
            } else if (prestamo.estaAtrasado()) {
                exito = true;
                mensaje = "Devolución registrada.\n\nATENCIÓN: el libro se devolvió con "
                        + prestamo.getDiasAtraso() + " día(s) de atraso.";
            } else {
                exito = true;
                mensaje = "Devolución registrada a tiempo.";
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al registrar la devolución: " + e.getMessage());
            exito = false;
            mensaje = "No fue posible registrar la devolución.\n\n"
                    + "Ocurrió un problema con la base de datos.\n"
                    + "Verifica que MySQL esté encendido e inténtalo nuevamente.";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            exito = false;
            mensaje = "El registro de la devolución fue interrumpido.";
        }

        // La interfaz solo se modifica desde el hilo de Swing
        final boolean exitoFinal = exito;
        final String mensajeFinal = mensaje;
        SwingUtilities.invokeLater(() -> resultado.alTerminar(exitoFinal, mensajeFinal));
    }
}
