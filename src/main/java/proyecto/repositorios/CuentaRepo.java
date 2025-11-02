package proyecto.repositorios;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import proyecto.modelo.documentos.Cuenta;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de la entidad Cuenta.
 * Proporciona métodos personalizados para realizar consultas sobre la colección "cuentas"
 * en la base de datos MongoDB.
 */
@Repository
public interface CuentaRepo extends MongoRepository<Cuenta, String> {

    /**
     * Busca una cuenta por su dirección de correo electrónico.
     *
     * @param email correo electrónico de la cuenta
     * @return un Optional con la cuenta encontrada o vacío si no existe
     */
    @Query("{ 'email' : ?0 }")
    Optional<Cuenta> buscaremail(String email);

    /**
     * Busca una cuenta por la cédula del usuario asociado.
     *
     * @param cedula número de cédula del usuario
     * @return un Optional con la cuenta correspondiente o vacío si no existe
     */
    @Query("{ 'usuario.cedula' : ?0 }")
    Optional<Cuenta> buscarCedula(String cedula);

    /**
     * Valida las credenciales de autenticación de un usuario (correo y contraseña).
     *
     * @param correo correo electrónico del usuario
     * @param password contraseña del usuario
     * @return un Optional con la cuenta si las credenciales son correctas
     */
    @Query("{email: ?0, password: ?1 }")
    Optional<Cuenta> validarDatosAutenticacion(String correo, String password);

    /**
     * Busca una cuenta por correo electrónico utilizando la convención de nombres
     * de Spring Data (sin necesidad de usar una consulta personalizada).
     *
     * @param email correo electrónico de la cuenta
     * @return un Optional con la cuenta encontrada o vacío si no existe
     */
    Optional<Cuenta> findByEmail(String email);

    /**
     * Busca una cuenta según su código de validación de registro.
     *
     * @param token código de validación asociado a la cuenta
     * @return un Optional con la cuenta si el código es válido
     */
    @Query("{ 'codigoValidacionRegistro.codigo': ?0 }")
    Optional<Cuenta> buscarPorCodigoValidacion(String token);

    /**
     * Busca todas las cuentas que tengan boletas asociadas a un evento específico.
     *
     * @param nombreEvento nombre del evento
     * @return lista de cuentas que compraron boletas para el evento indicado
     */
    @Query("{ 'boletas.nombreEvento' : ?0 }")
    List<Cuenta> buscarBoletaPorNombreEvento(String nombreEvento);
}
