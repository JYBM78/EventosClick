package proyecto.modelo.dto.autenticacion;

public record MensajeDTO<T>(
        boolean error,
        T respuesta
) {
}

