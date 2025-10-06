package proyecto.modelo.documentos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import proyecto.modelo.enums.*;
import proyecto.modelo.vo.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document("cuenta")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Cuenta {


    @Id
    private String id;

    private String email;
    private String password;
    private Rol rol;
    private LocalDateTime fechaRegistro;
    private Usuario usuario;
    EstadoCuenta estado;
    private CodigoValidacion codigoValidacionRegistro;
    private CodigoValidacion codigoValidacionPassword;



    private boolean activacionPrimeraVez;

    @Builder
    public Cuenta(String email, String password, Rol rol, LocalDateTime fechaRegistro, Usuario usuario, EstadoCuenta estado, CodigoValidacion codigoValidacionRegistro, CodigoValidacion codigoValidacionPassword) {
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.fechaRegistro = fechaRegistro;
        this.usuario = usuario;
        this.estado = estado;
        this.codigoValidacionRegistro = codigoValidacionRegistro;
        this.codigoValidacionPassword = codigoValidacionPassword;

        this.activacionPrimeraVez = false;
    }

}
