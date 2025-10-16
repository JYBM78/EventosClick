package proyecto.modelo.dto.orden;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;
import proyecto.modelo.vo.DetalleOrden;

import java.util.List;

public record CrearOrdenDTO(

        @NotBlank @Length(max = 50) String idCliente,


        @NotBlank String  codigoPasarela,
        @Positive float total,
        @NotNull List<DetalleOrden> items




) {
}
