package proyecto.modelo.dto.cuenta;

public record CambiarPasswordDTO(

        String correo,
        String codigoVerificacion,
        String passwordNueva
) {
}
