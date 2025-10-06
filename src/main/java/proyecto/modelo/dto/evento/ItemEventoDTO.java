package proyecto.modelo.dto.evento;

import java.time.LocalDate;

public record ItemEventoDTO(
        String id,
        String urlImagenPoster,
        String nombre,
        LocalDate fecha,
        String ciudad
) {
}
