package proyecto.servicios.implementaciones;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class RecaptchaServicioImpl {

    // Se inyecta la clave secreta del reCAPTCHA desde las propiedades de configuración
    @Value("${google.recaptcha.secret}")
    private String recaptchaSecret;

    // Se inyecta la URL del servicio de verificación de Google
    @Value("${google.recaptcha.verify-url}")
    private String verifyUrl;

    // Cliente HTTP que permite enviar solicitudes a servicios externos
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Verifica si el token reCAPTCHA proporcionado por el cliente es válido.
     * @param token Token generado por el cliente al resolver el reCAPTCHA.
     * @return true si el token es válido, false en caso contrario.
     */
    public boolean verifyRecaptcha(String token) {
        // Se muestra el token recibido y la clave secreta (solo con fines de depuración)
        System.out.println("Token recibido: " + token);
        System.out.println("Secret: " + recaptchaSecret);

        // Se prueba la conexión con Google para verificar conectividad
        try {
            ResponseEntity<String> response = restTemplate.getForEntity("https://www.google.com", String.class);
            System.out.println("Google responde: " + response.getStatusCode());
        } catch (Exception e) {
            System.out.println("Error conectando con Google: " + e.getMessage());
        }

        // Se construyen los parámetros que requiere la API de verificación de Google
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", recaptchaSecret); // Clave secreta del servidor
        params.add("response", token);         // Token del usuario

        // Se definen las cabeceras indicando que el contenido será un formulario
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Se crea la entidad HTTP que contiene los datos del formulario y las cabeceras
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // Se envía la solicitud POST al endpoint de verificación de Google
        ResponseEntity<Map> response = restTemplate.postForEntity(verifyUrl, request, Map.class);

        // Se obtiene el cuerpo de la respuesta que contiene el resultado de la validación
        Map<String, Object> body = response.getBody();
        if (body == null) return false;

        // Se imprime la respuesta completa (solo para depuración)
        System.out.println("Respuesta de Google: " + body);

        // Se retorna el valor booleano del campo "success" que indica si el token es válido
        return (Boolean) body.get("success");
    }
}
