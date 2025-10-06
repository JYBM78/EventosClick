package proyecto.repositorios;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import proyecto.modelo.documentos.Usuario;

@Repository
public interface UsuarioRepo extends MongoRepository<Usuario, String> {
}
