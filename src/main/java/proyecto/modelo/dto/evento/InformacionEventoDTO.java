package proyecto.modelo.dto.evento;



import proyecto.modelo.enums.EstadoEvento;
import proyecto.modelo.enums.TipoEvento;
import proyecto.modelo.vo.Localidad;

import java.time.LocalDate;
import java.util.List;

public record InformacionEventoDTO(

        String id,
        EstadoEvento estado,
        String nombre,
        String descripcion,
        TipoEvento tipo,
        LocalDate fechaEvento,
        String ciudad,
        String imagenPortada,
        String imagenLocalidades,
        List<Localidad> localidades
) {

}
