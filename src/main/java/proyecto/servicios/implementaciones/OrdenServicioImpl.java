package proyecto.servicios.implementaciones;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyecto.modelo.documentos.Evento;
import proyecto.modelo.documentos.Orden;
import proyecto.modelo.dto.cuenta.InformacionCuentaDTO;
import proyecto.modelo.dto.email.EmailDTO;
import proyecto.modelo.dto.orden.CrearOrdenDTO;
import proyecto.modelo.dto.orden.EditarOrdenDTO;
import proyecto.modelo.dto.orden.InformacionOrdenDTO;
import proyecto.modelo.vo.DetalleOrden;
import proyecto.modelo.vo.Localidad;
import proyecto.modelo.vo.Pago;
import proyecto.modelo.vo.Silla;
import proyecto.repositorios.EventoRepo;
import proyecto.repositorios.OrdenRepo;
import proyecto.servicios.interfaces.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrdenServicioImpl implements OrdenServicio {

    private final OrdenRepo ordenRepo;
    private final EventoServicio eventoServicio;
    private final CuentaServicio cuentaServicio;
    private final EmailServicio emailServicio;
    private final EventoRepo eventoRepo;
    private final CarritoServicio carritoServicio;

    /**
     * Crea una nueva orden validando fechas, capacidad y límite de compra por usuario.
     */
    @Override
    public String crearOrden(CrearOrdenDTO crearOrdenDTO) throws Exception {
        LocalDate fechaActual = LocalDate.now();

// Validar cada detalle de la orden antes de crearla
        for (DetalleOrden detalle : crearOrdenDTO.items()) {
            Evento evento = eventoServicio.obtenerEvento(detalle.getIdEvento());

            // Validar que la compra se realice máximo 2 días antes del evento
            if (evento.getFechaEvento().minusDays(2).isBefore(fechaActual)) {
                throw new Exception("La compra solo puede realizarse hasta dos días antes del evento: " + evento.getNombre());
            }

            // Validar que haya capacidad disponible
            Localidad localidad = evento.obtenerLocalidad(detalle.getNombreLocalidad());
            if (localidad.getCapacidadDisponible() < detalle.getCantidad()) {
                throw new Exception("No hay capacidad suficiente para la localidad " + detalle.getNombreLocalidad() +
                        " en el evento " + evento.getNombre());
            }

            // Verificar que no supere el límite de boletas por usuario
            int limiteBoletas = 5;
            int yaCompradas = calcularBoletasCompradas(crearOrdenDTO.idCliente(), detalle.getIdEvento());
            int nuevas = detalle.getCantidad();

            if (yaCompradas + nuevas > limiteBoletas) {
                throw new IllegalArgumentException("Has superado el límite de boletas permitidas para el evento " +
                        evento.getNombre() + " (" + limiteBoletas + " máximo por usuario)");
            }

            // 🔹 Validar disponibilidad de las sillas seleccionadas
            if (detalle.getSillasSeleccionadas() != null && !detalle.getSillasSeleccionadas().isEmpty()) {
                for (String codigoSilla : detalle.getSillasSeleccionadas()) {
                    Silla silla = localidad.getSillas().stream()
                            .filter(s -> s.getCodigo().equals(codigoSilla))
                            .findFirst()
                            .orElseThrow(() -> new Exception("La silla con código " + codigoSilla +
                                    " no existe en la localidad " + localidad.getNombre()));

                    if (!silla.isDisponible()) {
                        throw new Exception("La silla " + codigoSilla + " ya está ocupada en " +
                                localidad.getNombre() + " del evento " + evento.getNombre());
                    }

                    // ✅ Marcar silla como ocupada
                    silla.setDisponible(false);
                }
            }

            // 🔸 Reducir capacidad disponible en la localidad
            localidad.setEntradasVendidas(localidad.getCapacidadDisponible() - detalle.getCantidad());

            // 🔸 Guardar cambios en el evento (actualiza sillas ocupadas)
            eventoRepo.save(evento);
        }


        // Crear y guardar la nueva orden
        Orden nuevaOrden = new Orden();
        nuevaOrden.setIdCliente(crearOrdenDTO.idCliente());
        nuevaOrden.setFecha(fechaActual);
        nuevaOrden.setCodigoPasarela(crearOrdenDTO.codigoPasarela());
        nuevaOrden.setItems(crearOrdenDTO.items());
        nuevaOrden.setTotal(crearOrdenDTO.total());

        // Actualizar entradas vendidas en los eventos correspondientes
        for (DetalleOrden detalle : crearOrdenDTO.items()) {
            Evento evento = eventoServicio.obtenerEvento(detalle.getIdEvento());
            Localidad localidad = evento.obtenerLocalidad(detalle.getNombreLocalidad());
            localidad.setEntradasVendidas(localidad.getEntradasVendidas() + detalle.getCantidad());
            eventoRepo.save(evento);
        }

        // Guardar la orden en base de datos
        Orden ordenGuardada = ordenRepo.save(nuevaOrden);

        //Se vacia el carrito despues de q ue la orden proceda.
        carritoServicio.vaciarCarritoByIdCliente(crearOrdenDTO.idCliente());
        // Enviar correo de confirmación al cliente con código QR
        InformacionCuentaDTO cuenta = cuentaServicio.obtenerInformacionCuenta(crearOrdenDTO.idCliente());
        EmailDTO emailDTO = new EmailDTO(
                "Detalles de tu compra en EventosClick",
                "Gracias por tu compra. Adjuntamos el código QR de tu orden y los detalles de la misma.",
                cuenta.correo()
        );
        emailServicio.enviarCorreoConQr(emailDTO, ordenGuardada);

        return "La orden ha sido creada con éxito y se ha enviado un correo con los detalles de la compra. - ID: " + ordenGuardada.getId();
    }

    /**
     * Calcula cuántas boletas ha comprado un usuario para un evento.
     */
    int calcularBoletasCompradas(String idCliente, String idEvento) {
        List<Orden> ordenes = ordenRepo.buscarOrdenesPorClienteYEvento(idCliente, idEvento);
        return ordenes.stream()
                .flatMap(orden -> orden.getItems().stream())
                .filter(item -> item.getIdEvento().equals(idEvento))
                .mapToInt(DetalleOrden::getCantidad)
                .sum();
    }

    /**
     * Actualiza la información de una orden existente.
     */
    @Override
    public String actualizarOrden(EditarOrdenDTO editarOrdenDTO) throws Exception {
        Orden orden = obtenerOrden(editarOrdenDTO.id());
        orden.setItems(editarOrdenDTO.items());
        orden.setTotal(editarOrdenDTO.total());
        orden.setIdCliente(editarOrdenDTO.idCliente());
        orden.setIdCupon(editarOrdenDTO.idCupon());
        ordenRepo.save(orden);
        return "La orden ha sido actualizada con éxito.";
    }

    /**
     * Elimina una orden devolviendo la capacidad a los eventos correspondientes.
     */
    @Override
    public String eliminarOrden(String idOrden) throws Exception {
        Orden orden = obtenerOrden(idOrden);
        if (orden == null) throw new Exception("La orden no existe.");

        InformacionCuentaDTO cuenta = cuentaServicio.obtenerInformacionCuenta(orden.getIdCliente());
        if (cuenta == null) throw new Exception("No se encontró la cuenta del cliente.");

        // Devolver capacidad de entradas a los eventos afectados
        for (DetalleOrden detalle : orden.getItems()) {
            Evento evento = eventoServicio.obtenerEvento(detalle.getIdEvento());
            Localidad localidad = evento.obtenerLocalidad(detalle.getNombreLocalidad());
            localidad.setEntradasVendidas(localidad.getEntradasVendidas() - detalle.getCantidad());
        }

        ordenRepo.delete(orden);
        return "La orden ha sido cancelada, las boletas han sido eliminadas y la capacidad ha sido devuelta.";
    }

    /**
     * Busca todas las órdenes realizadas por un cliente.
     */
    @Override
    public List<Orden> buscarOrdenesPorCliente(String idCliente) throws Exception {
        return ordenRepo.buscarOrdenesPorCliente(idCliente);
    }

    /**
     * Retorna las órdenes dentro de un rango de fechas específico.
     */
    @Override
    public List<Orden> buscarOrdenesPorRangoDeFechas(String fechaInicio, String fechaFin) throws Exception {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
        Date dateOne, dateTwo;
        try {
            dateOne = parser.parse(fechaInicio);
            dateTwo = parser.parse(fechaFin);
        } catch (ParseException e) {
            throw new Exception("Error al parsear las fechas.");
        }

        return dateOne.before(dateTwo)
                ? ordenRepo.buscarOrdenesPorRangoDeFechas(dateOne, dateTwo)
                : new ArrayList<>();
    }

    /**
     * Devuelve información detallada de una orden.
     */
    @Override
    public InformacionOrdenDTO obtenerInformacionOrden(String idOrden) throws Exception {
        Orden orden = obtenerOrden(idOrden);
        return new InformacionOrdenDTO(
                orden.getId(),
                orden.getIdCliente(),
                orden.getFecha(),
                orden.getTotal(),
                orden.getItems()
        );
    }

    /**
     * Lista todas las órdenes registradas en el sistema.
     */
    @Override
    public List<InformacionOrdenDTO> listarTodasLasOrdenes() throws Exception {
        return ordenRepo.findAll().stream()
                .map(orden -> new InformacionOrdenDTO(
                        orden.getId(),
                        orden.getIdCliente(),
                        orden.getFecha(),
                        orden.getTotal(),
                        orden.getItems()))
                .collect(Collectors.toList());
    }

    /**
     * Crea una preferencia de pago en MercadoPago para una orden existente.
     */
    @Override
    public Preference realizarPago(String idOrden) throws Exception {
        Orden ordenGuardada = obtenerOrden(idOrden);
        List<PreferenceItemRequest> itemsPasarela = new ArrayList<>();

        // Construir los ítems que se enviarán a MercadoPago
        for (DetalleOrden item : ordenGuardada.getItems()) {
            Evento evento = eventoServicio.obtenerEvento(item.getIdEvento());
            Localidad localidad = evento.obtenerLocalidad(item.getNombreLocalidad());

            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(evento.getId())
                    .title(evento.getNombre())
                    .pictureUrl(evento.getImagenPortada())
                    .categoryId(evento.getTipo().name())
                    .quantity(item.getCantidad())
                    .currencyId("COP")
                    .unitPrice(BigDecimal.valueOf(localidad.getPrecio()))
                    .build();

            itemsPasarela.add(itemRequest);
        }

        // Configurar credenciales de MercadoPago
        MercadoPagoConfig.setAccessToken("APP_USR-5411335358313717-100712-ffe7d21472d9eb2733d13ab9a0cdc24e-2028101571");

        // Configurar datos del comprador
        PreferencePayerRequest payer = PreferencePayerRequest.builder()
                .name("Comprador")
                .surname("Sandbox")
                .email("test_user_4853470745469862009@testuser.com")
                .build();

        // Configurar URLs de retorno
        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success("https://app-fronted-eventosclick.web.app/pago-exitoso")
                .failure("https://app-fronted-eventosclick.web.app/pago-fallido")
                .pending("https://app-fronted-eventosclick.web.app/pago-pendiente")
                .build();

        // Construir la preferencia con metadatos y URL de notificación
        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .backUrls(backUrls)
                .payer(payer)
                .items(itemsPasarela)
                .metadata(Map.of("id_orden", ordenGuardada.getId()))
                .notificationUrl("https://76773d1be309.ngrok-free.app/api/general/notificacion-pago")
                .autoReturn("approved")
                .build();

        // Crear la preferencia y guardar el código en la orden
        Preference preference = new PreferenceClient().create(preferenceRequest);
        ordenGuardada.setCodigoPasarela(preference.getId());
        ordenRepo.save(ordenGuardada);

        return preference;
    }

    /**
     * Recibe y procesa notificaciones de MercadoPago para registrar pagos.
     */
    @Override
    public void recibirNotificacionMercadoPago(Map<String, Object> request) {
        try {
            Object tipo = request.get("type");

            // Si la notificación corresponde a un pago, procesarlo
            if ("payment".equals(tipo)) {
                String input = request.get("data").toString();
                String idPago = input.replaceAll("\\D+", "");

                Payment payment = new PaymentClient().get(Long.parseLong(idPago));

                String idOrden = payment.getMetadata().get("id_orden").toString();

                Orden orden = obtenerOrden(idOrden);
                Pago pago = crearPago(payment);
                orden.setPago(pago);
                ordenRepo.save(orden);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Convierte la información del pago de MercadoPago a un objeto Pago del dominio.
     */
    private Pago crearPago(Payment payment) {
        Pago pago = new Pago();
        pago.setIdPago(payment.getId().toString());
        pago.setFecha(payment.getDateCreated().toLocalDateTime());
        pago.setEstado(payment.getStatus());
        pago.setDetalleEstado(payment.getStatusDetail());
        pago.setTipoPago(payment.getPaymentTypeId());
        pago.setMoneda(payment.getCurrencyId());
        pago.setCodigoAutorizacion(payment.getAuthorizationCode());
        pago.setValorTransaccion(payment.getTransactionAmount().floatValue());
        return pago;
    }

    /**
     * Busca una orden por su ID, lanzando excepción si no existe.
     */
    private Orden obtenerOrden(String idOrden) throws Exception {
        return ordenRepo.findById(idOrden)
                .orElseThrow(() -> new Exception("No se encontró una orden con el ID " + idOrden));
    }

    /**
     * Obtiene el historial de órdenes de un cliente.
     */
    @Override
    public List<InformacionOrdenDTO> obtenerHistorialOrdenes(String idCliente) throws Exception {
        List<Orden> ordenesCliente = ordenRepo.buscarOrdenesPorCliente(idCliente);

        if (ordenesCliente.isEmpty()) {
            throw new Exception("No se encontraron órdenes para el cliente con ID " + idCliente);
        }

        return ordenesCliente.stream()
                .map(orden -> new InformacionOrdenDTO(
                        orden.getId(),
                        orden.getIdCliente(),
                        orden.getFecha(),
                        orden.getTotal(),
                        orden.getItems()
                ))
                .collect(Collectors.toList());
    }
}
