package proyecto.servicios.implementaciones;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyecto.modelo.documentos.Evento;
import proyecto.modelo.dto.evento.*;
import proyecto.modelo.enums.EstadoEvento;
import proyecto.modelo.enums.TipoEvento;
import proyecto.modelo.vo.Localidad;
import proyecto.modelo.vo.Silla;
import proyecto.repositorios.EventoRepo;
import proyecto.servicios.interfaces.EventoServicio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio {@link EventoServicio}.
 *
 * <p>Esta clase gestiona toda la lógica de negocio relacionada con los eventos del sistema:
 * creación, edición, eliminación lógica, filtrado y consultas específicas.</p>
 *
 * <p>Utiliza {@link EventoRepo} para la persistencia de datos, asegurando transacciones
 * consistentes mediante la anotación {@code @Transactional}.</p>
 *
 * <p>También realiza validaciones de integridad como:
 * <ul>
 *     <li>Evitar crear eventos con fechas pasadas</li>
 *     <li>Prevenir duplicados (mismo nombre, ciudad y fecha)</li>
 *     <li>Controlar estados de eventos (ACTIVO, ELIMINADO, etc.)</li>
 * </ul>
 * </p>
 */
@Service
@Transactional
@RequiredArgsConstructor
public class EventoServicioImpl implements EventoServicio {

    /** Repositorio para la gestión de persistencia de eventos. */
    private final EventoRepo eventoRepo;

    /**
     * Crea un nuevo evento en el sistema.
     *
     * <p>Valida que la fecha no sea anterior al día actual y que no exista otro evento con
     * el mismo nombre, ciudad y fecha. Además, genera automáticamente las localidades
     * y las sillas correspondientes.</p>
     *
     * @param crearEventoDTO datos del evento a crear.
     * @return mensaje de confirmación.
     * @throws Exception si la fecha es inválida o el evento ya existe.
     */
    @Override
    public String crearEvento(CrearEventoDTO crearEventoDTO) throws Exception {

        if (crearEventoDTO.fechaEvento().isBefore(LocalDate.now())) {
            throw new Exception("La fecha ingresada para el evento debe ser mayor a la fecha actual");
        }

        if (existeEvento(crearEventoDTO.fechaEvento(), crearEventoDTO.nombre(), crearEventoDTO.ciudad())) {
            throw new Exception("Ya existe un evento registrado con el nombre " +
                    crearEventoDTO.nombre() + " para la fecha " + crearEventoDTO.fechaEvento());
        }

        // Convertir las localidades del DTO a entidades, generando sillas automáticamente
        List<Localidad> localidades = crearEventoDTO.localidades()
                .stream()
                .map(localidadDTO -> {
                    Localidad localidad = new Localidad();
                    localidad.setNombre(localidadDTO.nombre());
                    localidad.setPrecio(localidadDTO.precio());
                    localidad.setCapacidadMaxima(localidadDTO.capacidadMaxima());
                    localidad.setEntradasVendidas(0);

                    // Generar las sillas para esta localidad
                    List<Silla> sillas = new ArrayList<>();
                    for (int i = 1; i <= localidadDTO.capacidadMaxima(); i++) {
                        sillas.add(new Silla("S" + i, true));
                    }
                    localidad.setSillas(sillas);
                    return localidad;
                })
                .collect(Collectors.toList());

        Evento nuevoEvento = new Evento();

        nuevoEvento.setImagenPortada(crearEventoDTO.imagenImportada().url());
        nuevoEvento.setImagenLocalidades(crearEventoDTO.imagenLocalidades().url());
        nuevoEvento.setFechaEvento(crearEventoDTO.fechaEvento());
        nuevoEvento.setNombre(crearEventoDTO.nombre());
        nuevoEvento.setDescripcion(crearEventoDTO.descripcion());
        nuevoEvento.setEstado(EstadoEvento.ACTIVO);
        nuevoEvento.setTipo(crearEventoDTO.tipo());
        nuevoEvento.setCiudad(crearEventoDTO.ciudad());
        nuevoEvento.setLocalidades(localidades);

        eventoRepo.save(nuevoEvento);

        return "El evento ha sido creado con éxito.";
    }

