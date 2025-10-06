package proyecto.modelo.dto.orden;



import proyecto.modelo.vo.DetalleOrden;

import java.time.LocalDate;
import java.util.List;

public record InformacionOrdenDTO(

        String id,
        String idCliente,

        LocalDate fechaCreacion,
        float total,

        List<DetalleOrden> items
) {
}
