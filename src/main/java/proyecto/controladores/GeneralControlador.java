package proyecto.controladores;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.modelo.documentos.Evento;
import proyecto.modelo.dto.autenticacion.MensajeDTO;
import proyecto.modelo.dto.cuenta.*;
import proyecto.modelo.dto.evento.FiltroEventoDTO;
import proyecto.modelo.dto.evento.InformacionEventoDTO;
import proyecto.modelo.dto.evento.ItemEventoDTO;
import proyecto.modelo.enums.Ciudad;
import proyecto.modelo.enums.TipoEvento;
import proyecto.servicios.implementaciones.OrdenServicioImpl;
import proyecto.servicios.implementaciones.RecaptchaServicioImpl;
import proyecto.servicios.interfaces.CuentaServicio;
import proyecto.servicios.interfaces.EventoServicio;

import java.util.List;
import java.util.Map;

/**
 * Controlador general que maneja operaciones públicas del sistema,
 * incluyendo gestión de eventos, cuentas, validación de seguridad (reCAPTCHA)
 * y recepción de notificaciones de pago.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/general")
public class GeneralControlador {

    private final EventoServicio eventoServicio;
    private final CuentaServicio cuentaServicio;
    private final OrdenServicioImpl ordenServicio;

    @Autowired
    private RecaptchaServicioImpl recaptchaService;

    // ---------------------------------------------
    // SECCIÓN: SEGURIDAD Y NOTIFICACIONES
    // ---------------------------------------------

    /**
     * Valida un token de reCAPTCHA enviado desde el cliente para prevenir bots.
     *
     * @param request mapa con el token enviado por el cliente
     * @return true si el token es válido, false si no lo es
     */
    @PostMapping("/validate")
    public ResponseEntity<MensajeDTO<Boolean>> validateToken(@RequestBody Map<String, String> request) throws Exception {
        String token = request.get("token");
        boolean isValid = recaptchaService.verifyRecaptcha(token);
        return ResponseEntity.ok(new MensajeDTO<>(false, isValid));
    }

    /**
     * Recibe notificaciones de pago desde la pasarela de MercadoPago.
     * Permite procesar los cambios en el estado de una orden de forma asíncrona.
     *
     * @param requestBody datos de la notificación enviada por MercadoPago
     */
    @PostMapping("/notificacion-pago")
    public void recibirNotificacionMercadoPago(@RequestBody Map<String, Object> requestBody) {
        ordenServicio.recibirNotificacionMercadoPago(requestBody);
    }

    // ---------------------------------------------
    // SECCIÓN: EVENTOS
    // ---------------------------------------------

    /**
     * Obtiene información detallada de un evento específico por su ID.
     *
     * @param id identificador del evento
     * @return información completa del evento
     */
    @GetMapping("/obtener-info-evento/{id}")
    public ResponseEntity<MensajeDTO<InformacionEventoDTO>> obtenerInformacionEvento(@PathVariable String id) throws Exception {
        InformacionEventoDTO info = eventoServicio.obtenerInformacionEvento(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, info));
    }

    /**
     * Lista todos los eventos públicos disponibles para los usuarios.
     *
     * @return lista de eventos visibles en formato resumido
     */
    @GetMapping("/listar-todos-eventos")
    public ResponseEntity<MensajeDTO<List<ItemEventoDTO>>> listarEventos() throws Exception {
        List<ItemEventoDTO> lista = eventoServicio.listarEventos();
        return ResponseEntity.ok(new MensajeDTO<>(false, lista));
    }

    /**
     * Lista todos los eventos, incluyendo aquellos con acceso administrativo.
     *
     * @return lista de eventos con información completa
     */
    @GetMapping("/listar-all-evento")
    public ResponseEntity<MensajeDTO<List<InformacionEventoDTO>>> listarTodosEventos() throws Exception {
        List<InformacionEventoDTO> lista = eventoServicio.listarEventosAdmin();
        return ResponseEntity.ok(new MensajeDTO<>(false, lista));
    }

    /**
     * Lista los tipos de evento disponibles (por ejemplo: concierto, conferencia, etc.).
     *
     * @return lista de tipos de evento definidos en el sistema
     */
    @GetMapping("/listar-tipo-eventos")
    public ResponseEntity<MensajeDTO<List<TipoEvento>>> listarTipoEventos() throws Exception {
        List<TipoEvento> lista = eventoServicio.obtenerTipoEventos();
        return ResponseEntity.ok(new MensajeDTO<>(false, lista));
    }

    /**
     * Lista las ciudades donde se realizan eventos.
     *
     * @return lista de ciudades disponibles
     */
    @GetMapping("/listar-ciudad-eventos")
    public ResponseEntity<MensajeDTO<List<Ciudad>>> listarCiudadesEventos() throws Exception {
        List<Ciudad> lista = List.of(Ciudad.values());
        return ResponseEntity.ok(new MensajeDTO<>(false, lista));
    }

    /**
     * Lista únicamente los eventos activos.
     *
     * @return lista de eventos activos
     */
    @GetMapping("/listar-eventos-activos")
    public ResponseEntity<MensajeDTO<List<ItemEventoDTO>>> listarEventosActivos() throws Exception {
        List<ItemEventoDTO> lista = eventoServicio.listarEventosActivos();
        return ResponseEntity.ok(new MensajeDTO<>(false, lista));
    }

    /**
     * Lista los eventos que se realizarán en el futuro.
     *
     * @return lista de eventos futuros
     */
    @GetMapping("/listar-eventos-futuros")
    public ResponseEntity<MensajeDTO<List<ItemEventoDTO>>> listarEventosFuturos() throws Exception {
        List<ItemEventoDTO> lista = eventoServicio.filtrarEventosFuturos();
        return ResponseEntity.ok(new MensajeDTO<>(false, lista));
    }

    /**
     * Filtra los eventos según criterios específicos (tipo, ciudad, fecha, etc.).
     *
     * @param filtroEventoDTO objeto con los criterios de filtrado
     * @return lista de eventos que cumplen con los filtros
     */
    @PostMapping("/filtrar-eventos")
    public ResponseEntity<MensajeDTO<List<ItemEventoDTO>>> filtrarEventos(@RequestBody FiltroEventoDTO filtroEventoDTO) throws Exception {
        List<ItemEventoDTO> lista = eventoServicio.filtrarEventos(filtroEventoDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, lista));
    }

    /**
     * Obtiene un evento por su ID.
     *
     * @param id identificador del evento
     * @return evento completo
     */
    @GetMapping("/obtener-evento/{id}")
    public ResponseEntity<MensajeDTO<Evento>> obtenerEvento(@PathVariable String id) throws Exception {
        Evento evento = eventoServicio.obtenerEvento(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, evento));
    }

    // ---------------------------------------------
    // SECCIÓN: CUENTAS DE USUARIO
    // ---------------------------------------------

    /**
     * Permite a un usuario editar su información de perfil.
     *
     * @param cuenta objeto con los nuevos datos del usuario
     * @return mensaje de confirmación
     */
    @PutMapping("/editar-perfil")
    public ResponseEntity<MensajeDTO<String>> editarCuenta(@Valid @RequestBody EditarCuentaDTO cuenta) throws Exception {
        cuentaServicio.editarCuenta(cuenta);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta editada exitosamente"));
    }

    /**
     * Elimina una cuenta de usuario del sistema.
     *
     * @param id identificador de la cuenta a eliminar
     * @return mensaje de confirmación
     */
    @PutMapping("/eliminar-cuenta/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarCuenta(@PathVariable String id) throws Exception {
        cuentaServicio.eliminarCuenta(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta eliminada exitosamente"));
    }

    /**
     * Obtiene información detallada de una cuenta específica.
     *
     * @param id identificador de la cuenta
     * @return datos completos de la cuenta
     */
    @GetMapping("/obtener-info-cuenta/{id}")
    public ResponseEntity<MensajeDTO<InformacionCuentaDTO>> obtenerInformacionCuenta(@PathVariable String id) throws Exception {
        InformacionCuentaDTO info = cuentaServicio.obtenerInformacionCuenta(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, info));
    }

    /**
     * Lista todas las cuentas de usuario registradas.
     *
     * @return lista de cuentas en formato resumido
     */
    @GetMapping("/listar-todo-cuentas")
    public ResponseEntity<MensajeDTO<List<ItemCuentaDTO>>> listarCuentas() throws Exception {
        List<ItemCuentaDTO> lista = cuentaServicio.listarCuentas();
        return ResponseEntity.ok(new MensajeDTO<>(false, lista));
    }

    /**
     * Envía un código de recuperación de contraseña al correo del usuario.
     *
     * @param correo dirección de correo electrónico del usuario
     * @return mensaje de confirmación
     */
    @GetMapping("/enviar-codigo-password/{correo}")
    public ResponseEntity<MensajeDTO<String>> enviarCodigoRecuperacionPassword(@PathVariable String correo) throws Exception {
        cuentaServicio.enviarCodigoRecuperacionPassword(correo);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Se ha enviado un correo con el código de recuperación de contraseña"));
    }

    /**
     * Envía un código de activación de cuenta al correo del usuario.
     *
     * @param correo dirección de correo electrónico del usuario
     * @return mensaje de confirmación
     */
    @GetMapping("/enviar-codigo-auth/{correo}")
    public ResponseEntity<MensajeDTO<String>> enviarCodigoActivacionCuenta(@PathVariable String correo) throws Exception {
        cuentaServicio.enviarCodigoActivacionCuenta(correo);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Se ha enviado un correo con el código de activación de su cuenta"));
    }

    /**
     * Permite al usuario cambiar su contraseña proporcionando el código y la nueva clave.
     *
     * @param cambiarPasswordDTO datos de cambio de contraseña
     * @return mensaje de éxito
     */
    @PutMapping("/cambiar-password")
    public ResponseEntity<MensajeDTO<String>> cambiarPassword(@RequestBody CambiarPasswordDTO cambiarPasswordDTO) throws Exception {
        cuentaServicio.cambiarPassword(cambiarPasswordDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Su contraseña ha sido cambiada."));
    }

    /**
     * Activa la cuenta de usuario tras verificar el código enviado al correo.
     *
     * @param activarCuentaDTO datos necesarios para la activación
     * @return mensaje de éxito
     */
    @PutMapping("/activar-cuenta")
    public ResponseEntity<MensajeDTO<String>> activarCuenta(@RequestBody ActivarCuentaDTO activarCuentaDTO) throws Exception {
        cuentaServicio.activarCuenta(activarCuentaDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta activada exitosamente."));
    }
}
