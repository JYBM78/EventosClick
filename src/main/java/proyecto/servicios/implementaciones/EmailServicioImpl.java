package proyecto.servicios.implementaciones;



import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.Value;
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
    @Value("${email.user}")
    private String correo;

    @Value("${email.password}")
    private String contra;

    @Override
    @Async
    public void enviarCorreo(EmailDTO emailDTO) throws Exception {


        Email email = EmailBuilder.startingBlank()
                .from(correo)
                .to(emailDTO.destinatario())
                .withSubject(emailDTO.asunto())
                .withPlainText(emailDTO.cuerpo())

                .buildEmail();


        //eventosclickuni@gmail.com
        //Hola123?
        //clave de aplicación: qyor yzrc eigg zdcg
        try (Mailer mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, correo, contra)
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer()) {

            mailer.sendMail(email);
        }


    }

    @Override
    @Async
    public void enviarCorreoHtml(EmailDTO emailDTO) throws Exception {


        Email email = EmailBuilder.startingBlank()
                .from(correo)
                .to(emailDTO.destinatario())
                .withSubject(emailDTO.asunto())
                .appendTextHTML(emailDTO.cuerpo())

                .buildEmail();


        try (Mailer mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, correo, contra)
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer()) {

            mailer.sendMail(email);
        }


    }
    @Override
    @Async
    public void enviarCorreoConQr(EmailDTO emailDTO, Orden orden) throws Exception {
        // Generar contenido del QR
        String contenidoQr = generarContenidoQr(orden);

        // Generar la imagen del código QR
        ByteArrayOutputStream qrStream = new ByteArrayOutputStream();
        generarImagenQr(contenidoQr, qrStream);

        System.out.println(emailDTO.destinatario());
        // Crear el correo con el adjunto del QR
        Email email = EmailBuilder.startingBlank()
                .from("unieventosfae@gmail.com")
                .to(emailDTO.destinatario())
                .withSubject(emailDTO.asunto())
                .withPlainText(emailDTO.cuerpo())
                .withAttachment("codigo_qr.png", qrStream.toByteArray(), "image/png")
                .buildEmail();

        // Enviar el correo
        try (Mailer mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, correo, contra)
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer()) {
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
