package proyecto.controladores;

import com.mercadopago.resources.preference.Preference;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.modelo.documentos.Orden;
import proyecto.modelo.dto.autenticacion.MensajeDTO;
import proyecto.modelo.dto.carrito.DetalleCarritoDTO;
import proyecto.modelo.dto.carrito.InformacionCarritoDTO;
import proyecto.modelo.dto.cuenta.ActivarCuentaDTO;
import proyecto.modelo.dto.orden.CrearOrdenDTO;
import proyecto.modelo.dto.orden.EditarOrdenDTO;
import proyecto.modelo.dto.orden.InformacionOrdenDTO;
import proyecto.servicios.implementaciones.OrdenServicioImpl;
import proyecto.servicios.interfaces.CarritoServicio;
import proyecto.servicios.interfaces.CuentaServicio;

import java.util.List;

/**
 * Controlador que maneja las operaciones que puede realizar un cliente autenticado,
 * incluyendo gestión de órdenes, pagos, carrito de compras y activación de cuenta.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cliente")
public class ClienteControlador {

    private final CuentaServicio cuentaServicio;
    private final CarritoServicio carritoServicio;
    private final OrdenServicioImpl ordenServicio;

    // ---------------------------------------------
    // SECCIÓN: ORDENES Y PAGOS
    // ---------------------------------------------

    /**
     * Inicia el proceso de pago de una orden mediante la integración con MercadoPago.
     *
     * @param idOrden identificador de la orden a pagar
     * @return preferencia de pago generada por MercadoPago
     */
    @PostMapping("/realizar-pago")
    public ResponseEntity<MensajeDTO<Preference>> realizarPago(@RequestParam("idOrden") String idOrden) throws Exception {
        return ResponseEntity.ok().body(new MensajeDTO<>(false, ordenServicio.realizarPago(idOrden)));
    }

    /**
     * Crea una nueva orden de compra para el cliente.
     *
     * @param orden datos necesarios para la creación de la orden
     * @return mensaje de confirmación
     */
    @PostMapping("/crear-orden")
    public ResponseEntity<MensajeDTO<String>> crearOrden(@Valid @RequestBody CrearOrdenDTO orden) throws Exception {
        return ResponseEntity.ok(new MensajeDTO<>(false, ordenServicio.crearOrden(orden)));
    }

    /**
     * Actualiza la información de una orden existente.
     *
     * @param orden datos actualizados de la orden
     * @return mensaje de éxito
     */
    @PutMapping("/actualizar-orden")
    public ResponseEntity<MensajeDTO<String>> actualizarOrden(@Valid @RequestBody EditarOrdenDTO orden) throws Exception {
        ordenServicio.actualizarOrden(orden);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Orden actualizada exitosamente"));
    }

    /**
     * Elimina una orden específica del sistema.
     *
     * @param id identificador de la orden
     * @return mensaje de confirmación
     */
    @DeleteMapping("/eliminar-orden/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarCupon(@PathVariable String id) throws Exception {
        ordenServicio.eliminarOrden(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Orden eliminada exitosamente"));
    }

    /**
     * Obtiene información detallada de una orden específica.
     *
     * @param id identificador de la orden
     * @return detalles de la orden
     */
    @GetMapping("/obtener-informacion-orden/{id}")
    public ResponseEntity<MensajeDTO<InformacionOrdenDTO>> obtenerInformacionOrden(@PathVariable String id) throws Exception {
        InformacionOrdenDTO ordenInfo = ordenServicio.obtenerInformacionOrden(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, ordenInfo));
    }

    /**
     * Busca todas las órdenes asociadas a un cliente específico.
     *
     * @param id identificador del cliente
     * @return lista de órdenes
     */
    @GetMapping("/obtener-ordenes-cliente-orden/{id}")
    public ResponseEntity<MensajeDTO<List<Orden>>> buscarOrdenesPorCliente(@PathVariable String id) throws Exception {
        List<Orden> ordenesCliente = ordenServicio.buscarOrdenesPorCliente(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, ordenesCliente));
    }

    /**
     * Busca órdenes dentro de un rango de fechas determinado.
     *
     * @param d1 fecha inicial en formato texto
     * @param d2 fecha final en formato texto
     * @return lista de órdenes dentro del rango
     */
    @GetMapping("/obtener-ordenes-rango-fecha-orden/{dateOne}/{dateTwo}")
    public ResponseEntity<MensajeDTO<List<Orden>>> buscarOrdenesPorRangoDeFechas(@PathVariable("dateOne") String d1, @PathVariable("dateTwo") String d2) throws Exception {
        List<Orden> ordenesClientes = ordenServicio.buscarOrdenesPorRangoDeFechas(d1, d2);
        return ResponseEntity.ok(new MensajeDTO<>(false, ordenesClientes));
    }

    /**
     * Lista todas las órdenes disponibles en el sistema.
     *
     * @return lista de órdenes con información detallada
     */
    @GetMapping("/obtener-ordenes-orden")
    public ResponseEntity<MensajeDTO<List<InformacionOrdenDTO>>> buscarOrdenes() throws Exception {
        List<InformacionOrdenDTO> ordenesCliente = ordenServicio.listarTodasLasOrdenes();
        return ResponseEntity.ok(new MensajeDTO<>(false, ordenesCliente));
    }

    // ---------------------------------------------
    // SECCIÓN: CARRITO DE COMPRAS
    // ---------------------------------------------

    /**
     * Agrega un nuevo ítem al carrito del cliente.
     *
     * @param id identificador del cliente
     * @param item detalle del producto a agregar
     * @return mensaje de confirmación
     */
    @PostMapping("/agregarItem-carrito/{id}")
    public ResponseEntity<MensajeDTO<String>> agregarItem(@PathVariable String id, @RequestBody DetalleCarritoDTO item) throws Exception {
        carritoServicio.agregarItem(id, item);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Item agregado correctamente"));
    }

    /**
     * Agrega un ítem único al carrito, reemplazando si ya existe.
     *
     * @param id identificador del cliente
     * @param item detalle del producto a agregar
     * @return mensaje de confirmación
     */
    @PostMapping("/agregarItem-carrito-unico/{id}")
    public ResponseEntity<MensajeDTO<String>> agregarItemUnico(@PathVariable String id, @RequestBody DetalleCarritoDTO item) throws Exception {
        carritoServicio.agregarItemUnico(id, item);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Item agregado correctamente"));
    }

    /**
     * Edita un ítem existente en el carrito.
     *
     * @param id identificador del cliente
     * @param item detalle actualizado del ítem
     * @return mensaje de confirmación
     */
    @PostMapping("/editarItem-carrito/{id}")
    public ResponseEntity<MensajeDTO<String>> editarItem(@PathVariable String id, @RequestBody DetalleCarritoDTO item) throws Exception {
        carritoServicio.editarItem(id, item);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Item editado correctamente"));
    }

    /**
     * Elimina un ítem específico del carrito del cliente.
     *
     * @param id identificador del cliente
     * @param idDetalleCarrito identificador del ítem dentro del carrito
     * @return mensaje de confirmación
     */
    @PutMapping("/eliminarItem-carrito/{id}/{idDetalleCarrito}")
    public ResponseEntity<MensajeDTO<String>> eliminarItem(@PathVariable String id, @PathVariable String idDetalleCarrito) throws Exception {
        carritoServicio.eliminarItem(id, idDetalleCarrito);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Item eliminado correctamente"));
    }

    /**
     * Obtiene la información del carrito de un cliente específico.
     *
     * @param id identificador del cliente
     * @return información del carrito
     */
    @GetMapping("/traerCarrito-carrito/{id}")
    public ResponseEntity<MensajeDTO<InformacionCarritoDTO>> traerCarrito(@PathVariable String id) throws Exception {
        return ResponseEntity.ok(new MensajeDTO<>(false, carritoServicio.traerCarritoCliente(id)));
    }

    /**
     * Obtiene el carrito a partir de su identificador directo.
     *
     * @param id identificador del carrito
     * @return información del carrito
     */
    @GetMapping("/traerCarrito-carritoId/{id}")
    public ResponseEntity<MensajeDTO<InformacionCarritoDTO>> traerCarritoPorId(@PathVariable String id) throws Exception {
        return ResponseEntity.ok(new MensajeDTO<>(false, carritoServicio.traerCarrito(id)));
    }

    // ---------------------------------------------
    // SECCIÓN: CUENTA Y HISTORIAL
    // ---------------------------------------------

    /**
     * Activa una cuenta de usuario mediante un token o código de activación.
     *
     * @param activarCuentaDTO datos necesarios para activar la cuenta
     * @return mensaje de éxito
     */
    @PutMapping("/activar-cuenta")
    public ResponseEntity<MensajeDTO<String>> activarCuenta(@RequestBody ActivarCuentaDTO activarCuentaDTO) throws Exception {
        cuentaServicio.activarCuenta(activarCuentaDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta activada exitosamente."));
    }

    /**
     * Obtiene el historial de órdenes realizadas por un cliente.
     *
     * @param idCliente identificador del cliente
     * @return lista con las órdenes históricas del cliente
     */
    @GetMapping("/obtenerHistorialOrdenes/{idCliente}")
    public ResponseEntity<MensajeDTO<List<InformacionOrdenDTO>>> obtenerHistorialOrdenes(@PathVariable String idCliente) throws Exception {
        List<InformacionOrdenDTO> historial = ordenServicio.obtenerHistorialOrdenes(idCliente);
        return ResponseEntity.ok(new MensajeDTO<>(false, historial));
    }
}
