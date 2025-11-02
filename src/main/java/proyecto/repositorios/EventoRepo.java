package proyecto.repositorios;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import proyecto.modelo.documentos.Evento;
import proyecto.modelo.enums.TipoEvento;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de la entidad Evento.
 * Permite realizar consultas personalizadas sobre la colección de eventos
 * almacenada en MongoDB.
 */
@Repository
public interface EventoRepo extends MongoRepository<Evento, String> {

    /**
     * Filtra eventos activos según una lista de tipos de evento.
     *
     * @param tipos lista de tipos de evento a filtrar
     * @return lista de eventos activos que coinciden con los tipos indicados
     */
    @Query("{ 'tipo' : { $in: ?0 }, 'estado': 'ACTIVO' }")
    List<Evento> filtrarEventosPorTipos(List<TipoEvento> tipos);

    /**
     * Busca un evento específico por nombre, fecha y ciudad.
     *
     * @param nombreEvento nombre del evento
     * @param fechaEvento fecha del evento
     * @param ciudad ciudad donde se realiza el evento
     * @return un Optional con el evento encontrado o vacío si no existe
     */
    @Query("{nombre : ?0, fechaEvento: ?1, ciudad : ?2}")
    Optional<Evento> buscarEvento(String nombreEvento, LocalDate fechaEvento, String ciudad);

    /**
     * Filtra eventos según nombre, tipo y ciudad,
     * mostrando solo aquellos con estado activo.
     *
     * @param nombreEvento nombre del evento
     * @param tipo tipo de evento
     * @param ciudad ciudad donde se realiza el evento
     * @return lista de eventos que coinciden con los filtros dados
     */
    @Query("{nombre : ?0, tipo: ?1, ciudad : ?2, estado: ACTIVO}")
    List<Evento> filtrarEventos(String nombreEvento, TipoEvento tipo, String ciudad);

    /**
     * Lista todos los eventos que se encuentran en estado activo.
     *
     * @return lista de eventos activos
     */
    @Query("{estado: ACTIVO}")
    List<Evento> listarEventosActivos();

    /**
     * Filtra eventos futuros dentro de un rango de fechas,
     * mostrando solo aquellos que aún están activos.
     *
     * @param fechaInicio fecha mínima (desde)
     * @param fechaFin fecha máxima (hasta)
     * @return lista de eventos futuros activos dentro del rango
     */
    @Query("{fecha: {$gte: ?0, $lte: ?1, estado : ACTIVO}}")
    List<Evento> filtrarEventosFuturos(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
