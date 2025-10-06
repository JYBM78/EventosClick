package proyecto.servicios.interfaces;



import proyecto.modelo.documentos.Cuenta;
import proyecto.modelo.dto.autenticacion.TokenDTO;
import proyecto.modelo.dto.cuenta.*;
import proyecto.modelo.dto.evento.ItemEventoDTO;
import proyecto.modelo.enums.TipoEvento;

import java.util.List;

public interface CuentaServicio {

    String crearCuenta(CrearCuentaDTO cuenta) throws Exception;

    String editarCuenta(EditarCuentaDTO cuenta) throws Exception;

    String eliminarCuenta(String id) throws Exception;

    InformacionCuentaDTO obtenerInformacionCuenta(String id) throws Exception;

    String enviarCodigoRecuperacionPassword(String correo) throws Exception;

    String cambiarPassword(CambiarPasswordDTO cambiarPasswordDTO ) throws Exception;

    TokenDTO iniciarSesion(LoginDTO loginDTO) throws Exception;

    String activarCuenta(ActivarCuentaDTO activarCuentaDTO) throws Exception;







    List<ItemCuentaDTO> listarCuentas() throws Exception;

    Cuenta obtenerPorEmail(String email) throws Exception;

    String enviarCodigoActivacionCuenta(String correo) throws Exception;


}
