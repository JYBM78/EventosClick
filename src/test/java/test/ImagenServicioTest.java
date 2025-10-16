package test;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import proyecto.servicios.interfaces.ImagenesServicio;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ImagenServicioTest {

   // @Autowired
    private ImagenesServicio imagenesServicio; ;

    @Test
    public void testSubirImagen() throws Exception {
        // Cargar la imagen desde src/test/resources
        File imageFile = new File("src/test/resources/gatoImagen.jpg"); // Ajusta el nombre y la ruta según sea necesario

        // Crear un MockMultipartFile a partir del archivo
        MultipartFile multipartFile = new MockMultipartFile(
                "testImage.jpg",  // Nombre del archivo en la solicitud
                imageFile.getName(),  // Nombre original del archivo
                "image/jpeg",  // Tipo de contenido
                Files.readAllBytes(imageFile.toPath())  // Contenido del archivo
        );

        // Llamar al método subirImagen
        Map url = imagenesServicio.subirImagen(multipartFile);

        // Verificar que la URL no sea nula
        assertNotNull(url);
        System.out.println(url);

    }


}



