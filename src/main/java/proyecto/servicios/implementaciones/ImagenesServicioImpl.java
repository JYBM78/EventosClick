package proyecto.servicios.implementaciones;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import proyecto.servicios.interfaces.ImagenesServicio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Implementación del servicio {@link ImagenesServicio} para la gestión de imágenes en Cloudinary.
 *
 * <p>Esta clase permite subir y eliminar imágenes en la nube utilizando la API de Cloudinary.
 * Se configura automáticamente con las credenciales definidas en el archivo de propiedades de la aplicación.</p>
 *
 * <p>Principales características:
 * <ul>
 *     <li>Subida de imágenes a una carpeta específica en Cloudinary.</li>
 *     <li>Eliminación de imágenes por su identificador público.</li>
 *     <li>Conversión segura de archivos {@link MultipartFile} a {@link File} temporal.</li>
 * </ul>
 * </p>
 */
@Service
public class ImagenesServicioImpl implements ImagenesServicio {

    /** Nombre del espacio en Cloudinary. */
    @Value("${cloudinary.cloud_name}")
    private String cloudName;

    /** Clave pública de acceso a Cloudinary. */
    @Value("${cloudinary.api_key}")
    private String apiKey;

    /** Clave secreta de autenticación en Cloudinary. */
    @Value("${cloudinary.api_secret}")
    private String apiSecret;

    /** Cliente Cloudinary configurado con las credenciales del entorno. */
    private Cloudinary cloudinary;

    /**
     * Inicializa la configuración de Cloudinary después de la inyección de dependencias.
     *
     * <p>Este método se ejecuta automáticamente al arrancar el servicio gracias a la anotación {@link PostConstruct}.
     * Configura las credenciales y crea una instancia del cliente Cloudinary.</p>
     */
    @PostConstruct
    public void initCloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        cloudinary = new Cloudinary(config);
    }

    /**
     * Sube una imagen al servicio de almacenamiento en la nube (Cloudinary).
     *
     * <p>Valida que el archivo no esté vacío, lo convierte a un archivo temporal y lo sube
     * a la carpeta <b>eventosClick</b> en Cloudinary.</p>
     *
     * @param imagen archivo a subir (tipo {@link MultipartFile}).
     * @return mapa con la información de la imagen subida:
     *         <ul>
     *             <li><b>url</b>: enlace seguro a la imagen.</li>
     *             <li><b>public_id</b>: identificador único en Cloudinary.</li>
     *         </ul>
     * @throws Exception si ocurre un error durante la carga o la conversión del archivo.
     */
    @Override
    public Map<String, String> subirImagen(MultipartFile imagen) throws Exception {
        if (imagen.isEmpty()) {
            throw new IllegalArgumentException("La imagen está vacía");
        }

        File file = convertir(imagen);
        Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.asMap("folder", "eventosClick"));

        Map<String, String> resultado = new HashMap<>();
        resultado.put("url", (String) uploadResult.get("secure_url"));
        resultado.put("public_id", (String) uploadResult.get("public_id"));
        return resultado;
    }

    /**
     * Elimina una imagen de Cloudinary a partir de su identificador público.
     *
     * @param idImagen identificador de la imagen en Cloudinary.
     * @return resultado de la operación devuelto por la API de Cloudinary.
     * @throws Exception si la eliminación falla o la imagen no existe.
     */
    @Override
    public Map eliminarImagen(String idImagen) throws Exception {
        Map resultado = cloudinary.uploader().destroy(idImagen, ObjectUtils.emptyMap());
        if (!"ok".equals(resultado.get("result"))) {
            throw new RuntimeException("No se pudo eliminar la imagen o no existía: " + idImagen);
        }
        return resultado;
    }

    /**
     * Convierte un archivo {@link MultipartFile} a un archivo temporal {@link File}.
     *
     * <p>El archivo temporal se guarda en el sistema con un nombre aleatorio único (UUID) y conserva
     * la extensión del archivo original cuando es posible.</p>
     *
     * @param imagen archivo recibido desde el cliente.
     * @return archivo temporal listo para ser procesado o subido.
     * @throws IOException si ocurre un error durante la escritura del archivo.
     */
    private File convertir(MultipartFile imagen) throws IOException {
        String originalName = imagen.getOriginalFilename();
        String extension = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf('.'))
                : ".tmp";

        File file = File.createTempFile(UUID.randomUUID().toString(), extension);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(imagen.getBytes());
        }
        return file;
    }
}
