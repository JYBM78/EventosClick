package proyecto.controladores;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proyecto.modelo.dto.autenticacion.MensajeDTO;
import proyecto.modelo.dto.autenticacion.TokenDTO;
import proyecto.modelo.dto.cuenta.CrearCuentaDTO;
import proyecto.modelo.dto.cuenta.LoginDTO;
import proyecto.servicios.interfaces.CuentaServicio;

/**
 * Controlador encargado de manejar las operaciones relacionadas con la autenticación de usuarios.
 * Permite iniciar sesión y crear nuevas cuentas.
 * Todas las rutas de este controlador son públicas.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AutenticacionControlador {

    private final CuentaServicio cuentaServicio;

    /**
     * Inicia sesión en el sistema con las credenciales del usuario.
     * Si las credenciales son correctas, devuelve un token JWT para autenticación.
     *
     * @param loginDTO objeto con los datos de inicio de sesión (correo y contraseña)
     * @return objeto con el token de autenticación y un mensaje de confirmación
     */
    @PostMapping("/iniciar-sesion")
    public ResponseEntity<MensajeDTO<TokenDTO>> iniciarSesion(@Valid @RequestBody LoginDTO loginDTO) throws Exception {
        TokenDTO token = cuentaServicio.iniciarSesion(loginDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, token));
    }

    /**
     * Crea una nueva cuenta de usuario con los datos proporcionados.
     *
     * @param cuenta objeto con la información necesaria para registrar la cuenta
     * @return mensaje confirmando la creación exitosa de la cuenta
     */
    @PostMapping("/crear-cuenta")
    public ResponseEntity<MensajeDTO<String>> crearCuenta(@Valid @RequestBody CrearCuentaDTO cuenta) throws Exception {
        cuentaServicio.crearCuenta(cuenta);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta creada exitosamente"));
    }
}