    /**
     * Edita un evento existente, actualizando sus datos principales.
     *
     * @param editarEventoDTO DTO con los nuevos datos del evento.
     * @return el ID del evento modificado.
     * @throws Exception si la nueva fecha es inválida o el evento no existe.
     */
    @Override
    public String editarEvento(EditarEventoDTO editarEventoDTO) throws Exception {

        Evento eventoModificado = obtenerEvento(editarEventoDTO.id());

        if (editarEventoDTO.fechaEvento().isBefore(LocalDate.now())) {
            throw new Exception("La nueva fecha ingresada para el evento debe ser mayor a la fecha actual");
        }
        // 4️⃣ Reiniciar las sillas de cada localidad
        List<Localidad> localidades = eventoModificado.getLocalidades();

        if (localidades != null && !localidades.isEmpty()) {
            for (Localidad localidad : localidades) {
                // Reiniciar la lista de sillas
                List<Silla> nuevasSillas = new ArrayList<>();

                for (int i = 1; i <= localidad.getCapacidadMaxima(); i++) {
                    // Se crean nuevas sillas disponibles
                    nuevasSillas.add(new Silla("S" + i, true));
                }

                localidad.setSillas(nuevasSillas);
                // Si manejas persistencia en cascada, esto basta;
                // si no, deberías guardar las localidades explícitamente.
            }
        }

        eventoModificado.setNombre(editarEventoDTO.nombre());
        eventoModificado.setImagenPortada(editarEventoDTO.imagenPortada());
        eventoModificado.setDescripcion(editarEventoDTO.descripcion());
        eventoModificado.setImagenLocalidades(editarEventoDTO.imagenLocalidades());
        eventoModificado.setFechaEvento(editarEventoDTO.fechaEvento());

        eventoRepo.save(eventoModificado);
        return eventoModificado.getId();
    }

    /**
     * Elimina lógicamente un evento, cambiando su estado a {@link EstadoEvento#ELIMINADO}.
     *
     * @param id identificador del evento a eliminar.
     * @return mensaje de confirmación.
     * @throws Exception si el evento no existe.
     */
    @Override
    public String eliminarEvento(String id) throws Exception {
        Evento evento = obtenerEvento(id);
        evento.setEstado(EstadoEvento.ELIMINADO);
        eventoRepo.save(evento);
        return "El evento ha sido eliminado.";
    }

    /**
     * Obtiene la información detallada de un evento.
     *
     * @param id identificador del evento.
     * @return DTO con la información completa del evento.
     * @throws Exception si el evento no existe.
     */
    @Override
    public InformacionEventoDTO obtenerInformacionEvento(String id) throws Exception {
        Evento evento = obtenerEvento(id);

        return new InformacionEventoDTO(
                id,
                evento.getEstado(),
                evento.getNombre(),
                evento.getDescripcion(),
                evento.getTipo(),
                evento.getFechaEvento(),
                evento.getCiudad(),
                evento.getImagenPortada(),
                evento.getImagenLocalidades(),
                evento.getLocalidades()
        );
    }

    /**
     * Lista todos los eventos registrados en formato resumido.
     *
     * @return lista de eventos como {@link ItemEventoDTO}.
     * @throws Exception si ocurre un error en la consulta.
     */
    @Override
    public List<ItemEventoDTO> listarEventos() throws Exception {
        List<Evento> eventos = eventoRepo.findAll();

        return eventos.stream()
                .map(evento -> new ItemEventoDTO(
                        evento.getId(),
                        evento.getImagenPortada(),
                        evento.getNombre(),
                        evento.getFechaEvento(),
                        evento.getCiudad()))
                .collect(Collectors.toList());
    }

    /**
     * Método pendiente de implementación: listar todos los eventos con detalle.
     */
    @Override
    public List<InformacionEventoDTO> listarTodosEvento() throws Exception {
        return null;
    }

    /**
     * Lista únicamente los eventos activos.
     *
     * @return lista de eventos activos en formato {@link ItemEventoDTO}.
     * @throws Exception si ocurre un error durante la consulta.
     */
    @Override
    public List<ItemEventoDTO> listarEventosActivos() throws Exception {
        List<Evento> eventosActivos = eventoRepo.listarEventosActivos();
        return eventosActivos.stream()
                .map(evento -> new ItemEventoDTO(
                        evento.getId(),
                        evento.getImagenPortada(),
                        evento.getNombre(),
                        evento.getFechaEvento(),
                        evento.getCiudad()))
                .collect(Collectors.toList());
    }

