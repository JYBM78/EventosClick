package proyecto.modelo.dto.evento;

import co.edu.uniquindio.proyecto.modelo.dto.imagenDTO;
import co.edu.uniquindio.proyecto.modelo.enums.TipoEvento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;

public record CrearEventoDTO(

         @NotNull imagenDTO imagenImportada,
         @NotBlank @Length (min = 5, max = 100) String nombre,
         @NotBlank @Length (max = 500) String descripcion,
         @NotNull imagenDTO imagenLocalidades,
         @NotNull TipoEvento tipo,
         @NotNull LocalDate fechaEvento,
         @NotBlank @Length (max = 20) String ciudad,
         @NotEmpty List<LocalidadDTO> localidades


) {
}
