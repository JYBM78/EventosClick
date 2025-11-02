package proyecto.modelo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Silla {
    private String codigo;          // Ejemplo: "A1", "B5"
    private boolean disponible = true;
}
