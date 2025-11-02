package proyecto.servicios.interfaces;

import proyecto.modelo.documentos.Orden;
import proyecto.modelo.dto.email.EmailDTO;

/**
 * Servicio encargado de gestionar el envío de correos electrónicos dentro del sistema.
 *
 * Esta interfaz define los métodos necesarios para enviar diferentes tipos de correos,
 * incluyendo correos simples, correos con formato HTML y correos que contienen un código QR
 * generado a partir de una orden.
 */
public interface EmailServicio {

    /**
     * Envía un correo electrónico en formato de texto plano.
     *
     * @param emailDTO objeto que contiene los datos necesarios para el envío del correo:
     *                 destinatario, asunto y contenido.
     * @throws Exception si ocurre un error durante el proceso de envío del correo.
     */
    void enviarCorreo(EmailDTO emailDTO) throws Exception;

    /**
     * Envía un correo electrónico con contenido en formato HTML.
     *
     * @param emailDTO objeto con la información del correo, incluyendo el cuerpo en formato HTML.
     * @throws Exception si se presenta un error al procesar o enviar el correo.
     */
    void enviarCorreoHtml(EmailDTO emailDTO) throws Exception;

    /**
     * Envía un correo electrónico con un código QR adjunto, relacionado con una orden específica.
     *
     * @param emailDTO objeto que contiene los datos del correo (destinatario, asunto y cuerpo).
     * @param orden objeto que representa la orden asociada al correo, utilizada para generar el QR.
     * @throws Exception si ocurre un error al generar el QR o al enviar el correo.
     */
    void enviarCorreoConQr(EmailDTO emailDTO, Orden orden) throws Exception;
}
