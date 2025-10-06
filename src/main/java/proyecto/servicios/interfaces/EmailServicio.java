package proyecto.servicios.interfaces;


import proyecto.modelo.documentos.Orden;
import proyecto.modelo.dto.email.EmailDTO;

public interface EmailServicio {

    void enviarCorreo(EmailDTO emailDTO) throws Exception;
    void enviarCorreoHtml(EmailDTO emailDTO) throws Exception;
    void enviarCorreoConQr(EmailDTO emailDTO, Orden orden ) throws Exception;

}
