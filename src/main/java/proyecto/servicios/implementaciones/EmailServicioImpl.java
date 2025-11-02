package proyecto.servicios.implementaciones;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import proyecto.modelo.documentos.Orden;
import proyecto.modelo.dto.email.EmailDTO;
import proyecto.servicios.interfaces.EmailServicio;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementación del servicio {@link EmailServicio}.
 *
 * Esta clase se encarga de gestionar el envío de correos electrónicos mediante
 * la API de SendGrid. Soporta:
 * <ul>
 *     <li>Envío de correos de texto plano</li>
 *     <li>Envío de correos en formato HTML</li>
 *     <li>Envío de correos con código QR adjunto</li>
 * </ul>
 *
 * Se utiliza la anotación {@code @Async} para ejecutar los envíos de forma asíncrona,
 * evitando bloquear el hilo principal.
 */
@Service
public class EmailServicioImpl implements EmailServicio {

    /** Dirección de correo usada como remitente principal. */
    private static final String REMITENTE = "eventosclickuni@gmail.com";

    /** API key de SendGrid, inyectada desde el archivo de configuración. */
    @Value("${sendgrid_api_key}")
    private String SENDGRID_API_KEY;

    /** Host y puerto SMTP de SendGrid. */
    private static final String SENDGRID_HOST = "smtp.sendgrid.net";
    private static final int SENDGRID_PORT = 587;

    /**
     * Construye y configura un {@link Mailer} con las credenciales de SendGrid.
     * Este método puede utilizarse para enviar correos usando SimpleJavaMail si se desea.
     *
     * @return objeto {@link Mailer} configurado.
     */
    private Mailer buildMailer() {
        return MailerBuilder
                .withSMTPServer(SENDGRID_HOST, SENDGRID_PORT, "apikey", SENDGRID_API_KEY)
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer();
    }

    // ============================================================
    // ================ MÉTODOS DE ENVÍO DE CORREOS ===============
    // ============================================================

    /**
     * Envía un correo de texto plano utilizando la API de SendGrid.
     *
     * @param emailDTO objeto con los datos del correo (asunto, cuerpo, destinatario).
     * @throws IOException si ocurre un error al enviar el correo.
     */
    @Override
    @Async
    public void enviarCorreo(EmailDTO emailDTO) throws IOException {
        System.out.println(" SendGrid Key cargada: " + (SENDGRID_API_KEY != null));
        System.out.println(SENDGRID_API_KEY);

        Email from = new Email(REMITENTE);
        Email to = new Email(emailDTO.destinatario());
        Content content = new Content("text/plain", emailDTO.cuerpo());
        Mail mail = new Mail(from, emailDTO.asunto(), to, content);

        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            System.out.println(" Correo enviado: " + response.getStatusCode());
            System.out.println("Body: " + response.getBody());
            System.out.println("Headers: " + response.getHeaders());
        } catch (IOException ex) {
            System.err.println(" Error al enviar correo: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Envía un correo en formato HTML.
     *
     * @param emailDTO datos del correo (asunto, cuerpo HTML, destinatario).
     * @throws Exception si ocurre un error al comunicarse con la API de SendGrid.
     */
    @Override
    @Async
    public void enviarCorreoHtml(EmailDTO emailDTO) throws Exception {
        System.out.println(" Enviando correo HTML a: " + emailDTO.destinatario());

        Email from = new Email(REMITENTE);
        Email to = new Email(emailDTO.destinatario());
        Content content = new Content("text/html", emailDTO.cuerpo());
        Mail mail = new Mail(from, emailDTO.asunto(), to, content);

        enviarMailConSendGrid(mail);
    }

    /**
     * Envía un correo con un código QR adjunto. El QR contiene la información de una orden,
     * como ID, cliente, fecha, total y sillas asignadas.
     *
     * @param emailDTO datos del correo (asunto, cuerpo, destinatario).
     * @param orden objeto {@link Orden} del cual se generará el QR.
     * @throws Exception si ocurre un error durante la generación del QR o el envío del correo.
     */
    @Override
    @Async
    public void enviarCorreoConQr(EmailDTO emailDTO, Orden orden) throws Exception {
        System.out.println(" Enviando correo con QR a: " + emailDTO.destinatario());

        // 1️⃣ Generar contenido QR
        String contenidoQr = generarContenidoQr(orden);

        // 2️⃣ Crear imagen QR
        ByteArrayOutputStream qrStream = new ByteArrayOutputStream();
        generarImagenQr(contenidoQr, qrStream);

        // 3️⃣ Convertir QR a Base64 para adjuntarlo
        String qrBase64 = Base64.getEncoder().encodeToString(qrStream.toByteArray());

        // 4️⃣ Crear correo con HTML y adjunto QR
        Email from = new Email(REMITENTE);
        Email to = new Email(emailDTO.destinatario());
        Content content = new Content("text/html", emailDTO.cuerpo());
        Mail mail = new Mail(from, emailDTO.asunto(), to, content);

        Attachments attachment = new Attachments();
        attachment.setContent(qrBase64);
        attachment.setType("image/png");
        attachment.setFilename("codigo_qr.png");
        attachment.setDisposition("attachment");
        mail.addAttachments(attachment);

        enviarMailConSendGrid(mail);
    }

    /**
     * Envía un objeto {@link Mail} ya configurado mediante la API de SendGrid.
     * Método de apoyo utilizado por los demás tipos de envío.
     *
     * @param mail correo a enviar.
     * @throws IOException si ocurre un error en la comunicación con SendGrid.
     */
    private void enviarMailConSendGrid(Mail mail) throws IOException {
        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            System.out.println(" Correo enviado: " + response.getStatusCode());
            System.out.println(" Respuesta: " + response.getBody());
        } catch (IOException ex) {
            System.err.println(" Error al enviar correo: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Genera el contenido textual que se incluirá dentro del código QR
     * con base en la información de una orden.
     *
     * @param orden objeto {@link Orden} con los datos de compra.
     * @return texto plano que representará los datos dentro del QR.
     */
    private String generarContenidoQr(Orden orden) {
        StringBuilder sb = new StringBuilder();

        sb.append("Orden ID: ").append(orden.getId()).append("\n")
                .append("Cliente ID: ").append(orden.getIdCliente()).append("\n")
                .append("Fecha: ").append(orden.getFecha()).append("\n")
                .append("Total: $").append(orden.getTotal()).append("\n")
                .append("Sillas asignadas:\n");

        orden.getItems().forEach(item -> {
            if (item.getSillasSeleccionadas() != null && !item.getSillasSeleccionadas().isEmpty()) {
                sb.append("Evento: ").append(item.getIdEvento())
                        .append(" (").append(item.getNombreLocalidad()).append(") → ")
                        .append(String.join(", ", item.getSillasSeleccionadas()))
                        .append("\n");
            }
        });

        return sb.toString();
    }

    /**
     * Genera una imagen QR en formato PNG a partir de un texto.
     *
     * @param contenido texto a codificar en el QR.
     * @param outputStream flujo de salida donde se escribirá la imagen.
     * @throws WriterException si ocurre un error durante la generación del código QR.
     * @throws IOException si ocurre un error al escribir la imagen.
     */
    private void generarImagenQr(String contenido, ByteArrayOutputStream outputStream) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = qrCodeWriter.encode(contenido, BarcodeFormat.QR_CODE, 300, 300, hints);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ImageIO.write(qrImage, "png", outputStream);
    }
}
