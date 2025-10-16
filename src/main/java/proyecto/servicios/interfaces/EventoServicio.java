package proyecto.servicios.interfaces;



import org.springframework.stereotype.Service;
import proyecto.modelo.documentos.Evento;
import proyecto.modelo.dto.evento.*;
import proyecto.modelo.enums.TipoEvento;

import java.util.List;

public interface EventoServicio {

    String crearEvento(CrearEventoDTO crearEventoDTO) throws Exception;

    String editarEvento(EditarEventoDTO editarEventoDTO) throws Exception;;

    String eliminarEvento(String id) throws Exception;

    InformacionEventoDTO obtenerInformacionEvento(String id) throws Exception;

    List<ItemEventoDTO> listarEventos() throws Exception;
    List<InformacionEventoDTO> listarTodosEvento() throws Exception;
    List<ItemEventoDTO> listarEventosActivos()  throws Exception;
    List<ItemEventoDTO> filtrarEventosFuturos() throws Exception;


    List<ItemEventoDTO> filtrarEventos(FiltroEventoDTO filtroEventoDTO) throws Exception;

    Evento obtenerEvento(String id) throws Exception;
    List<TipoEvento> obtenerTipoEventos();

    List<InformacionEventoDTO> listarEventosAdmin() throws  Exception;

    List<Evento> traerEventosPorPreferenciaUsuario(List<TipoEvento> tipos) throws  Exception;
}
