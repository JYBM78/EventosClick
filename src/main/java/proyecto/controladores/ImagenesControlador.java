package proyecto.controladores;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.modelo.dto.autenticacion.MensajeDTO;
import proyecto.servicios.interfaces.ImagenesServicio;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/imagenes")
public class ImagenesControlador {

    private final ImagenesServicio imagenesServicio;

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

