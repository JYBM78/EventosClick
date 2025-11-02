package proyecto.modelo.dto.carrito;

import java.util.List;

public record DetalleCarritoDTO(
        String idDetalleCarrito,
        String idEvento,
        int cantidad,
        String nombreLocalidad,
        float precioUnitario,
        List<String> sillasSeleccionadas // 🔹 NUEVO campo
) {
}
