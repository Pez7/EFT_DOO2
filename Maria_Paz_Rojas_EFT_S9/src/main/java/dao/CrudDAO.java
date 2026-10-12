package dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Contrato común para los DAO con operaciones CRUD.
 * Cada DAO lo implementa para su propia entidad (T).
 */
public interface CrudDAO<T> {

    // Inserta un nuevo registro
    void create(T entidad) throws SQLException;

    // Retorna todos los registros
    List<T> readAll() throws SQLException;

    // Actualiza un registro existente
    void update(T entidad) throws SQLException;

    // Elimina un registro por su id
    void delete(int id) throws SQLException;
}
