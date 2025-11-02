package proyecto.repositorios;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import proyecto.modelo.documentos.Orden;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Orden.
 * Proporciona operaciones CRUD y consultas personalizadas sobre las órdenes
 * almacenadas en MongoDB.
 */
@Repository
public interface OrdenRepo extends MongoRepository<Orden, String> {

    /**
     * Busca una orden específica por su identificador.
     *
     * @param id identificador de la orden
     * @return un Optional que contiene la orden si existe
     */
    @Query("{id : id}")
    Optional<Orden> buscarOrden(String id);

    /**
     * Obtiene todas las órdenes asociadas a un cliente específico.
     *
     * @param idCliente identificador del cliente
     * @return lista de órdenes realizadas por el cliente
     */
    @Query("{ 'idCliente' : ?0 }")
    List<Orden> buscarOrdenesPorCliente(String idCliente);

    /**
     * Busca las órdenes que se realizaron dentro de un rango de fechas.
     *
     * @param fechaInicio fecha inicial del rango
     * @param fechaFin fecha final del rango
     * @return lista de órdenes dentro del rango de fechas especificado
     */
    @Query("{ 'fecha' : { $gte: ?0, $lte: ?1 } }")
    List<Orden> buscarOrdenesPorRangoDeFechas(Date fechaInicio, Date fechaFin);

    /**
     * Busca las órdenes asociadas a un cliente y a un evento específico.
     *
     * @param idCliente identificador del cliente
     * @param idEvento identificador del evento
     * @return lista de órdenes del cliente relacionadas con el evento indicado
     */
    @Query("{ 'idCliente': ?0, 'items.idEvento': ?1 }")
    List<Orden> buscarOrdenesPorClienteYEvento(String idCliente, String idEvento);
}
