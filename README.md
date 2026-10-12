# Biblioteca Escolar – Sistema de gestión de libros, estudiantes y préstamos (Semana 9)

Evaluación Final Transversal individual de Desarrollo Orientado a Objetos II (PRY2203).
👤 Autor del proyecto

Nombre completo: María Paz Rojas

Sección: 2026_202_OL_PRY2202_24613392_PCT

Carrera: Analista programador computacional

Sede: Online

Aplicación de escritorio en Java Swing (JFrame) con persistencia en MySQL mediante JDBC, aplicando los patrones MVC, DAO y Singleton, y concurrencia con hilos y sincronización.

## Funcionalidades
- **Autenticación**: ingreso con RUT y contraseña validados en la base de datos.
  - Rol `bibliotecario`: gestión total.
  - Rol `estudiante`: préstamos, devoluciones y consulta de libros.
  - Botón *Cerrar sesión* para volver al ingreso.
- **Libros**: registrar, editar, eliminar y listar (título, autor, ISBN, editorial, stock y categoría seleccionada en `JComboBox`). El estudiante solo puede consultar el listado.
- **Estudiantes**: registrar, editar, eliminar y listar. Al registrar un estudiante también se crea su usuario (rol estudiante) para que pueda ingresar al sistema.
- **Usuarios**: registrar, editar, eliminar y listar (nombre, RUT, correo, contraseña y rol).
- **Préstamos y devoluciones**:
  - El préstamo verifica el stock, lo descuenta y registra el préstamo en el historial del estudiante.
  - La fecha de vencimiento se calcula automáticamente (fecha de préstamo + 7 días).
  - La devolución suma 1 al stock y detecta el atraso, informando los días de atraso.
- **Reportes**: libros más prestados, historial por estudiante y listado de libros actualmente en préstamo.
- Validación de campos obligatorios y formatos antes de cada operación.
- Manejo de `SQLException` con mensajes mediante `JOptionPane` (RUT/ISBN duplicado, registros con préstamos asociados, problemas de conexión).

## Patrones y conceptos aplicados
- **MVC**: `modelo` (datos), `vista` (ventanas JFrame) y `controlador` (intermediario entre vista y DAO).
- **DAO**: cada DAO se encarga solo del acceso a la base de datos. La interfaz genérica `CrudDAO<T>` la implementan `LibroDAO`, `EstudianteDAO` y `UsuarioDAO`.
- **Singleton**: `DatabaseConnection` con constructor privado y método estático `getInstance()`; una única conexión creada con `DriverManager.getConnection`.
- **Herencia, clase abstracta y polimorfismo**: `Persona` (abstracta, con el método abstracto `getDescripcion()`) es extendida por `Estudiante` y `Usuario`.
- **Concurrencia**: `TareaPrestamo` y `TareaDevolucion` implementan `Runnable` y se ejecutan con `new Thread`, por lo que la interfaz no se congela. Al terminar, el resultado se muestra con `SwingUtilities.invokeLater`.
- **Sincronización**: `GestorPrestamos` (instancia única) tiene los métodos `synchronized` que verifican y modifican el stock dentro de una transacción, evitando condiciones de carrera ante préstamos simultáneos.

## Estructura
```
src/main/java
├── Main.java                     Punto de entrada (prueba la conexión y abre la ventana de ingreso)
├── util/DatabaseConnection.java  Conexión JDBC a biblioteca (Singleton)
├── modelo/                       Persona (abstracta), Estudiante, Usuario, RolUsuario, Libro, Categoria, Prestamo, LibroMasPrestado
├── dao/                          CrudDAO (interfaz), LibroDAO, EstudianteDAO, UsuarioDAO, CategoriaDAO, PrestamoDAO
├── controlador/                  LoginControlador, LibroControlador, EstudianteControlador, UsuarioControlador, PrestamoControlador, ReporteControlador
├── sincronizacion/               GestorPrestamos (synchronized), TareaPrestamo, TareaDevolucion, ResultadoTarea
└── vista/                        VentanaLogin, VentanaLibros, VentanaEstudiantes, VentanaUsuarios, VentanaPrestamos, VentanaReportes (JFrame), BarraNavegacion y UtilVista
sql/PRY2203_EFT_S9_Script_crea_tablas_biblioteca.sql     Script de creación de la base de datos
sql/PRY2203_EFT_S9_Script_poblado_tablas_biblioteca.sql  Script de poblamiento con datos de prueba
```

⚙️ Instrucciones para ejecutar el proyecto

git clone https://github.com/Pez7/EFT_DOO2

1. Tener MySQL en ejecución y ejecutar, en este orden, `sql/PRY2203_EFT_S9_Script_crea_tablas_biblioteca.sql` y `sql/PRY2203_EFT_S9_Script_poblado_tablas_biblioteca.sql` (MySQL Workbench o consola).
2. Ajustar usuario y contraseña en `src/main/java/util/DatabaseConnection.java`.
3. Abrir la carpeta en IntelliJ IDEA (proyecto Maven) y recargar las dependencias.
4. Ejecutar `Main`.

Para generar el ejecutable: `mvn package` crea `target/Biblioteca_S9-1.0-jar-with-dependencies.jar` (incluye el conector de MySQL), que se ejecuta con `java -jar`.

## Usuarios de prueba
| Rol | RUT | Contraseña |
|---|---|---|
| Bibliotecario | `12345678-9` | `clave123` |
| Estudiante | `98765432-1` | `clave123` |

## Formatos de entrada
- RUT: con guion y sin puntos (ej. `12345678-9`)
- Fechas mostradas: `dd-mm-aaaa` (ej. `05-10-2026`)

## Prueba de concurrencia
En *Préstamos*, seleccionar un libro con stock 2 y presionar *Prestar* tres veces seguidas (cambiando de estudiante). La ventana sigue respondiendo; se aprueban 2 préstamos y el tercero se rechaza por falta de stock. En la consola se ve cómo cada hilo entra por turno al método `synchronized`.
