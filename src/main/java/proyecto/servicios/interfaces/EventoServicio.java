package proyecto.servicios.interfaces;

import proyecto.modelo.documentos.Evento;
import proyecto.modelo.dto.evento.*;
import proyecto.modelo.enums.TipoEvento;

import java.util.List;

/**
 * Servicio encargado de la gestión de eventos dentro del sistema.
 *
 * Esta interfaz define las operaciones principales para crear, editar, eliminar,
 * consultar y filtrar eventos, tanto para usuarios como para administradores.
 * También provee métodos para obtener información relacionada con los tipos de eventos
 * y la preferencia de los usuarios.
 */
public interface EventoServicio {

    /**
     * Crea un nuevo evento en el sistema.
     *
     * @param crearEventoDTO objeto que contiene los datos necesarios para registrar un evento.
     * @return un mensaje indicando el resultado de la creación.
     * @throws Exception si ocurre algún error al registrar el evento.
     */
    String crearEvento(CrearEventoDTO crearEventoDTO) throws Exception;

    /**
     * Edita la información de un evento existente.
     *
     * @param editarEventoDTO objeto con los datos actualizados del evento.
     * @return un mensaje indicando el resultado de la operación.
     * @throws Exception si el evento no existe o ocurre un error en la actualización.
     */
    String editarEvento(EditarEventoDTO editarEventoDTO) throws Exception;

    /**
     * Elimina un evento identificado por su ID.
     *
     * @param id identificador único del evento.
     * @return un mensaje indicando si la eliminación fue exitosa.
     * @throws Exception si el evento no se encuentra o no puede eliminarse.
     */
    String eliminarEvento(String id) throws Exception;

    /**
     * Obtiene la información detallada de un evento por su ID.
     *
     * @param id identificador del evento.
     * @return un objeto {@link InformacionEventoDTO} con los datos del evento.
     * @throws Exception si el evento no existe.
     */
    InformacionEventoDTO obtenerInformacionEvento(String id) throws Exception;

    /**
     * Lista los eventos disponibles para los usuarios (visibles al público).
     *
     * @return una lista de objetos {@link ItemEventoDTO}.
     * @throws Exception si ocurre un error al obtener la información.
     */
    List<ItemEventoDTO> listarEventos() throws Exception;

    /**
     * Lista todos los eventos, incluyendo los no activos.
     *
     * @return una lista de objetos {@link InformacionEventoDTO}.
     * @throws Exception si ocurre un error en la consulta.
     */
    List<InformacionEventoDTO> listarTodosEvento() throws Exception;

    /**
     * Lista todos los eventos actualmente activos en el sistema.
     *
     * @return una lista de objetos {@link ItemEventoDTO}.
     * @throws Exception si ocurre un error en la obtención de datos.
     */
    List<ItemEventoDTO> listarEventosActivos() throws Exception;

    /**
     * Filtra los eventos que ocurrirán en el futuro.
     *
     * @return una lista de eventos futuros.
     * @throws Exception si ocurre un error durante la búsqueda.
     */
    List<ItemEventoDTO> filtrarEventosFuturos() throws Exception;

    /**
     * Aplica filtros personalizados sobre los eventos (por tipo, ciudad, nombre, etc.).
     *
     * @param filtroEventoDTO objeto que contiene los criterios de filtrado.
     * @return una lista de eventos que cumplen con los filtros aplicados.
     * @throws Exception si ocurre un error en la consulta.
     */
    List<ItemEventoDTO> filtrarEventos(FiltroEventoDTO filtroEventoDTO) throws Exception;

    /**
     * Obtiene un evento completo a partir de su ID.
     *
     * @param id identificador único del evento.
     * @return el objeto {@link Evento} correspondiente.
     * @throws Exception si el evento no existe.
     */
    Evento obtenerEvento(String id) throws Exception;

    /**
     * Devuelve la lista de tipos de eventos disponibles.
     *
     * @return una lista de valores del enumerado {@link TipoEvento}.
     */
    List<TipoEvento> obtenerTipoEventos();

    /**
     * Lista los eventos con información completa para el panel de administración.
     *
     * @return una lista de {@link InformacionEventoDTO}.
     * @throws Exception si ocurre un error al obtener los datos.
     */
    List<InformacionEventoDTO> listarEventosAdmin() throws Exception;

    /**
     * Obtiene los eventos que coinciden con las preferencias de un usuario
     * según sus tipos de eventos de interés.
     *
     * @param tipos lista de tipos de eventos preferidos.
     * @return una lista de eventos que se ajustan a las preferencias del usuario.
     * @throws Exception si ocurre un error durante la búsqueda.
     */
    List<Evento> traerEventosPorPreferenciaUsuario(List<TipoEvento> tipos) throws Exception;
}
