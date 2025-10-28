package proyecto.servicios.implementaciones;



import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
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
import java.util.HashMap;
import java.util.Map;


@Service
public class EmailServicioImpl implements EmailServicio {
    // 📧 Credenciales SendGrid
    private static final String REMITENTE = "eventosclickuni@gmail.com";
    @Value("${sendgrid.api.key}")
    private   String SENDGRID_API_KEY;
    private static final String SENDGRID_HOST = "smtp.sendgrid.net";
    private static final int SENDGRID_PORT = 587;

    // 🔹 Construir y configurar el mailer de SendGrid
    private Mailer buildMailer() {
        return MailerBuilder
                .withSMTPServer(SENDGRID_HOST, SENDGRID_PORT, "eventosClick", SENDGRID_API_KEY)
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer();
    }

    // ============================================================
    // ================ MÉTODOS DE ENVÍO DE CORREOS ===============
    // ============================================================

    @Override
    @Async
    public void enviarCorreo(EmailDTO emailDTO) throws Exception {
        System.out.println(SENDGRID_API_KEY);
        Email email = EmailBuilder.startingBlank()
                .from("EventosClick", REMITENTE)
                .to(emailDTO.destinatario())
                .withSubject(emailDTO.asunto())
                .withPlainText(emailDTO.cuerpo())
                .buildEmail();

        try (Mailer mailer = buildMailer()) {
            mailer.sendMail(email);
        }
    }

    @Override
    @Async
    public void enviarCorreoHtml(EmailDTO emailDTO) throws Exception {
        Email email = EmailBuilder.startingBlank()
                .from("EventosClick", REMITENTE)
                .to(emailDTO.destinatario())
                .withSubject(emailDTO.asunto())
                .appendTextHTML(emailDTO.cuerpo())
                .buildEmail();

        try (Mailer mailer = buildMailer()) {
            mailer.sendMail(email);
        }
    }

    @Override
    @Async
    public void enviarCorreoConQr(EmailDTO emailDTO, Orden orden) throws Exception {
        // 🔹 Generar contenido y QR
        String contenidoQr = generarContenidoQr(orden);
        ByteArrayOutputStream qrStream = new ByteArrayOutputStream();

        generarImagenQr(contenidoQr,qrStream);

        // 🔹 Crear correo con el QR adjunto
        Email email = EmailBuilder.startingBlank()
                .from("EventosClick", REMITENTE)
                .to(emailDTO.destinatario())
                .withSubject(emailDTO.asunto())
                .withPlainText(emailDTO.cuerpo())
                .withAttachment("codigo_qr.png", qrStream.toByteArray(), "image/png")
                .buildEmail();

        // 🔹 Enviar correo
        try (Mailer mailer = buildMailer()) {
            mailer.sendMail(email);
        }
    }

    // Método para generar el contenido del QR a partir de la orden
    private String generarContenidoQr(Orden orden) {
        return "Orden ID: " + orden.getId() + "\n" +
                "Cliente ID: " + orden.getIdCliente() + "\n" +
                "Fecha: " + orden.getFecha() + "\n" +
                "Total: $" + orden.getTotal();
    }

    // Método para generar la imagen del QR
    private void generarImagenQr(String contenido, ByteArrayOutputStream outputStream) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = qrCodeWriter.encode(contenido, BarcodeFormat.QR_CODE, 300, 300, hints);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ImageIO.write(qrImage, "png", outputStream);
    }


}
