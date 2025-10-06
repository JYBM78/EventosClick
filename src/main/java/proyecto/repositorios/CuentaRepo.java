package proyecto.repositorios;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import proyecto.modelo.documentos.Cuenta;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaRepo extends MongoRepository<Cuenta, String> {

    @Query("{ 'email' : ?0 }")
    Optional<Cuenta> buscaremail(String email);

    @Query("{ 'usuario.cedula' : ?0 }")
    Optional<Cuenta> buscarCedula(String cedula);

    @Query("{email: ?0, password: ?1 }")
    Optional<Cuenta> validarDatosAutenticacion(String correo, String password);

    Optional<Cuenta> findByEmail(String email);

    @Query("{ 'codigoValidacionRegistro.codigo': ?0 }")
    Optional<Cuenta> buscarPorCodigoValidacion(String token);

    @Query("{ 'boletas.nombreEvento' : ?0 }")
    List<Cuenta> buscarBoletaPorNombreEvento(String nombreEvento);


}
