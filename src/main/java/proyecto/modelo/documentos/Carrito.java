package proyecto.modelo.documentos;


import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import proyecto.modelo.vo.DetalleCarrito;

import java.time.LocalDateTime;
import java.util.List;

@Document("carritos")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Carrito {

    @Id
    @EqualsAndHashCode.Exclude
    private String id;
    private LocalDateTime fecha;
    private List<DetalleCarrito> items;
    private String idUsuario;//Esto es de cuenta id
    private float precioTotal;

    @Builder
    public Carrito(String idCarrito, String nombre, double v) {

    }
}
