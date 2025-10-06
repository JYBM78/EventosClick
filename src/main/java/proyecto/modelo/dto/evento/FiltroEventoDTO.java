package proyecto.modelo.dto.evento;


import proyecto.modelo.enums.TipoEvento;

public record FiltroEventoDTO(
        String nombre,
        TipoEvento tipo,
        String ciudad
) {
}
