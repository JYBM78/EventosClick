package proyecto.repositorios;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import proyecto.modelo.documentos.Usuario;

/**
 * Repositorio para la entidad Usuario.
 *
 * Esta interfaz permite realizar operaciones CRUD (crear, leer, actualizar y eliminar)
 * sobre los documentos de tipo Usuario en la base de datos MongoDB.
 *
 * Al extender MongoRepository, hereda métodos como:
 * - save() para guardar un usuario
 * - findById() para buscar por ID
 * - findAll() para listar todos los usuarios
 * - deleteById() para eliminar un usuario
 */
@Repository
public interface UsuarioRepo extends MongoRepository<Usuario, String> {
}
