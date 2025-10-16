package test;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import proyecto.ProyectoApplication;
import proyecto.modelo.documentos.Orden;
import proyecto.modelo.dto.orden.CrearOrdenDTO;
import proyecto.modelo.dto.orden.EditarOrdenDTO;
import proyecto.modelo.dto.orden.InformacionOrdenDTO;
import proyecto.modelo.vo.DetalleOrden;
import proyecto.servicios.interfaces.OrdenServicio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = ProyectoApplication.class)
public class OrdenServicioTest {
    @Autowired
    private OrdenServicio ordenServicio;
    @Test
    public void crearOrdenTest() {
        String idEvento = "evento001";

        // Crear un detalle de orden
        DetalleOrden detalleOrden = new DetalleOrden("001", idEvento, 100.0f, "General", 2);
        List<DetalleOrden> items = Arrays.asList(detalleOrden);

        // Crear el DTO de orden
        CrearOrdenDTO crearOrdenDTO = new CrearOrdenDTO(
                "652c95c6f0b56723d4638913", // idCliente
                "CodigoPasarela1",           // codigoPasarela
                100f,                        // total
                items                        // items
        );

        // Probar creación de la orden
        assertDoesNotThrow(() -> {
            String idOrden = ordenServicio.crearOrden(crearOrdenDTO);
            assertNotNull(idOrden, "El ID de la orden no debe ser nulo");
            System.out.println("Orden creada con ID: " + idOrden);
        });
    }


    /**
     * Test para actualizar una orden.
     */
    @Test
    public void actualizarOrdenTest() {
        DetalleOrden detalleOrden = new DetalleOrden("Producto1", "evento001", 100.0f, "General", 2 );
        List<DetalleOrden> items = Arrays.asList(detalleOrden); // Lista de detalles



        EditarOrdenDTO editarOrdenDTO = new EditarOrdenDTO(
                "6701fcf0bdb88d4aa14f6547",
                "652c95c6f0b56723d4638913",
                //"evento001",
                LocalDate.now(),
                "CODIGO_PASARELA_202",
                150f,
                items

        );

        assertDoesNotThrow(() -> {
            String resultado = ordenServicio.actualizarOrden(editarOrdenDTO);
            assertEquals("La orden ha sido actualizada con éxito.", resultado);

        });
    }

    /**
     * Test para eliminar una orden.
     */
    @Test
    public void eliminarOrdenTest() {
        String idOrden = "6701fd0d44bf8e22611ddeaa";
        assertDoesNotThrow(() -> {
            String resultado = ordenServicio.eliminarOrden(idOrden);
            assertEquals("La orden ha sido eliminada.", resultado);  // Validar mensaje de éxito
        });
    }

    /**
     * Test para buscar órdenes por cliente.
     */
    @Test
    public void buscarOrdenesPorClienteTest() {
        String idCliente = "670d2a7614d99c1d4d8c20fa";
        assertDoesNotThrow(() -> {
            List<Orden> ordenes = ordenServicio.buscarOrdenesPorCliente(idCliente);
            assertNotNull(ordenes);  // Verificar que la lista no sea nula
            assertFalse(ordenes.isEmpty());  // Verificar que la lista no esté vacía
        });
    }

    /**
     * Test para buscar órdenes por rango de fechas.
     */
    @Test
    public void buscarOrdenesPorRangoDeFechasTest() {
        String fechaInicio = "2024-01-01";
        String fechaFin = "2024-12-31";

        assertDoesNotThrow(() -> {
            List<Orden> ordenes = ordenServicio.buscarOrdenesPorRangoDeFechas(fechaInicio, fechaFin);
            assertNotNull(ordenes);  // Verificar que la lista no sea nula
            assertFalse(ordenes.isEmpty());  // Verificar que la lista no esté vacía
        });
    }

    /**
     * Test para obtener información de una orden específica.
     */
    @Test
    public void obtenerInformacionOrdenTest() {
        String idOrden = "6701fcf0bdb88d4aa14f6547";
        assertDoesNotThrow(() -> {
            InformacionOrdenDTO infoOrden = ordenServicio.obtenerInformacionOrden(idOrden);
            assertNotNull(infoOrden);  // Verificar que la información de la orden no sea nula
            assertEquals("6701fcf0bdb88d4aa14f6547", infoOrden.id());  // Verificar que el ID de la orden coincida
        });
    }

    /**
     * Test para listar todas las órdenes.
     */
    @Test
    public void listarTodasLasOrdenesTest() {
        assertDoesNotThrow(() -> {
            List<InformacionOrdenDTO> ordenes = ordenServicio.listarTodasLasOrdenes();
            assertNotNull(ordenes);  // Verificar que la lista no sea nula
            assertFalse(ordenes.isEmpty());  // Verificar que la lista no esté vacía
        });
    }
}
