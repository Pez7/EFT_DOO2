import util.DatabaseConnection;
import vista.UtilVista;
import vista.VentanaLogin;

import javax.swing.*;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Se prueba la conexión antes de abrir la aplicación
            try {
                DatabaseConnection.getInstance().getConnection();
                System.out.println("Conexión exitosa a la base de datos.");
                new VentanaLogin().setVisible(true);
            } catch (SQLException e) {
                UtilVista.mostrarErrorSQL(null, "conectar con la base de datos biblioteca", e);
            }
        });
    }
}
