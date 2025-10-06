package proyecto.modelo.documentos;


import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import proyecto.modelo.vo.DetalleOrden;
import proyecto.modelo.vo.Pago;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Orden {

    @Id
    @EqualsAndHashCode.Exclude
    private String id;

   // private ObjectId idCliente;
    private String idCliente;
    private LocalDate fecha;
    private String codigoPasarela;
    private List<DetalleOrden> items;
    private Pago pago;
    private float total;
    //private ObjectId idCupon;
    private String idCupon;
}
