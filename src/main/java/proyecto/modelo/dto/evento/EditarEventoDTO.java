package proyecto.modelo.dto.evento;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import proyecto.modelo.dto.imagenDTO;

import java.time.LocalDate;

public record EditarEventoDTO(
        @NotBlank String id,
        @NotBlank String nombre,

        @NotBlank String descripcion,
       // @NotNull EstadoEvento estado,
        String ciudad,
        @NotNull LocalDate fechaEvento,

        @NotNull imagenDTO imagenPortada,
        @NotNull imagenDTO imagenLocalidades


) {
}
