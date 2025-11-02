package proyecto.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuración para habilitar la ejecución asíncrona en la aplicación.
 *
 * Esta clase activa el soporte de métodos asíncronos en Spring mediante la
 * anotación {@link EnableAsync}. Permite que los métodos anotados con
 * {@code @Async} se ejecuten en hilos separados, mejorando el rendimiento y
 * la capacidad de respuesta de la aplicación.
 *
 * La clase implementa la interfaz {@link AsyncConfigurer}, lo que permite
 * personalizar el comportamiento del procesamiento asíncrono (por ejemplo,
 * definir un {@code Executor} propio o un manejador de excepciones), aunque en
 * este caso se usa la configuración por defecto de Spring.
 */
@Configuration // Indica que esta clase contiene configuraciones de Spring
@EnableAsync   // Habilita el procesamiento asíncrono (@Async)
public class AsyncConfig implements AsyncConfigurer {

    // Actualmente no se sobrescriben métodos de AsyncConfigurer,
    // por lo que se usa la configuración predeterminada.
}
