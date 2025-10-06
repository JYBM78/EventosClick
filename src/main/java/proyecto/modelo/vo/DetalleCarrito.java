package proyecto.modelo.vo;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class DetalleCarrito {

    private String idDetalleCarrito;
    private String idEvento;
    private int cantidad;
    private String nombreLocalidad;
    private float precioUnitario;


}

