package proyecto.servicios.interfaces;

import com.mercadopago.resources.preference.Preference;
import proyecto.modelo.documentos.Orden;
import proyecto.modelo.dto.orden.*;

import java.util.List;
import java.util.Map;

/**
 * Servicio encargado de la gestión de órdenes dentro del sistema.
 *
 * Define las operaciones necesarias para crear, actualizar, eliminar y consultar órdenes,
 * así como la integración con el sistema de pagos (por ejemplo, MercadoPago).
 */
public interface OrdenServicio {

    /**
     * Crea una nueva orden a partir de los datos proporcionados.
     *
     * @param crearOrdenDTO objeto {@link CrearOrdenDTO} que contiene los datos necesarios para crear la orden.
     * @return un mensaje de confirmación indicando que la orden fue creada exitosamente.
     * @throws Exception si ocurre algún error durante el proceso de creación.
     */
    String crearOrden(CrearOrdenDTO crearOrdenDTO) throws Exception;

    /**
     * Actualiza la información de una orden existente.
     *
     * @param editarOrdenDTO objeto {@link EditarOrdenDTO} con los nuevos datos de la orden.
     * @return un mensaje indicando el resultado de la actualización.
     * @throws Exception si la orden no existe o ocurre un error en la actualización.
     */
    String actualizarOrden(EditarOrdenDTO editarOrdenDTO) throws Exception;

    /**
     * Elimina una orden del sistema utilizando su identificador.
     *
     * @param idOrden identificador único de la orden a eliminar.
     * @return un mensaje indicando si la orden fue eliminada correctamente.
     * @throws Exception si la orden no existe o no puede eliminarse.
     */
    String eliminarOrden(String idOrden) throws Exception;

    /**
     * Busca todas las órdenes asociadas a un cliente específico.
     *
     * @param idCliente identificador del cliente.
     * @return una lista de {@link Orden} pertenecientes al cliente.
     * @throws Exception si no se encuentran órdenes o ocurre un error en la búsqueda.
     */
    List<Orden> buscarOrdenesPorCliente(String idCliente) throws Exception;

    /**
     * Busca las órdenes registradas dentro de un rango de fechas determinado.
     *
     * @param fechaInicio fecha inicial en formato de texto (por ejemplo, "2025-01-01").
     * @param fechaFin fecha final en formato de texto.
     * @return una lista de {@link Orden} dentro del rango de fechas especificado.
     * @throws Exception si el formato de fecha es incorrecto o no se encuentran órdenes.
     */
    List<Orden> buscarOrdenesPorRangoDeFechas(String fechaInicio, String fechaFin) throws Exception;

    /**
     * Obtiene la información detallada de una orden.
     *
     * @param idOrden identificador de la orden.
     * @return un objeto {@link InformacionOrdenDTO} con todos los datos relevantes de la orden.
     * @throws Exception si la orden no existe o no puede obtenerse.
     */
    InformacionOrdenDTO obtenerInformacionOrden(String idOrden) throws Exception;

    /**
     * Lista todas las órdenes registradas en el sistema.
     *
     * @return una lista de {@link InformacionOrdenDTO} con la información de cada orden.
     * @throws Exception si ocurre un error durante la consulta.
     */
    List<InformacionOrdenDTO> listarTodasLasOrdenes() throws Exception;

    /**
     * Recibe y procesa las notificaciones enviadas por MercadoPago sobre el estado de los pagos.
     *
     * @param request cuerpo de la notificación recibido desde la API de MercadoPago.
     * @throws Exception si ocurre un error al procesar la notificación.
     */
    void recibirNotificacionMercadoPago(Map<String, Object> request) throws Exception;

    /**
     * Inicia el proceso de pago de una orden a través de la plataforma MercadoPago.
     *
     * @param idOrden identificador de la orden a pagar.
     * @return un objeto {@link Preference} con los detalles de la transacción generada.
     * @throws Exception si ocurre un error durante la creación de la preferencia de pago.
     */
    Preference realizarPago(String idOrden) throws Exception;

    /**
     * Obtiene el historial completo de órdenes de un cliente.
     *
     * @param idCliente identificador del cliente.
     * @return una lista de {@link InformacionOrdenDTO} con las órdenes realizadas por el cliente.
     * @throws Exception si no existen registros o hay un error en la consulta.
     */
    List<InformacionOrdenDTO> obtenerHistorialOrdenes(String idCliente) throws Exception;
}
