package modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Representa un préstamo de un libro a un estudiante (tabla prestamos).
 * fecha_devolucion corresponde a la fecha de vencimiento del préstamo.
 */
public class Prestamo {

    // Plazo del préstamo en días (fecha de vencimiento = fecha de préstamo + 7)
    public static final int DIAS_PRESTAMO = 7;

    private int id;
    private int idEstudiante;
    private int idLibro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private boolean devuelto;

    // Datos de solo lectura obtenidos con JOIN, para mostrar en la tabla
    private String nombreEstudiante;
    private String tituloLibro;

    public Prestamo(int idEstudiante, int idLibro, LocalDate fechaPrestamo, LocalDate fechaDevolucion, boolean devuelto) {
        this.idEstudiante = idEstudiante;
        this.idLibro = idLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = devuelto;
    }

    public Prestamo(int id, int idEstudiante, int idLibro, LocalDate fechaPrestamo, LocalDate fechaDevolucion, boolean devuelto) {
        this(idEstudiante, idLibro, fechaPrestamo, fechaDevolucion, devuelto);
        this.id = id;
    }

    /**
     * Calcula automáticamente la fecha de vencimiento a partir de la fecha de préstamo.
     */
    public static LocalDate calcularVencimiento(LocalDate fechaPrestamo) {
        return fechaPrestamo.plusDays(DIAS_PRESTAMO);
    }

    /**
     * Días de atraso a la fecha de hoy (0 si no está atrasado o si ya fue devuelto).
     */
    public long getDiasAtraso() {
        if (devuelto) {
            return 0;
        }
        long dias = ChronoUnit.DAYS.between(fechaDevolucion, LocalDate.now());
        return Math.max(dias, 0);
    }

    public boolean estaAtrasado() {
        return getDiasAtraso() > 0;
    }

    /**
     * Estado que se muestra en las tablas: DEVUELTO, PENDIENTE o ATRASADO (n días).
     */
    public String getEstado() {
        if (devuelto) {
            return "DEVUELTO";
        }
        if (estaAtrasado()) {
            return "ATRASADO (" + getDiasAtraso() + " días)";
        }
        return "PENDIENTE";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public void setDevuelto(boolean devuelto) {
        this.devuelto = devuelto;
    }

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }

    public void setNombreEstudiante(String nombreEstudiante) {
        this.nombreEstudiante = nombreEstudiante;
    }

    public String getTituloLibro() {
        return tituloLibro;
    }

    public void setTituloLibro(String tituloLibro) {
        this.tituloLibro = tituloLibro;
    }
}
