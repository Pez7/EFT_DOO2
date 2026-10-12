package modelo;

/**
 * Representa un libro del inventario (tabla libros).
 */
public class Libro {

    private int id;
    private String titulo;
    private String autor;
    private String isbn;
    private String editorial;
    private int stock;
    private int idCategoria;

    // Dato de solo lectura obtenido con JOIN, para mostrar en la tabla
    private String nombreCategoria;

    public Libro(String titulo, String autor, String isbn, String editorial, int stock, int idCategoria) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.editorial = editorial;
        this.stock = stock;
        this.idCategoria = idCategoria;
    }

    public Libro(int id, String titulo, String autor, String isbn, String editorial, int stock, int idCategoria) {
        this(titulo, autor, isbn, editorial, stock, idCategoria);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    /**
     * Texto que se muestra en los ComboBox: "id - título (stock: n)".
     */
    @Override
    public String toString() {
        return id + " - " + titulo + " (stock: " + stock + ")";
    }
}
