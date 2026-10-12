package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Conexión con la base de datos MySQL biblioteca.
 * Aplica el patrón Singleton: existe una única instancia de esta clase
 * y una única conexión compartida por todos los DAO.
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "123456";

    // Única instancia de la clase
    private static DatabaseConnection instancia;

    private Connection conexion;

    // Constructor privado: nadie fuera de la clase puede crear otra instancia
    private DatabaseConnection() {
    }

    /**
     * Retorna la única instancia de DatabaseConnection (la crea la primera vez).
     * Es synchronized para que dos hilos no creen dos instancias al mismo tiempo.
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instancia == null) {
            instancia = new DatabaseConnection();
        }
        return instancia;
    }

    /**
     * Retorna la conexión. Se abre con DriverManager.getConnection la primera vez
     * y se vuelve a abrir solo si se cerró o se perdió.
     */
    public synchronized Connection getConnection() throws SQLException {
        if (conexion == null || conexion.isClosed() || !conexion.isValid(2)) {
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        }
        return conexion;
    }
}
