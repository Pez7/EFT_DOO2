package modelo;

/**
 * Representa a un estudiante de la biblioteca (tabla estudiantes).
 */
public class Estudiante extends Persona {

    private String curso;

    public Estudiante(String nombre, String rut, String curso, String correo) {
        super(nombre, rut, correo);
        this.curso = curso;
    }

    public Estudiante(int id, String nombre, String rut, String curso, String correo) {
        super(id, nombre, rut, correo);
        this.curso = curso;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    @Override
    public String getDescripcion() {
        return nombre + " (" + curso + ")";
    }
}
