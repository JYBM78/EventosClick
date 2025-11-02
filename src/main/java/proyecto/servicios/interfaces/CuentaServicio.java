package proyecto.servicios.interfaces;

import proyecto.modelo.documentos.Cuenta;
import proyecto.modelo.dto.autenticacion.TokenDTO;
import proyecto.modelo.dto.cuenta.*;
import proyecto.modelo.dto.evento.ItemEventoDTO;
import proyecto.modelo.enums.TipoEvento;
import java.util.List;

/**
 * Servicio que define las operaciones relacionadas con la gestión de cuentas de usuario.
 *
 * Esta interfaz establece los métodos para el registro, autenticación, actualización,
 * eliminación y recuperación de contraseñas, así como la obtención de información detallada
 * sobre las cuentas registradas en el sistema.
 */
public interface CuentaServicio {

    /**
     * Crea una nueva cuenta de usuario a partir de los datos proporcionados.
     *
     * @param cuenta objeto con la información necesaria para crear la cuenta
     * @return mensaje confirmando la creación de la cuenta
     * @throws Exception si ocurre un error durante el registro
     */
    String crearCuenta(CrearCuentaDTO cuenta) throws Exception;

    /**
     * Edita la información de una cuenta existente.
     *
     * @param cuenta objeto con los datos actualizados de la cuenta
     * @return mensaje confirmando la edición exitosa
     * @throws Exception si ocurre un error al actualizar los datos
     */
    String editarCuenta(EditarCuentaDTO cuenta) throws Exception;

    /**
     * Elimina una cuenta del sistema a partir de su identificador.
     *
     * @param id identificador único de la cuenta a eliminar
     * @return mensaje indicando que la cuenta fue eliminada exitosamente
     * @throws Exception si la cuenta no existe o no puede ser eliminada
     */
    String eliminarCuenta(String id) throws Exception;

    /**
     * Obtiene la información detallada de una cuenta.
     *
     * @param id identificador de la cuenta
     * @return objeto con la información de la cuenta
     * @throws Exception si la cuenta no se encuentra
     */
    InformacionCuentaDTO obtenerInformacionCuenta(String id) throws Exception;

    /**
     * Envía un código de recuperación de contraseña al correo del usuario.
     *
     * @param correo dirección de correo asociada a la cuenta
     * @return mensaje confirmando el envío del código
     * @throws Exception si el correo no está registrado o el envío falla
     */
    String enviarCodigoRecuperacionPassword(String correo) throws Exception;

    /**
     * Cambia la contraseña de una cuenta utilizando los datos de validación y el nuevo valor.
     *
     * @param cambiarPasswordDTO objeto con los datos necesarios para el cambio
     * @return mensaje confirmando el cambio de contraseña
     * @throws Exception si el código de validación no es correcto o hay un error
     */
    String cambiarPassword(CambiarPasswordDTO cambiarPasswordDTO) throws Exception;

    /**
     * Inicia sesión de usuario validando las credenciales proporcionadas.
     *
     * @param loginDTO objeto con el correo y contraseña del usuario
     * @return token de autenticación si las credenciales son válidas
     * @throws Exception si las credenciales son incorrectas o la cuenta está inactiva
     */
    TokenDTO iniciarSesion(LoginDTO loginDTO) throws Exception;

    /**
     * Activa una cuenta de usuario utilizando un código de verificación.
     *
     * @param activarCuentaDTO objeto con la información de activación
     * @return mensaje confirmando la activación exitosa
     * @throws Exception si el código es inválido o la cuenta ya está activa
     */
    String activarCuenta(ActivarCuentaDTO activarCuentaDTO) throws Exception;

    /**
     * Lista todas las cuentas registradas en el sistema.
     *
     * @return lista de cuentas con su información resumida
     * @throws Exception si ocurre un error al recuperar la información
     */
    List<ItemCuentaDTO> listarCuentas() throws Exception;

    /**
     * Busca una cuenta utilizando el correo electrónico como criterio.
     *
     * @param email correo electrónico del usuario
     * @return objeto Cuenta asociado al correo
     * @throws Exception si no se encuentra una cuenta con el correo proporcionado
     */
    Cuenta obtenerPorEmail(String email) throws Exception;

    /**
     * Envía un código de activación de cuenta al correo del usuario.
     *
     * @param correo dirección de correo a la cual se enviará el código
     * @return mensaje confirmando el envío del código
     * @throws Exception si ocurre un error durante el envío del correo
     */
    String enviarCodigoActivacionCuenta(String correo) throws Exception;
}
