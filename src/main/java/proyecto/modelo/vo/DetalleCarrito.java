package proyecto.modelo.vo;

import lombok.*;

import java.util.List;

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
    private List<String> sillasSeleccionadas; // 🔹 NUEVO campo


}