    /**
     * Filtra los eventos próximos a ocurrir (hasta 2 meses en el futuro).
     *
     * @return lista de eventos futuros.
     * @throws Exception si ocurre un error en la consulta.
     */
    @Override
    public List<ItemEventoDTO> filtrarEventosFuturos() throws Exception {
        LocalDateTime fechaInicio = LocalDateTime.now();
        LocalDateTime fechaFin = fechaInicio.plusMonths(2);
        List<Evento> eventosFuturos = eventoRepo.filtrarEventosFuturos(fechaInicio, fechaFin);

        return eventosFuturos.stream()
                .map(evento -> new ItemEventoDTO(
                        evento.getId(),
                        evento.getImagenPortada(),
                        evento.getNombre(),
                        evento.getFechaEvento(),
                        evento.getCiudad()))
                .collect(Collectors.toList());
    }

    /**
     * Filtra eventos según los criterios proporcionados (nombre, tipo, ciudad).
     *
     * @param filtroEventoDTO criterios de filtrado.
     * @return lista de eventos coincidentes.
     * @throws Exception si ocurre un error en la consulta.
     */
    @Override
    public List<ItemEventoDTO> filtrarEventos(FiltroEventoDTO filtroEventoDTO) throws Exception {
        List<Evento> eventos = eventoRepo.filtrarEventos(filtroEventoDTO.nombre(), filtroEventoDTO.tipo(), filtroEventoDTO.ciudad());

        return eventos.stream()
                .map(evento -> new ItemEventoDTO(
                        evento.getId(),
                        evento.getImagenPortada(),
                        evento.getNombre(),
                        evento.getFechaEvento(),
                        evento.getCiudad()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un evento por su ID, validando su existencia.
     *
     * @param id identificador del evento.
     * @return entidad {@link Evento}.
     * @throws Exception si no existe un evento con el ID especificado.
     */
    @Override
    public Evento obtenerEvento(String id) throws Exception {
        Optional<Evento> eventoOptional = eventoRepo.findById(id);

        if (eventoOptional.isEmpty()) {
            throw new Exception("No existe un evento registrado con el id " + id + ".");
        }

        return eventoOptional.get();
    }

    /**
     * Devuelve todos los tipos de eventos disponibles en el sistema.
     *
     * @return lista de {@link TipoEvento}.
     */
    @Override
    public List<TipoEvento> obtenerTipoEventos() {
        return List.of(TipoEvento.values());
    }

    /**
     * Lista todos los eventos con información completa, pensada para vistas administrativas.
     *
     * @return lista detallada de eventos.
     * @throws Exception si ocurre un error durante la consulta.
     */
    @Override
    public List<InformacionEventoDTO> listarEventosAdmin() throws Exception {
        List<Evento> eventos = eventoRepo.findAll();

        return eventos.stream()
                .map(evento -> new InformacionEventoDTO(
                        evento.getId(),
                        evento.getEstado(),
                        evento.getNombre(),
                        evento.getDescripcion(),
                        evento.getTipo(),
                        evento.getFechaEvento(),
                        evento.getCiudad(),
                        evento.getImagenPortada(),
                        evento.getImagenLocalidades(),
                        evento.getLocalidades()))
                .collect(Collectors.toList());
    }

    /**
     * Retorna los eventos que coinciden con una lista de preferencias de tipo de evento.
     *
     * @param tipos lista de tipos de evento preferidos por el usuario.
     * @return lista de eventos que coinciden con los tipos.
     * @throws Exception si ocurre un error en la consulta.
     */
    @Override
    public List<Evento> traerEventosPorPreferenciaUsuario(List<TipoEvento> tipos) throws Exception {
        return eventoRepo.filtrarEventosPorTipos(tipos);
    }

    /**
     * Verifica si ya existe un evento con el mismo nombre, ciudad y fecha.
     *
     * @param fechaEvento fecha del evento.
     * @param nombre nombre del evento.
     * @param ciudad ciudad donde se realizará el evento.
     * @return {@code true} si el evento ya existe, {@code false} en caso contrario.
     */
    private boolean existeEvento(LocalDate fechaEvento, String nombre, String ciudad) {
        return eventoRepo.buscarEvento(nombre, fechaEvento, ciudad).isPresent();
    }
}
