package proyecto.modelo.dto.orden;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;
import proyecto.modelo.vo.DetalleOrden;

import java.time.LocalDate;
import java.util.List;

public record EditarOrdenDTO (


        @NotBlank String id,
        @NotBlank @Length(max = 50) String idCliente,

        @NotNull LocalDate fecha,
        @NotBlank String  codigoPasarela,

        @Positive float total,

        //@NotNull Pago pago,
       // @NotNull String idCupon,
        @NotNull List<DetalleOrden> items

){
}
