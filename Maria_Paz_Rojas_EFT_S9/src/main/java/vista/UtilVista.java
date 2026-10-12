package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.format.DateTimeFormatter;

/**
 * Métodos de apoyo reutilizados por todas las ventanas:
 * mensajes al usuario, manejo de errores SQL, validaciones y configuración de tablas.
 */
public class UtilVista {

    // Formato de fecha dd-mm-aaaa para mostrar en las tablas
    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // Códigos de error de MySQL
    private static final int ERROR_DUPLICADO = 1062;
    private static final int ERROR_CLAVE_FORANEA = 1451;

    // Muestra un mensaje informativo (por ejemplo, una operación exitosa)
    public static void mostrarMensaje(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Biblioteca", JOptionPane.INFORMATION_MESSAGE);
    }

    // Muestra una advertencia (dato inválido u operación no permitida)
    public static void mostrarAdvertencia(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    // Pide confirmación al usuario. Retorna true si responde "Sí"
    public static boolean confirmar(Component padre, String mensaje) {
        int respuesta = JOptionPane.showConfirmDialog(padre, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION);
        return respuesta == JOptionPane.YES_OPTION;
    }

    /**
     * Manejo genérico de errores SQL: muestra al usuario un mensaje claro
     * y deja el detalle técnico en la consola.
     *
     * @param accion lo que se intenta hacer, por ejemplo "eliminar el libro"
     */
    public static void mostrarErrorSQL(Component padre, String accion, SQLException e) {
        System.err.println("Error SQL al " + accion + ": " + e.getMessage());

        String causa;
        if (e instanceof SQLIntegrityConstraintViolationException && e.getErrorCode() == ERROR_DUPLICADO) {
            // rut o isbn repetido (columnas UNIQUE)
            causa = "Ya existe un registro con ese RUT o ISBN.";
        } else if (e instanceof SQLIntegrityConstraintViolationException && e.getErrorCode() == ERROR_CLAVE_FORANEA) {
            // la clave foránea impide la operación (ej. borrar un libro que tiene préstamos)
            causa = "El registro está relacionado con otros datos.\n"
                    + "Un libro o estudiante con préstamos registrados no se puede eliminar.";
        } else {
            causa = "Ocurrió un problema con la base de datos.\n"
                    + "Verifica que MySQL esté encendido e inténtalo nuevamente.";
        }
        JOptionPane.showMessageDialog(padre, "No fue posible " + accion + ".\n\n" + causa,
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Crea un modelo de tabla cuyas celdas no se pueden editar
    public static DefaultTableModel crearModeloTabla(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // Oculta una columna de la tabla
    public static void ocultarColumna(JTable tabla, int columna) {
        tabla.getColumnModel().getColumn(columna).setMinWidth(0);
        tabla.getColumnModel().getColumn(columna).setMaxWidth(0);
    }

    // Revisa carácter por carácter si el texto tiene algún dígito
    public static boolean contieneNumeros(String texto) {
        for (char c : texto.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }
}
