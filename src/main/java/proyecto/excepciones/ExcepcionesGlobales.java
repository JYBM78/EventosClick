package proyecto.excepciones;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import proyecto.modelo.dto.autenticacion.MensajeDTO;
import proyecto.modelo.dto.autenticacion.ValidacionDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase global para el manejo centralizado de excepciones en la aplicación.
 *
 * <p>Esta clase captura las excepciones que se lanzan durante la ejecución de
 * los controladores REST y devuelve respuestas con un formato estándar, usando
 * los DTOs {@link MensajeDTO} y {@link ValidacionDTO}.</p>
 *
 * <p>Gracias a la anotación {@code @RestControllerAdvice}, esta clase se aplica
 * automáticamente a todos los controladores REST del proyecto.</p>
 */
@RestControllerAdvice
public class ExcepcionesGlobales {

    /**
     * Maneja cualquier excepción no controlada (Exception genérica).
     *
     * @param e la excepción capturada
     * @return una respuesta HTTP 500 (Internal Server Error) con el mensaje de error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MensajeDTO<String>> generalException(Exception e) {
        // Imprime la traza de la excepción en la consola para depuración
        e.printStackTrace();

        // Retorna una respuesta con el mensaje de error dentro del objeto MensajeDTO
        return ResponseEntity.internalServerError()
                .body(new MensajeDTO<>(true, e.getMessage()));
    }

    /**
     * Maneja las excepciones de validación de argumentos en los controladores.
     *
     * <p>Ocurre cuando se envían datos inválidos a un endpoint que usa anotaciones
     * como {@code @Valid} o {@code @Validated}.</p>
     *
     * @param ex excepción lanzada por Spring al fallar la validación de argumentos
     * @return una respuesta HTTP 400 (Bad Request) con la lista de errores de validación
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MensajeDTO<List<ValidacionDTO>>> validationException(MethodArgumentNotValidException ex) {
        // Lista donde se almacenarán los errores de validación
        List<ValidacionDTO> errores = new ArrayList<>();

        // Obtenemos los resultados de la validación fallida
        BindingResult results = ex.getBindingResult();

        // Recorremos todos los errores de campos y los convertimos en objetos ValidacionDTO
        for (FieldError e : results.getFieldErrors()) {
            errores.add(new ValidacionDTO(e.getField(), e.getDefaultMessage()));
        }

        // Retorna la lista de errores en un objeto MensajeDTO
        return ResponseEntity.badRequest()
                .body(new MensajeDTO<>(true, errores));
    }
}
