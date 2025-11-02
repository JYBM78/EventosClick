package proyecto.servicios.interfaces;

import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

/**
 * Servicio encargado de la gestión de imágenes en el sistema.
 *
 * Define las operaciones necesarias para subir y eliminar imágenes,
 * delegando la lógica de almacenamiento a la implementación correspondiente
 * (por ejemplo, servicios en la nube como Cloudinary o AWS S3).
 */
public interface ImagenesServicio {

    /**
     * Sube una imagen al servidor o servicio externo de almacenamiento.
     *
     * @param imagen archivo de tipo {@link MultipartFile} que contiene la imagen a subir.
     * @return un {@link Map} con la información de la imagen subida,
     *         como la URL de acceso y el identificador del recurso.
     * @throws Exception si ocurre un error durante la carga de la imagen.
     */
    Map<String, String> subirImagen(MultipartFile imagen) throws Exception;

    /**
     * Elimina una imagen almacenada a partir de su identificador.
     *
     * @param idImagen identificador único de la imagen en el servicio de almacenamiento.
     * @return un {@link Map} con información sobre el resultado de la eliminación.
     * @throws Exception si la imagen no existe o no puede eliminarse.
     */
    Map eliminarImagen(String idImagen) throws Exception;
}
