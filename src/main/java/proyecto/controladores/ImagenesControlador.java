package proyecto.controladores;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.modelo.dto.autenticacion.MensajeDTO;
import proyecto.servicios.interfaces.ImagenesServicio;

import java.util.Map;

/**
 * Controlador responsable de manejar las operaciones relacionadas con imágenes,
 * incluyendo la carga y eliminación desde el sistema o un servicio externo de almacenamiento.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/imagenes")
public class ImagenesControlador {

    private final ImagenesServicio imagenesServicio;

    /**
     * Sube una imagen al servidor o servicio de almacenamiento (por ejemplo, Cloudinary).
     *
     * @param imagen archivo de imagen recibido del cliente
     * @return un objeto con los datos de respuesta (por ejemplo, URL pública, ID de imagen, etc.)
     * @throws Exception si ocurre un error durante la carga
     */
    @PostMapping("/subir")
    public ResponseEntity<MensajeDTO<Map>> subir(@RequestParam("imagen") MultipartFile imagen) throws Exception {
        Map respuesta = imagenesServicio.subirImagen(imagen);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, respuesta));
    }

    /**
     * Elimina una imagen del sistema o servicio de almacenamiento según su identificador.
     *
     * @param idImagen identificador único de la imagen a eliminar
     * @return mensaje de confirmación al eliminar correctamente la imagen
     * @throws Exception si ocurre un error al intentar eliminar la imagen
     */
    @DeleteMapping("/eliminar")
    public ResponseEntity<MensajeDTO<String>> eliminar(@RequestParam("idImagen") String idImagen) throws Exception {
        imagenesServicio.eliminarImagen(idImagen);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "La imagen fue eliminada correctamente"));
    }
}
