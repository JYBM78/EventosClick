package proyecto.servicios.interfaces;


import org.springframework.stereotype.Service;
import proyecto.modelo.dto.carrito.DetalleCarritoDTO;
import proyecto.modelo.dto.carrito.InformacionCarritoDTO;

@Service
public interface CarritoServicio {



    String eliminarItem(String idCarrito, String idEvento) throws Exception;

    void agregarItem(String idCarrito, DetalleCarritoDTO item) throws Exception;
    void agregarItemUnico(String idCuenta, DetalleCarritoDTO item) throws Exception;
    void editarItem(String idCarrito , DetalleCarritoDTO item) throws  Exception;

   InformacionCarritoDTO traerCarrito(String idCarrito) throws Exception;

    InformacionCarritoDTO traerCarritoCliente(String idCuenta) throws  Exception;
   void vaciarCarrito(String idCarrito) throws Exception;
}