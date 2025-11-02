package proyecto.repositorios;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import proyecto.modelo.documentos.Carrito;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Carrito.
 *
 * Esta interfaz gestiona las operaciones CRUD y consultas personalizadas
 * sobre los documentos de tipo Carrito almacenados en MongoDB.
 */
@Repository
public interface CarritoRepo extends MongoRepository<Carrito, String> {

    /**
     * Busca un carrito asociado a un usuario específico mediante su identificador.
     *
     * @param idUsuario identificador del usuario
     * @return un Optional que contiene el carrito si existe
     */
    @Query("{ 'idUsuario' : ?0 }")
    Optional<Carrito> buscarCarritoPorIdUsuario(String idUsuario);

    /**
     * Busca un carrito específico por su identificador único.
     *
     * @param idCarrito identificador del carrito
     * @return un Optional con el carrito encontrado o vacío si no existe
     */
    @Query("{ '_id' : ?0 }")
    Optional<Carrito> buscarCarritoPorId(String idCarrito);

    /**
     * Obtiene todos los carritos que contienen un evento específico dentro de sus ítems.
     *
     * @param idEvento identificador del evento
     * @return lista de carritos que contienen el evento indicado
     */
    @Query("{ 'items.idEvento' : ?0 }")
    List<Carrito> buscarCarritosPorEvento(ObjectId idEvento);

    /**
     * Busca todos los carritos que tienen más de una cantidad determinada de ítems.
     *
     * @param cantidad número mínimo de ítems que debe tener el carrito
     * @return lista de carritos que superan la cantidad indicada
     */
    @Query("{ 'items.cantidad' : { $gt: ?0 } }")
    List<Carrito> buscarCarritosConMasDeXItems(int cantidad);
}
