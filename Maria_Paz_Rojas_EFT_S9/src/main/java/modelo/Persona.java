package modelo;

/**
 * Clase abstracta con los datos comunes de las personas del sistema
 * (estudiantes y usuarios): id, nombre, rut y correo.
 * No se puede instanciar directamente; Estudiante y Usuario la extienden.
 */
public abstract class Persona {

    protected int id;
    protected String nombre;
    protected String rut;
    protected String correo;

    public Persona(String nombre, String rut, String correo) {
        this.nombre = nombre;
        this.rut = rut;
        this.correo = correo;
    }

    public Persona(int id, String nombre, String rut, String correo) {
        this(nombre, rut, correo);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * Método abstracto: cada subclase define cómo se describe.
     */
    public abstract String getDescripcion();

    /**
     * Texto que se muestra en los ComboBox: "id - descripción".
     * Usa getDescripcion(), que se resuelve según el tipo real del objeto (polimorfismo).
     */
    @Override
    public String toString() {
        return id + " - " + getDescripcion();
    }
}
