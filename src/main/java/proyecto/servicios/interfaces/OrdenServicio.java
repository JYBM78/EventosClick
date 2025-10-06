package proyecto.servicios.interfaces;


import com.mercadopago.resources.preference.Preference;
import proyecto.modelo.documentos.Orden;
import proyecto.modelo.dto.orden.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrdenServicio {

    String crearOrden(CrearOrdenDTO crearOrdenDTO) throws Exception;

    String actualizarOrden(EditarOrdenDTO editarOrdenDTO) throws Exception;

    String eliminarOrden(String idOrden) throws Exception;

    List<Orden> buscarOrdenesPorCliente(String idCliente) throws Exception;

   // List<Orden> buscarOrdenesPorRangoDeFechas(Date fechaInicio, Date fechaFin) throws Exception;

    List<Orden> buscarOrdenesPorRangoDeFechas(String fechaInicio, String fechaFin) throws Exception;

    InformacionOrdenDTO obtenerInformacionOrden(String idOrden) throws Exception;

    List<InformacionOrdenDTO> listarTodasLasOrdenes() throws Exception;

    //List<InformacionOrdenDTO> listarOrdenesPorCliente(String idCliente) throws Exception;

    void recibirNotificacionMercadoPago(Map<String, Object> request) throws Exception;

    Preference realizarPago(String idOrden) throws Exception;
    List<InformacionOrdenDTO> obtenerHistorialOrdenes(String idCliente) throws Exception;

}
