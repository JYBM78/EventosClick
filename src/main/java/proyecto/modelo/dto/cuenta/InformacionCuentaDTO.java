package proyecto.modelo.dto.cuenta;



import java.util.List;

public record InformacionCuentaDTO(
        String id,
        String cedula,
        String nombre,
        String telefono,
        String direccion,
        String correo

) {
}
