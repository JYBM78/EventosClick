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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CarritoServicioImpl implements CarritoServicio {




    @Autowired
    private final CarritoRepo carritoRepo;

    private final CuentaRepo cuentaRepo;


    @Override
    public String eliminarItem(String idCarrito, String idDetalleCarrito) throws Exception {

        Optional<Carrito> carrito = carritoRepo.findById(idCarrito);

        if(carrito.isPresent()){
            Optional<DetalleCarrito> detalleCarrito = carrito.get().getItems().stream().filter(x -> x.getIdDetalleCarrito().equals(idDetalleCarrito) ).findFirst();
            if(detalleCarrito.isPresent()){
                carrito.get().getItems().remove(detalleCarrito.get());
                carritoRepo.save(carrito.get());
                return "Item eliminado correctamente";
            }
        }
        return "Item no ha sido eliminado correctamente";
    }
    @Override
    public void agregarItem(String idCarrito, DetalleCarritoDTO item) throws Exception {
        //System.out.println(idCarrito);
       Optional<Carrito> carrito = carritoRepo.findById(idCarrito);



        if(carrito.isPresent()){
            Carrito carritoActual = carrito.get();

            // Verificar si el item ya está en el carrito
            Optional<DetalleCarrito> itemExistente = carritoActual.getItems().stream()
                    .filter(i -> i.getIdEvento().equals(item.idEvento()))
                    .findFirst();

            if (itemExistente.isPresent()) {
                // Si el item ya existe, incrementar la cantidad
                DetalleCarrito detalleExistente = itemExistente.get();
                detalleExistente.setCantidad(detalleExistente.getCantidad() + item.cantidad());
            } else {
                // Si el item no existe, agregarlo al carrito
                DetalleCarrito detalleCarrito = new DetalleCarrito();
                detalleCarrito.setIdDetalleCarrito(item.idDetalleCarrito());
                detalleCarrito.setCantidad(item.cantidad());
                detalleCarrito.setIdEvento(item.idEvento());
                detalleCarrito.setNombreLocalidad(item.nombreLocalidad());
                detalleCarrito.setPrecioUnitario(item.precioUnitario());


                carritoActual.getItems().add(detalleCarrito);
            }
            // Guardar el carrito actualizado en la base de datos
            carritoRepo.save(carrito.get());
        }
    }
    @Override
    public void agregarItemUnico(String idCuenta, DetalleCarritoDTO item) throws Exception {
        Optional<Carrito> carrito = carritoRepo.buscarCarritoPorIdUsuario(idCuenta);
        if(carrito.isPresent()){
            Carrito carritoActual = carrito.get();

            // Verificar si el item ya está en el carrito
            Optional<DetalleCarrito> itemExistente = carritoActual.getItems().stream()
                    .filter(i -> i.getIdEvento().equals(item.idEvento()))
                    .findFirst();
            if (itemExistente.isPresent()) {
                // Si el item ya existe, incrementar la cantidad
                DetalleCarrito detalleExistente = itemExistente.get();
                detalleExistente.setCantidad(detalleExistente.getCantidad() + item.cantidad());
            } else {
                // Si el item no existe, agregarlo al carrito
                DetalleCarrito detalleCarrito = new DetalleCarrito();
                detalleCarrito.setIdDetalleCarrito(item.idDetalleCarrito());
                detalleCarrito.setCantidad(item.cantidad());
                detalleCarrito.setIdEvento(item.idEvento());
                detalleCarrito.setNombreLocalidad(item.nombreLocalidad());
                detalleCarrito.setPrecioUnitario(item.precioUnitario());


                carritoActual.getItems().add(detalleCarrito);
            }
            // Guardar el carrito actualizado en la base de datos
            carritoRepo.save(carrito.get());
        }
    }

    @Override
    public void editarItem(String idCarrito, DetalleCarritoDTO item) throws Exception {
        Optional<Carrito> carrito = carritoRepo.findById(idCarrito);

        if (carrito.isPresent()) {
            Carrito carritoActual = carrito.get();

            // Buscar el item que se desea editar en el carrito
            Optional<DetalleCarrito> itemExistente = carritoActual.getItems().stream()
                    .filter(i -> i.getIdEvento().equals(item.idEvento()))
                    .findFirst();

            if (itemExistente.isPresent()) {
                // Si el item existe, actualizar los detalles
                DetalleCarrito detalleExistente = itemExistente.get();
                detalleExistente.setCantidad(item.cantidad());
                detalleExistente.setNombreLocalidad(item.nombreLocalidad());
                detalleExistente.setPrecioUnitario(item.precioUnitario());
                // Actualizar otros campos de detalleExistente según sea necesario

                // Guardar el carrito actualizado en la base de datos
                carritoRepo.save(carritoActual);
            } else {
                throw new Exception("Item no encontrado en el carrito");
            }
        } else {
            throw new Exception("Carrito no encontrado");
        }

    }

    @Override
    public InformacionCarritoDTO traerCarrito(String idCarrito) throws Exception {

        Optional<Carrito> carrito = carritoRepo.findById(idCarrito);

        if(carrito.isPresent()){
            Carrito carritodto = carrito.get();
            return new InformacionCarritoDTO(
                    carritodto.getFecha(),
                    convertirADetalleCarritoDTO(carritodto.getItems()),
                    carritodto.getId(),
                    carritodto.getIdUsuario()

            );

        }else {
            throw  new Exception("No se ha encontrado un carrito");
        }

    }
    public List<DetalleCarritoDTO> convertirADetalleCarritoDTO(List<DetalleCarrito> lista) {
        return lista.stream()
                .map(detalle -> new DetalleCarritoDTO(
                        detalle.getIdDetalleCarrito(),
                        detalle.getIdEvento(),
                        detalle.getCantidad(),
                        detalle.getNombreLocalidad(),
                        detalle.getPrecioUnitario()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public InformacionCarritoDTO traerCarritoCliente(String idCuenta) throws  Exception{
        Optional<Carrito> carrito = carritoRepo.buscarCarritoPorIdUsuario(idCuenta);

        if(carrito.isPresent()){
            Carrito carritodto = carrito.get();
            return new InformacionCarritoDTO(
                    carritodto.getFecha(),
                    convertirADetalleCarritoDTO(carritodto.getItems()),
                    carritodto.getId(),
                    carritodto.getIdUsuario()

            );
        }else {
            throw  new Exception("No se ha encontrado un carrito");
        }

    }

    @Override
    public void vaciarCarrito(String idCarrito) throws Exception {
        Optional<Carrito> carrito = carritoRepo.findById(idCarrito);

        if (carrito.isPresent()) {
            Carrito carritoActual = carrito.get();

            // Vaciar la lista de ítems del carrito
            carritoActual.getItems().clear();

            // Guardar el carrito actualizado en la base de datos
            carritoRepo.save(carritoActual);
        } else {
            throw new Exception("Carrito no encontrado");
        }
    }



}
