package proyecto.modelo.dto.carrito;


import proyecto.modelo.vo.DetalleCarrito;

import java.time.LocalDateTime;
import java.util.List;

public record InformacionCarritoDTO(
        LocalDateTime fecha,
        List<DetalleCarritoDTO> items,
        String id,
        String idUsuario
) {
}
