package modelo;

/**
 * Fila del reporte "Libros más prestados": título, autor y cantidad de préstamos.
 */
public class LibroMasPrestado {

    private final String titulo;
    private final String autor;
    private final int cantidadPrestamos;

    public LibroMasPrestado(String titulo, String autor, int cantidadPrestamos) {
        this.titulo = titulo;
        this.autor = autor;
        this.cantidadPrestamos = cantidadPrestamos;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public int getCantidadPrestamos() {
        return cantidadPrestamos;
    }
}
