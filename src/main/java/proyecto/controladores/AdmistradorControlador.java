package proyecto.controladores;


import proyecto.modelo.documentos.Evento;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.modelo.documentos.Orden;
import proyecto.modelo.dto.autenticacion.MensajeDTO;
import proyecto.modelo.dto.evento.CrearEventoDTO;
import proyecto.modelo.dto.evento.EditarEventoDTO;
import proyecto.modelo.dto.evento.InformacionEventoDTO;
import proyecto.modelo.dto.orden.InformacionOrdenDTO;
import proyecto.servicios.implementaciones.OrdenServicioImpl;
import proyecto.servicios.interfaces.EventoServicio;
import proyecto.servicios.interfaces.ImagenesServicio;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdmistradorControlador {

    private final EventoServicio eventoServicio;
    private final ImagenesServicio imagenesServicio;
    private final OrdenServicioImpl ordenServicio;

    //Orden
    @GetMapping("/obtener-ordenes-rango-fecha-orden/{dateOne}/{dateTwo}")
    public ResponseEntity<MensajeDTO<List<Orden>>> buscarOrdenesPorRangoDeFechas(@PathVariable("dateOne")String d1, @PathVariable("dateTwo")String d2) throws Exception {
        List<Orden> ordenesClientes = ordenServicio.buscarOrdenesPorRangoDeFechas(d1,d2);
        return ResponseEntity.ok(new MensajeDTO<>(false, ordenesClientes));
    }
    @GetMapping("/obtener-evento/{id}")
    public ResponseEntity<MensajeDTO<Evento>> obtenerEvento(@PathVariable String id) throws Exception {
        Evento evento = eventoServicio.obtenerEvento(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, evento));
    }
    @GetMapping("/obtener-ordenes-orden")
    public ResponseEntity<MensajeDTO<List<InformacionOrdenDTO>>> buscarOrdenes() throws Exception {
        List<InformacionOrdenDTO> ordenesCliente = ordenServicio.listarTodasLasOrdenes();
        return ResponseEntity.ok(new MensajeDTO<>(false, ordenesCliente));
    }

    //Evento

    @GetMapping("/listar-todos-eventos-admin")
    public ResponseEntity<MensajeDTO<List<InformacionEventoDTO>>> listarEventos() throws Exception {
        List<InformacionEventoDTO> lista = eventoServicio.listarEventosAdmin();
        return ResponseEntity.ok(new MensajeDTO<>(false, lista));
    }
    @PostMapping("/crear-evento")
    public ResponseEntity<MensajeDTO<String>> crearEvento(@Valid @RequestBody CrearEventoDTO evento) throws Exception{
        eventoServicio.crearEvento(evento);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Evento creado exitosamente"));
    }

    @PutMapping("/editar-evento")
    public ResponseEntity<MensajeDTO<String>> editarEvento(@Valid @RequestBody EditarEventoDTO evento) throws Exception{
        eventoServicio.editarEvento(evento);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Evento editado exitosamente"));
    }

    @DeleteMapping("/eliminar-evento/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarEvento(@PathVariable String id) throws Exception{
        eventoServicio.eliminarEvento(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Evento eliminado exitosamente"));
    }

    //ORGANIZAR
    @PostMapping("/subir")
    public ResponseEntity<MensajeDTO<Map>> subir(@RequestParam("imagen") MultipartFile imagen) throws Exception{
        Map respuesta = imagenesServicio.subirImagen(imagen);
        return ResponseEntity.ok().body(new MensajeDTO<Map>(false, respuesta));
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<MensajeDTO<String>> eliminar(@RequestParam("idImagen") String idImagen)  throws Exception{
        imagenesServicio.eliminarImagen( idImagen );
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "La imagen fue eliminada correctamente"));
    }



}
