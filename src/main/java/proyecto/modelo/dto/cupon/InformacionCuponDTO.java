package proyecto.modelo.dto.cupon;

import co.edu.uniquindio.proyecto.modelo.enums.EstadoCupon;
import co.edu.uniquindio.proyecto.modelo.enums.TipoCupon;

import java.time.LocalDate;

public record InformacionCuponDTO(

        String id,
        String nombre,
        float descuento,
        LocalDate fechaVencimiento,
        String codigo,
        EstadoCupon estado,
        TipoCupon tipo

) {
}
