package sincronizacion;

/**
 * Contrato para recibir el resultado de una tarea que se ejecuta en segundo plano.
 * La vista lo implementa (con una lambda) para mostrar el mensaje y refrescar la tabla.
 */
public interface ResultadoTarea {

    /**
     * @param exito   true si la operación se realizó
     * @param mensaje texto para mostrar al usuario
     */
    void alTerminar(boolean exito, String mensaje);
}
