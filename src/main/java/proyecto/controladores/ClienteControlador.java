package proyecto.controladores;


import com.mercadopago.resources.preference.Preference;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
import proyecto.modelo.vo.DetalleCarrito;
import proyecto.servicios.implementaciones.OrdenServicioImpl;
import proyecto.servicios.interfaces.CarritoServicio;
import proyecto.servicios.interfaces.CuentaServicio;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cliente")
public class ClienteControlador {

    private final CuentaServicio cuentaServicio;


    private final CarritoServicio carritoServicio;


    //Orden
    private final OrdenServicioImpl ordenServicio;



    @PostMapping("/realizar-pago")
    public ResponseEntity<MensajeDTO<Preference>> realizarPago(@RequestParam("idOrden") String idOrden) throws Exception{
        return ResponseEntity.ok().body(new MensajeDTO<>(false, ordenServicio.realizarPago(idOrden)));
    }

    @PostMapping("/crear-orden")
    public ResponseEntity<MensajeDTO<String>> crearOrden(@Valid @RequestBody CrearOrdenDTO orden) throws Exception {

        return ResponseEntity.ok(new MensajeDTO<>(false, ordenServicio.crearOrden(orden)));

    }
    @PutMapping("/actualizar-orden")
    public ResponseEntity<MensajeDTO<String>> actualizarOrden(@Valid @RequestBody EditarOrdenDTO orden) throws Exception {
        ordenServicio.actualizarOrden(orden);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Orden actualizado exitosamente"));
    }

    @DeleteMapping("/eliminar-orden/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarCupon(@PathVariable String id) throws Exception {
        ordenServicio.eliminarOrden(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Orden eliminado exitosamente"));
    }

    @GetMapping("/obtener-informacion-orden/{id}")
    public ResponseEntity<MensajeDTO<InformacionOrdenDTO>> obtenerInformacionOrden(@PathVariable String id) throws Exception {
        InformacionOrdenDTO cuponInfo = ordenServicio.obtenerInformacionOrden(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, cuponInfo));
    }
    @GetMapping("/obtener-ordenes-cliente-orden/{id}")
    public ResponseEntity<MensajeDTO<List<Orden>>> buscarOrdenesPorCliente(@PathVariable String id) throws Exception {
        List<Orden> ordenesCliente = ordenServicio.buscarOrdenesPorCliente(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, ordenesCliente));
    }
    @GetMapping("/obtener-ordenes-rango-fecha-orden/{dateOne}/{dateTwo}")
    public ResponseEntity<MensajeDTO<List<Orden>>> buscarOrdenesPorRangoDeFechas(@PathVariable("dateOne")String d1, @PathVariable("dateTwo")String d2) throws Exception {
        List<Orden> ordenesClientes = ordenServicio.buscarOrdenesPorRangoDeFechas(d1,d2);
        return ResponseEntity.ok(new MensajeDTO<>(false, ordenesClientes));
    }
    @GetMapping("/obtener-ordenes-orden")
    public ResponseEntity<MensajeDTO<List<InformacionOrdenDTO>>> buscarOrdenes() throws Exception {
        List<InformacionOrdenDTO> ordenesCliente = ordenServicio.listarTodasLasOrdenes();
        return ResponseEntity.ok(new MensajeDTO<>(false, ordenesCliente));
    }

    //Carrito
    @PostMapping("/agregarItem-carrito/{id}")
    public ResponseEntity<MensajeDTO<String>> agregarItem(@PathVariable String id,@RequestBody DetalleCarritoDTO item) throws Exception {
        carritoServicio.agregarItem(id, item);
        return ResponseEntity.ok(new MensajeDTO<>(false,"Item agregado correctamente"));
    }


    @PostMapping("/agregarItem-carrito-unico/{id}")
    public ResponseEntity<MensajeDTO<String>> agregarItemUnico(@PathVariable String id,@RequestBody DetalleCarritoDTO item) throws Exception {
        carritoServicio.agregarItemUnico(id, item);
        return ResponseEntity.ok(new MensajeDTO<>(false,"Item agregado correctamente"));
    }


    @PostMapping("/editarItem-carrito/{id}")
    public ResponseEntity<MensajeDTO<String>> editarItem(@PathVariable String id,@RequestBody DetalleCarritoDTO item) throws Exception {
        carritoServicio.editarItem(id, item);
        return ResponseEntity.ok(new MensajeDTO<>(false,"Item editado correctamente"));
    }

    @PutMapping("/eliminarItem-carrito/{id}/{idDetalleCarrito}")
    public ResponseEntity<MensajeDTO<String>> eliminarItem(@PathVariable String id, @PathVariable String idDetalleCarrito) throws Exception {
        carritoServicio.eliminarItem(id, idDetalleCarrito);
        return ResponseEntity.ok(new MensajeDTO<>(false,"Item eliminado correctamente"));
    }
    @GetMapping("/traerCarrito-carrito/{id}")
    public ResponseEntity<MensajeDTO<InformacionCarritoDTO>>  traerCArrito(@PathVariable String id) throws Exception {
        return  ResponseEntity.ok(new MensajeDTO<>(false,carritoServicio.traerCarritoCliente(id)));
    }
    @GetMapping("/traerCarrito-carritoId/{id}")
    public ResponseEntity<MensajeDTO<InformacionCarritoDTO>>  traerCarritoPorId(@PathVariable String id) throws Exception {
        return  ResponseEntity.ok(new MensajeDTO<>(false,carritoServicio.traerCarrito(id)));
    }


    @PutMapping("/activar-cuenta")
    public ResponseEntity<MensajeDTO<String>> activarCuenta(@RequestBody ActivarCuentaDTO activarCuentaDTO) throws Exception {
        cuentaServicio.activarCuenta(activarCuentaDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta activada exitosamente."));
    }

    @GetMapping("/obtenerHistorialOrdenes/{idCliente}")
    public ResponseEntity<MensajeDTO<List<InformacionOrdenDTO>>> obtenerHistorialOrdenes(@PathVariable String idCliente) throws Exception{
        List<InformacionOrdenDTO> historial = ordenServicio.obtenerHistorialOrdenes(idCliente);
        return ResponseEntity.ok(new MensajeDTO<>(false, historial));
    }

}
