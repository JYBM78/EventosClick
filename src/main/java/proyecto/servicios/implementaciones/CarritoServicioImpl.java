package proyecto.servicios.implementaciones;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyecto.modelo.documentos.Carrito;
import proyecto.modelo.dto.carrito.DetalleCarritoDTO;
import proyecto.modelo.dto.carrito.InformacionCarritoDTO;
import proyecto.modelo.vo.DetalleCarrito;
import proyecto.repositorios.CarritoRepo;
import proyecto.repositorios.CuentaRepo;
import proyecto.servicios.interfaces.CarritoServicio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio {@link CarritoServicio}.
 *
 * Se encarga de la gestión completa del carrito de compras de un usuario:
 * agregar, editar, eliminar, vaciar y obtener la información del carrito.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CarritoServicioImpl implements CarritoServicio {

    @Autowired
    private final CarritoRepo carritoRepo;

    private final CuentaRepo cuentaRepo;

    /**
     * Elimina un ítem del carrito según su identificador.
     *
     * @param idCarrito identificador del carrito.
     * @param idDetalleCarrito identificador del ítem dentro del carrito.
     * @return mensaje de confirmación si la eliminación fue exitosa.
     * @throws Exception si el carrito o el ítem no existen.
     */
    @Override
    public String eliminarItem(String idCarrito, String idDetalleCarrito) throws Exception {
        Optional<Carrito> carrito = carritoRepo.findById(idCarrito);
        if (carrito.isEmpty()) {
            throw new Exception("Carrito no encontrado");
        }

        Optional<DetalleCarrito> detalleCarrito = carrito.get().getItems().stream()
                .filter(x -> x.getIdDetalleCarrito().equals(idDetalleCarrito))
                .findFirst();

        if (detalleCarrito.isEmpty()) {
            throw new Exception("Detalle del carrito no encontrado");
        }

        carrito.get().getItems().remove(detalleCarrito.get());
        carritoRepo.save(carrito.get());
        return "Item eliminado correctamente";
    }

    /**
     * Agrega un ítem al carrito. Si el ítem ya existe, incrementa su cantidad.
     *
     * @param idCarrito identificador del carrito.
     * @param item objeto {@link DetalleCarritoDTO} con la información del ítem.
     * @throws Exception si el carrito no se encuentra.
     */
    @Override
    public void agregarItem(String idCarrito, DetalleCarritoDTO item) throws Exception {
        Optional<Carrito> carrito = carritoRepo.findById(idCarrito);

        if (carrito.isPresent()) {
            Carrito carritoActual = carrito.get();

            Optional<DetalleCarrito> itemExistente = carritoActual.getItems().stream()
                    .filter(i -> i.getIdEvento().equals(item.idEvento()))
                    .findFirst();

            if (itemExistente.isPresent()) {
                DetalleCarrito detalleExistente = itemExistente.get();
                detalleExistente.setCantidad(detalleExistente.getCantidad() + item.cantidad());
            } else {
                DetalleCarrito detalleCarrito = new DetalleCarrito();
                detalleCarrito.setIdDetalleCarrito(item.idDetalleCarrito());
                detalleCarrito.setCantidad(item.cantidad());
                detalleCarrito.setIdEvento(item.idEvento());
                detalleCarrito.setNombreLocalidad(item.nombreLocalidad());
                detalleCarrito.setPrecioUnitario(item.precioUnitario());
                carritoActual.getItems().add(detalleCarrito);
            }

            carritoRepo.save(carritoActual);
        } else {
            throw new Exception("Carrito no encontrado");
        }
    }

    /**
     * Agrega un ítem único al carrito del cliente. Si el mismo evento y localidad existen,
     * incrementa la cantidad en lugar de duplicarlo.
     *
     * @param idCuenta identificador del cliente.
     * @param item objeto {@link DetalleCarritoDTO} con la información del ítem.
     * @throws Exception si el carrito del cliente no se encuentra.
     */
    @Override
    public void agregarItemUnico(String idCuenta, DetalleCarritoDTO item) throws Exception {
        Optional<Carrito> carritoOpt = carritoRepo.buscarCarritoPorIdUsuario(idCuenta);

        if (carritoOpt.isEmpty()) {
            throw new Exception("Carrito no encontrado");
        }

        Carrito carrito = carritoOpt.get();

        Optional<DetalleCarrito> itemExistente = carrito.getItems().stream()
                .filter(i -> i.getIdEvento().equals(item.idEvento()) &&
                        i.getNombreLocalidad().equalsIgnoreCase(item.nombreLocalidad()))
                .findFirst();

        if (itemExistente.isPresent()) {
            DetalleCarrito detalleExistente = itemExistente.get();
            detalleExistente.setCantidad(detalleExistente.getCantidad() + item.cantidad());
        } else {
            DetalleCarrito nuevoItem = new DetalleCarrito();
            nuevoItem.setIdDetalleCarrito(item.idDetalleCarrito());
            nuevoItem.setIdEvento(item.idEvento());
            nuevoItem.setNombreLocalidad(item.nombreLocalidad());
            nuevoItem.setCantidad(item.cantidad());
            nuevoItem.setPrecioUnitario(item.precioUnitario());
            nuevoItem.setSillasSeleccionadas(new ArrayList<>(item.sillasSeleccionadas()));
            carrito.getItems().add(nuevoItem);
        }

        carritoRepo.save(carrito);
    }

    /**
     * Edita los detalles de un ítem existente en el carrito.
     *
     * @param idCarrito identificador del carrito.
     * @param item objeto {@link DetalleCarritoDTO} con los nuevos datos del ítem.
     * @throws Exception si el carrito o el ítem no existen.
     */
    @Override
    public void editarItem(String idCarrito, DetalleCarritoDTO item) throws Exception {
        Optional<Carrito> carrito = carritoRepo.findById(idCarrito);

        if (carrito.isPresent()) {
            Carrito carritoActual = carrito.get();

            Optional<DetalleCarrito> itemExistente = carritoActual.getItems().stream()
                    .filter(i -> i.getIdEvento().equals(item.idEvento()))
                    .findFirst();

            if (itemExistente.isPresent()) {
                DetalleCarrito detalleExistente = itemExistente.get();
                detalleExistente.setCantidad(item.cantidad());
                detalleExistente.setNombreLocalidad(item.nombreLocalidad());
                detalleExistente.setPrecioUnitario(item.precioUnitario());
                carritoRepo.save(carritoActual);
            } else {
                throw new Exception("Item no encontrado en el carrito");
            }
        } else {
            throw new Exception("Carrito no encontrado");
        }
    }

    /**
     * Obtiene la información detallada de un carrito por su ID.
     *
     * @param idCarrito identificador del carrito.
     * @return objeto {@link InformacionCarritoDTO} con los datos del carrito.
     * @throws Exception si el carrito no se encuentra.
     */
    @Override
    public InformacionCarritoDTO traerCarrito(String idCarrito) throws Exception {
        Optional<Carrito> carrito = carritoRepo.findById(idCarrito);

        if (carrito.isPresent()) {
            Carrito carritodto = carrito.get();
            return new InformacionCarritoDTO(
                    carritodto.getFecha(),
                    convertirADetalleCarritoDTO(carritodto.getItems()),
                    carritodto.getId(),
                    carritodto.getIdUsuario()
            );
        } else {
            throw new Exception("No se ha encontrado un carrito");
        }
    }

    /**
     * Convierte una lista de entidades {@link DetalleCarrito} en una lista de {@link DetalleCarritoDTO}.
     *
     * @param lista lista de entidades del carrito.
     * @return lista equivalente en formato DTO.
     */
    public List<DetalleCarritoDTO> convertirADetalleCarritoDTO(List<DetalleCarrito> lista) {
        return lista.stream()
                .map(detalle -> new DetalleCarritoDTO(
                        detalle.getIdDetalleCarrito(),
                        detalle.getIdEvento(),
                        detalle.getCantidad(),
                        detalle.getNombreLocalidad(),
                        detalle.getPrecioUnitario(),
                        detalle.getSillasSeleccionadas()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el carrito asociado a un cliente.
     *
     * @param idCuenta identificador del cliente.
     * @return información del carrito del cliente.
     * @throws Exception si el carrito no se encuentra.
     */
    @Override
    public InformacionCarritoDTO traerCarritoCliente(String idCuenta) throws Exception {
        Optional<Carrito> carrito = carritoRepo.buscarCarritoPorIdUsuario(idCuenta);

        if (carrito.isPresent()) {
            Carrito carritodto = carrito.get();
            return new InformacionCarritoDTO(
                    carritodto.getFecha(),
                    convertirADetalleCarritoDTO(carritodto.getItems()),
                    carritodto.getId(),
                    carritodto.getIdUsuario()
            );
        } else {
            throw new Exception("No se ha encontrado un carrito");
        }
    }

    /**
     * Vacía completamente el carrito, eliminando todos sus ítems.
     *
     * @param idCarrito identificador del carrito.
     * @throws Exception si el carrito no se encuentra.
     */
    @Override
    public void vaciarCarrito(String idCarrito) throws Exception {
        Optional<Carrito> carrito = carritoRepo.findById(idCarrito);

        if (carrito.isPresent()) {
            Carrito carritoActual = carrito.get();
            carritoActual.getItems().clear();
            carritoRepo.save(carritoActual);
        } else {
            throw new Exception("Carrito no encontrado");
        }
    }
}
