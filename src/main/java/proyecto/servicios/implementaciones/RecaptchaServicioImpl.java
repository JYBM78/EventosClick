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

import java.util.HashMap;
import java.util.Map;

@Service
public class RecaptchaServicioImpl {

    @Value("${google.recaptcha.secret}")
    private String recaptchaSecret;

    //@Value("${google.recaptcha.verify-url}")
    private String verifyUrl = "https://www.google.com/recaptcha/api/siteverify";

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean verifyRecaptcha(String token) {
        System.out.println("Token recibido: " + token);

        // 🔹 Cuerpo con formato correcto
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", recaptchaSecret);
        params.add("response", token);

        // 🔹 Cabeceras para indicar que es un formulario
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // 🔹 Petición POST a la API de Google
        ResponseEntity<Map> response = restTemplate.postForEntity(verifyUrl, request, Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null) return false;

        System.out.println("Respuesta de Google: " + body);

        return (Boolean) body.get("success");
    }
}
