package modelo;

/**
 * Representa a un usuario que puede ingresar al sistema (tabla usuarios).
 */
public class Usuario extends Persona {

    private String contrasena;
    private RolUsuario rol;

    public Usuario(String nombre, String rut, String correo, String contrasena, RolUsuario rol) {
        super(nombre, rut, correo);
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public Usuario(int id, String nombre, String rut, String correo, String contrasena, RolUsuario rol) {
        super(id, nombre, rut, correo);
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    // El bibliotecario tiene gestión total; el estudiante solo préstamos, devoluciones y consulta
    public boolean esBibliotecario() {
        return rol == RolUsuario.BIBLIOTECARIO;
    }

    @Override
    public String getDescripcion() {
        return nombre + " (" + rol.name().toLowerCase() + ")";
    }
}
