package proyecto.servicios.interfaces;

import org.springframework.stereotype.Service;
import proyecto.modelo.dto.carrito.DetalleCarritoDTO;
import proyecto.modelo.dto.carrito.InformacionCarritoDTO;

/**
 * Servicio que define las operaciones relacionadas con la gestión del carrito de compras.
 *
 * Esta interfaz especifica las acciones que se pueden realizar sobre un carrito,
 * como agregar, editar o eliminar ítems, así como obtener información detallada del carrito.
 */
@Service
public interface CarritoServicio {

    /**
     * Elimina un ítem específico del carrito.
     *
     * @param idCarrito identificador del carrito
     * @param idEvento identificador del evento asociado al ítem
     * @return mensaje indicando el resultado de la eliminación
     * @throws Exception si ocurre un error durante el proceso
     */
    String eliminarItem(String idCarrito, String idEvento) throws Exception;

    /**
     * Agrega un nuevo ítem al carrito.
     * Si el ítem ya existe, se puede actualizar su cantidad o detalles.
     *
     * @param idCarrito identificador del carrito
     * @param item objeto con los datos del ítem a agregar
     * @throws Exception si ocurre un error al agregar el ítem
     */
    void agregarItem(String idCarrito, DetalleCarritoDTO item) throws Exception;

    /**
     * Agrega un ítem único al carrito del usuario.
     *
     * A diferencia del método anterior, este asegura que solo exista una instancia del ítem en el carrito.
     *
     * @param idCuenta identificador de la cuenta del usuario
     * @param item datos del ítem a agregar
     * @throws Exception si ocurre un error durante el proceso
     */
    void agregarItemUnico(String idCuenta, DetalleCarritoDTO item) throws Exception;

    /**
     * Edita los datos de un ítem dentro del carrito, como su cantidad o precio.
     *
     * @param idCarrito identificador del carrito
     * @param item datos actualizados del ítem
     * @throws Exception si ocurre un error durante la edición
     */
    void editarItem(String idCarrito, DetalleCarritoDTO item) throws Exception;

    /**
     * Obtiene la información detallada de un carrito específico.
     *
     * @param idCarrito identificador del carrito
     * @return objeto con la información completa del carrito
     * @throws Exception si el carrito no existe o ocurre un error en la consulta
     */
    InformacionCarritoDTO traerCarrito(String idCarrito) throws Exception;

    /**
     * Obtiene el carrito asociado a una cuenta de usuario.
     *
     * @param idCuenta identificador de la cuenta del usuario
     * @return información detallada del carrito del cliente
     * @throws Exception si no se encuentra un carrito asociado o ocurre un error
     */
    InformacionCarritoDTO traerCarritoCliente(String idCuenta) throws Exception;

    /**
     * Vacía completamente el contenido del carrito, eliminando todos los ítems.
     *
     * @param idCarrito identificador del carrito
     * @throws Exception si ocurre un error durante la operación
     */
    void vaciarCarrito(String idCarrito) throws Exception;
}
