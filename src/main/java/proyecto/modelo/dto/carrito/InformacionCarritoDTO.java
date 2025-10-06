package proyecto.modelo.dto.carrito;

import co.edu.uniquindio.proyecto.modelo.vo.DetalleCarrito;

import java.time.LocalDateTime;
import java.util.List;

public record InformacionCarritoDTO(
        LocalDateTime fecha,
        List<DetalleCarrito> items,
        String id,
        String idUsuario
) {
}
