package proyecto.servicios.implementaciones;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyecto.modelo.documentos.Evento;
import proyecto.modelo.dto.evento.*;
import proyecto.modelo.enums.EstadoEvento;
import proyecto.modelo.enums.TipoEvento;
import proyecto.modelo.vo.Localidad;
import proyecto.repositorios.EventoRepo;
import proyecto.servicios.interfaces.EventoServicio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EventoServicioImpl implements EventoServicio {

    private final EventoRepo eventoRepo;

    @Override
    public String crearEvento(CrearEventoDTO crearEventoDTO) throws Exception {

        if(crearEventoDTO.fechaEvento().isBefore(LocalDate.now())){
            throw new Exception("La fecha ingresada para el evento debe ser mayor a la fecha actual");
        }

        if( existeEvento(crearEventoDTO.fechaEvento(), crearEventoDTO.nombre(), crearEventoDTO.ciudad())){
            throw new Exception("Ya existe un evento registrado con el nombre " +
                    crearEventoDTO.nombre() + " para la fecha " + crearEventoDTO.fechaEvento());
        }

        List<Localidad> localidades = crearEventoDTO.localidades()
                .stream()
                .map(localidadDTO -> new Localidad(
                        localidadDTO.precio(),
                        localidadDTO.nombre(),
                        localidadDTO.capacidadMaxima())
                )
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

    @Override
    public String editarEvento(EditarEventoDTO editarEventoDTO) throws Exception {

        Evento eventoModificado = obtenerEvento(editarEventoDTO.id());

        if(editarEventoDTO.fechaEvento().isBefore(LocalDate.now())){
            throw new Exception("La nueva fecha ingresada para el evento debe ser mayor a la fecha actual");
        }

        eventoModificado.setNombre(editarEventoDTO.nombre());
        eventoModificado.setImagenPortada(editarEventoDTO.imagenPortada().url());
       // eventoModificado.setEstado(editarEventoDTO.estado());
        eventoModificado.setDescripcion(editarEventoDTO.descripcion());
        eventoModificado.setImagenLocalidades(editarEventoDTO.imagenLocalidades().url());
        eventoModificado.setFechaEvento(editarEventoDTO.fechaEvento());

        eventoRepo.save(eventoModificado);
        return eventoModificado.getId();
    }

    //Preguntar si se deben hacer más validaciones para eliminar un evento
    //por ejemplo, si ya hay ordenes asociadas al evento que se va a eliminar
    @Override
    public String eliminarEvento(String id) throws Exception {

        Evento evento = obtenerEvento(id);

        evento.setEstado(EstadoEvento.ELIMINADO);

        eventoRepo.save(evento);

        return "El evento ha sido eliminado.";
    }

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

    @Override
    public List<ItemEventoDTO> listarEventos() throws Exception {

        List<Evento> eventos = eventoRepo.findAll();

        return eventos.stream()
                .map(evento -> new ItemEventoDTO(
                        evento.getId(),

                        evento.getImagenPortada(),
                        evento.getNombre(),
                        evento.getFechaEvento(),
                        evento.getCiudad()
                        ))
                .collect(Collectors.toList());
    }

    @Override
    public List<InformacionEventoDTO> listarTodosEvento() throws Exception {
        return null;
    }

    @Override

    public List<ItemEventoDTO> listarEventosActivos() throws Exception {
        List<Evento> eventosActivos = eventoRepo.listarEventosActivos();
        return eventosActivos.stream()
                .map(evento -> new ItemEventoDTO(

                        evento.getId(),

                        evento.getImagenPortada(),
                        evento.getNombre(),
                        evento.getFechaEvento(),
                        evento.getCiudad()
                ))
                .collect(Collectors.toList());
    }

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
                        evento.getCiudad()
                ))
                .collect(Collectors.toList());
    }

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

    @Override
    public Evento obtenerEvento(String id) throws Exception {

        Optional<Evento> eventoOptional = eventoRepo.findById(id);

        if(eventoOptional.isEmpty()){
            throw new Exception("No existe un evento registrado con el id " + id + ".");
        }

        Evento evento = eventoOptional.get();

        // Validar el estado del evento
        /*if (evento.getEstado() != EstadoEvento.ACTIVO) {  // Asume que el estado que buscas es "ACTIVO"
            throw new Exception("El evento con id " + id + " no está activo. Estado actual: " + evento.getEstado());
        }*/

        return evento;

    }

    @Override
    public List<TipoEvento> obtenerTipoEventos() {
        return List.of(TipoEvento.values());
    }

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
                        evento.getLocalidades()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<Evento> traerEventosPorPreferenciaUsuario(List<TipoEvento> tipos) throws Exception {
        return eventoRepo.filtrarEventosPorTipos(tipos);
    }

    //Método para validar si el evento ya existe y no se duplique un mismo evento cuando se esta creando
    private boolean existeEvento(LocalDate fechaEvento, String nombre, String ciudad) {

        return eventoRepo.buscarEvento(nombre, fechaEvento, ciudad).isPresent();

    }


}
