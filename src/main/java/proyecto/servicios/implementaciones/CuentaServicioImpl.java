package proyecto.servicios.implementaciones;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyecto.config.JWTUtils;
import proyecto.modelo.documentos.Carrito;
import proyecto.modelo.documentos.Cuenta;
import proyecto.modelo.documentos.Usuario;
import proyecto.modelo.dto.autenticacion.TokenDTO;
import proyecto.modelo.dto.cuenta.*;
import proyecto.modelo.dto.email.EmailDTO;
import proyecto.modelo.enums.EstadoCuenta;
import proyecto.modelo.enums.Rol;
import proyecto.modelo.vo.CodigoValidacion;
import proyecto.repositorios.CarritoRepo;
import proyecto.repositorios.CuentaRepo;
import proyecto.servicios.interfaces.CuentaServicio;
import proyecto.servicios.interfaces.EmailServicio;
import proyecto.servicios.interfaces.EventoServicio;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementación del servicio {@link CuentaServicio}.
 *
 * Gestiona el ciclo de vida de las cuentas de usuario:
 * creación, edición, eliminación, activación, autenticación y recuperación de contraseña.
 *
 * Además, asocia automáticamente un carrito a cada nueva cuenta.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CuentaServicioImpl implements CuentaServicio {

    private final CuentaRepo cuentaRepo;
    private final JWTUtils jwtUtils;
    private final EmailServicio emailServicio;
    private final EventoServicio eventoServicio;
    private final CarritoRepo carritoRepo;

    /**
     * Crea una nueva cuenta de usuario, valida que el correo y la cédula sean únicos,
     * genera un código de activación y envía un correo con dicho código.
     * También crea un carrito vacío asociado a la cuenta.
     *
     * @param cuenta datos de la cuenta a crear.
     * @return mensaje de confirmación.
     * @throws Exception si ya existe un usuario con el mismo correo o cédula.
     */
    @Override
    public String crearCuenta(CrearCuentaDTO cuenta) throws Exception {
        if (existeEmail(cuenta.correo())) {
            throw new Exception("Ya existe un usuario registrado con el correo " + cuenta.correo());
        }

        if (existeCedula(cuenta.cedula())) {
            throw new Exception("La cédula " + cuenta.cedula() + " ya se encuentra registrada.");
        }

        String codigoAleatorio = generarCodigo();

        Cuenta nuevaCuenta = new Cuenta();
        nuevaCuenta.setEmail(cuenta.correo());
        nuevaCuenta.setPassword(encriptarPassword(cuenta.password()));
        nuevaCuenta.setRol(Rol.CLIENTE);
        nuevaCuenta.setFechaRegistro(LocalDateTime.now());
        nuevaCuenta.setUsuario(new Usuario(
                cuenta.cedula(),
                cuenta.nombre(),
                cuenta.telefono(),
                cuenta.direccion()
        ));
        nuevaCuenta.setEstado(EstadoCuenta.INACTIVO);
        nuevaCuenta.setCodigoValidacionRegistro(new CodigoValidacion(LocalDateTime.now(), codigoAleatorio));

        Cuenta cuentaGuardada = cuentaRepo.save(nuevaCuenta);

        Carrito carrito = new Carrito();
        carrito.setFecha(LocalDateTime.now());
        carrito.setItems(new ArrayList<>());
        carrito.setIdUsuario(cuentaGuardada.getId());
        carrito.setPrecioTotal(0);
        carritoRepo.save(carrito);

        emailServicio.enviarCorreo(new EmailDTO("CODIGO DE ACTIVACIÓN CUENTA", codigoAleatorio, nuevaCuenta.getEmail()));
        return "Su cuenta se ha generado con éxito.";
    }

    /**
     * Edita los datos personales de una cuenta existente.
     *
     * @param cuenta DTO con los datos modificados.
     * @return ID de la cuenta modificada.
     * @throws Exception si la cuenta no existe.
     */
    @Override
    public String editarCuenta(EditarCuentaDTO cuenta) throws Exception {
        if (!existeCuenta(cuenta.id())) {
            throw new Exception("No se encontró una cuenta con el id " + cuenta.id());
        }

        Cuenta cuentaModificada = obtenerCuenta(cuenta.id());
        cuentaModificada.getUsuario().setNombre(cuenta.nombre());
        cuentaModificada.getUsuario().setDireccion(cuenta.direccion());
        cuentaModificada.getUsuario().setTelefono(cuenta.telefono());

        cuentaRepo.save(cuentaModificada);
        return cuentaModificada.getId();
    }

    /**
     * Marca una cuenta como eliminada (cambio de estado a ELIMINADO).
     *
     * @param id identificador de la cuenta.
     * @return mensaje de confirmación.
     * @throws Exception si la cuenta no existe.
     */
    @Override
    public String eliminarCuenta(String id) throws Exception {
        if (!existeCuenta(id)) {
            throw new Exception("No se encontró una cuenta con el id " + id);
        }

        Cuenta cuenta = obtenerCuenta(id);
        cuenta.setEstado(EstadoCuenta.ELIMINADO);
        cuentaRepo.save(cuenta);

        return "Su cuenta ha sido eliminada.";
    }

    /**
     * Obtiene la información general de una cuenta activa.
     *
     * @param id identificador de la cuenta.
     * @return DTO con la información de la cuenta.
     * @throws Exception si la cuenta no existe o está eliminada.
     */
    @Override
    @Transactional(readOnly = true)
    public InformacionCuentaDTO obtenerInformacionCuenta(String id) throws Exception {
        Cuenta cuenta = obtenerCuenta(id);

        return new InformacionCuentaDTO(
                id,
                cuenta.getUsuario().getCedula(),
                cuenta.getUsuario().getNombre(),
                cuenta.getUsuario().getTelefono(),
                cuenta.getUsuario().getDireccion(),
                cuenta.getEmail()
        );
    }

    /**
     * Envía un código de recuperación de contraseña al correo del usuario.
     *
     * @param correo correo asociado a la cuenta.
     * @return mensaje de confirmación.
     * @throws Exception si el correo no está registrado.
     */
    @Override
    public String enviarCodigoRecuperacionPassword(String correo) throws Exception {
        Cuenta cuenta = obtenerEmail(correo);
        String codigoValidacion = generarCodigo();

        cuenta.setCodigoValidacionPassword(new CodigoValidacion(LocalDateTime.now(), codigoValidacion));
        cuentaRepo.save(cuenta);

        emailServicio.enviarCorreo(new EmailDTO("CODIGO DE RECUPERACION DE CONTRASEÑA", codigoValidacion, correo));
        return "Se ha enviado un correo con el código de recuperación de contraseña";
    }

    /**
     * Cambia la contraseña de una cuenta validando un código de recuperación temporal.
     *
     * @param cambiarPasswordDTO datos del cambio de contraseña.
     * @return mensaje de confirmación.
     * @throws Exception si el código no coincide o ha expirado.
     */
    @Override
    public String cambiarPassword(CambiarPasswordDTO cambiarPasswordDTO) throws Exception {
        Cuenta cuentaOptional = obtenerEmail(cambiarPasswordDTO.correo());
        CodigoValidacion codigoValidacion = cuentaOptional.getCodigoValidacionPassword();

        if (codigoValidacion.getCodigo().equals(cambiarPasswordDTO.codigoVerificacion())) {
            if (codigoValidacion.getFechaCreacion().plusMinutes(15).isAfter(LocalDateTime.now())) {
                cuentaOptional.setPassword(encriptarPassword(cambiarPasswordDTO.passwordNueva()));
                cuentaRepo.save(cuentaOptional);
            } else {
                throw new Exception("El código ya expiró.");
            }
        } else {
            throw new Exception("El código ingresado no coincide con el enviado al correo.");
        }

        return "Su contraseña ha sido cambiada.";
    }

    /**
     * Inicia sesión validando el correo y la contraseña.
     * Si la cuenta está inactiva, la activa automáticamente.
     *
     * @param loginDTO credenciales de inicio de sesión.
     * @return token JWT con la información del usuario.
     * @throws Exception si el correo o la contraseña son incorrectos.
     */
    @Override
    public TokenDTO iniciarSesion(LoginDTO loginDTO) throws Exception {
        Cuenta cuenta = obtenerPorEmail(loginDTO.correo());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (!passwordEncoder.matches(loginDTO.password(), cuenta.getPassword())) {
            throw new Exception("La contraseña es incorrecta");
        }

        if (cuenta.getEstado() != EstadoCuenta.ACTIVO || cuenta.getEstado() != EstadoCuenta.ELIMINADO ) {
            cuenta.setEstado(EstadoCuenta.ACTIVO);
            cuentaRepo.save(cuenta);
        }

        Map<String, Object> map = construirClaims(cuenta);
        return new TokenDTO(jwtUtils.generarToken(cuenta.getEmail(), map));
    }

    /**
     * Activa una cuenta mediante un token de validación recibido por correo.
     *
     * @param activarCuentaDTO DTO con el token de activación.
     * @return mensaje de confirmación.
     * @throws Exception si el token es inválido o ha expirado.
     */
    @Override
    public String activarCuenta(ActivarCuentaDTO activarCuentaDTO) throws Exception {
        Optional<Cuenta> cuentaOpt = cuentaRepo.buscarPorCodigoValidacion(activarCuentaDTO.token());

        if (!cuentaOpt.isPresent()) {
            throw new Exception("El token de activación es inválido.");
        }

        if (cuentaOpt.get().getEstado() == EstadoCuenta.ACTIVO) {
            throw new Exception("La cuenta ya está activa.");
        }

        Cuenta cuenta = cuentaOpt.get();
        LocalDateTime fechaCreacionToken = cuenta.getCodigoValidacionRegistro().getFechaCreacion();
        if (fechaCreacionToken.plusMinutes(15).isBefore(LocalDateTime.now())) {
            throw new Exception("El token de activación ha expirado.");
        }

        cuenta.setEstado(EstadoCuenta.ACTIVO);
        cuentaRepo.save(cuenta);
        return "Cuenta activada exitosamente.";
    }

    /**
     * Lista todas las cuentas registradas del sistema en formato simplificado.
     *
     * @return lista de DTOs de cuentas.
     */
    @Override
    public List<ItemCuentaDTO> listarCuentas() {
        List<Cuenta> cuentas = cuentaRepo.findAll();
        List<ItemCuentaDTO> items = new ArrayList<>();

        for (Cuenta cuenta : cuentas) {
            items.add(new ItemCuentaDTO(
                    cuenta.getId(),
                    cuenta.getUsuario().getNombre(),
                    cuenta.getEmail(),
                    cuenta.getUsuario().getTelefono()
            ));
        }

        return items;
    }

    /**
     * Obtiene una cuenta por su correo electrónico, verificando que no esté eliminada.
     *
     * @param email correo de la cuenta.
     * @return objeto {@link Cuenta}.
     * @throws Exception si no existe una cuenta con ese correo o está eliminada.
     */
    @Override
    public Cuenta obtenerPorEmail(String email) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.findByEmail(email);

        if (cuentaOptional.isEmpty()) {
            throw new Exception("No existe una cuenta registrada con el correo " + email + ".");
        }

        Cuenta cuenta = cuentaOptional.get();

        if (cuenta.getEstado() == EstadoCuenta.ELIMINADO) {
            throw new Exception("La cuenta registrada con el correo " + email + " está ELIMINADA.");
        }

        return cuenta;
    }

    /**
     * Envía un nuevo código de activación a una cuenta inactiva.
     *
     * @param correo correo del usuario.
     * @return mensaje de confirmación.
     * @throws Exception si el correo no está registrado.
     */
    @Override
    public String enviarCodigoActivacionCuenta(String correo) throws Exception {
        Cuenta cuenta = obtenerEmail(correo);
        String codigoValidacion = generarCodigo();

        cuenta.setCodigoValidacionRegistro(new CodigoValidacion(LocalDateTime.now(), codigoValidacion));
        cuentaRepo.save(cuenta);

        emailServicio.enviarCorreo(new EmailDTO("CODIGO DE ACTIVACIÓN CUENTA", codigoValidacion, correo));
        return "Se ha enviado un correo con el código de activación de su cuenta";
    }

    // Métodos privados auxiliares con comentarios breves

    private Cuenta obtenerEmail(String correo) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscaremail(correo);

        if (cuentaOptional.isEmpty()) {
            throw new Exception("El correo dado no está registrado.");
        }

        Cuenta cuenta = cuentaOptional.get();

        if (cuenta.getEstado().equals(EstadoCuenta.ELIMINADO)) {
            throw new Exception("La cuenta registrada con el correo " + correo + " está ELIMINADA.");
        }

        return cuenta;
    }

    private Cuenta obtenerCuenta(String id) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.findById(id);

        if (cuentaOptional.isEmpty()) {
            throw new Exception("No existe una cuenta registrada con el id " + id + ".");
        }

        Cuenta cuenta = cuentaOptional.get();

        if (cuenta.getEstado().equals(EstadoCuenta.ELIMINADO)) {
            throw new Exception("La cuenta registrada con el correo " + id + " está ELIMINADA.");
        }

        return cuenta;
    }

    private boolean existeCuenta(String cuenta) {
        Optional<Cuenta> optionalCuenta = cuentaRepo.findById(cuenta);
        return optionalCuenta.isPresent();
    }

    private boolean existeCedula(String cedula) {
        return cuentaRepo.buscarCedula(cedula).isPresent();
    }

    private boolean existeEmail(String email) {
        return cuentaRepo.buscaremail(email).isPresent();
    }

    private String generarCodigo() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder codigo = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int indice = (int) (caracteres.length() * Math.random());
            codigo.append(caracteres.charAt(indice));
        }
        return codigo.toString();
    }

    private String encriptarPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    private Map<String, Object> construirClaims(Cuenta cuenta) {
        return Map.of(
                "rol", cuenta.getRol(),
                "nombre", cuenta.getUsuario().getNombre(),
                "id", cuenta.getId()
        );
    }

    private Cuenta obtenerCuentaPorIdPropietario(String idPropietario) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.findById(idPropietario);
        if (cuentaOptional.isEmpty()) {
            throw new Exception("No existe una cuenta con el propietario " + idPropietario);
        }
        return cuentaOptional.get();
    }
}
