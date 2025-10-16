package test;



import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import proyecto.ProyectoApplication;
import proyecto.modelo.documentos.Carrito;
import proyecto.modelo.dto.carrito.DetalleCarritoDTO;
import proyecto.modelo.dto.carrito.InformacionCarritoDTO;
import proyecto.modelo.vo.DetalleCarrito;
import proyecto.servicios.interfaces.CarritoServicio;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration(classes = ProyectoApplication.class)
public class CarritoServiceTest {

    @Autowired
    private CarritoServicio carritoServicio;






    @Test
    void testAgregarItem() throws Exception {
        // Crear un nuevo DetalleCarrito
        DetalleCarrito item = new DetalleCarrito();
        item.setIdDetalleCarrito("idPrueba");
        item.setIdEvento("evento001");
        item.setCantidad(2);
        item.setNombreLocalidad("General");

        DetalleCarritoDTO detalleCarritoDTO = new DetalleCarritoDTO("idDtalle01","evento002",1,"VIP", 10000F );
        // Agregar el item al carrito
        carritoServicio.agregarItem("6701eedf2c4a9b1234567890", detalleCarritoDTO);


        // Traer el carrito actualizado
        InformacionCarritoDTO carritoActualizado = carritoServicio.traerCarrito("6701eedf2c4a9b1234567890");

        // Verificar que el item fue agregado
        assertEquals(3, carritoActualizado.items().size());
        assertEquals("6701eea02f877bfc0e9397cf", carritoActualizado.items().get(0).idEvento());
       // assertEquals(2, carritoActualizado.getItems().get(0).getCantidad());
    }

    @Test
    void testEliminarItem() throws Exception {
        // Crear un nuevo DetalleCarrito y agregarlo
        DetalleCarrito item = new DetalleCarrito();
        item.setIdEvento("evento002");
        item.setCantidad(1);
        item.setNombreLocalidad("VIP");
        DetalleCarritoDTO detalleCarritoDTO = new DetalleCarritoDTO("idDtalle01","evento002",1,"VIP", 10000F );
        carritoServicio.agregarItem("6701eefb2c4a9b1234567891", detalleCarritoDTO);//carrito 2

        // Asegurarse que el item está en el carrito
       // assertEquals(1, carrito.getItems().size());

        // Eliminar el item
        String result = carritoServicio.eliminarItem("6701eefb2c4a9b1234567891", "item001");

        // Verificar que el item fue eliminado
        assertEquals("Item eliminado correctamente", result);
        //assertEquals(0, carrito.getItems().size());
    }

    @Test
    void testTraerCarrito() throws Exception {
        // Crear un nuevo DetalleCarrito y agregarlo
        DetalleCarrito item = new DetalleCarrito();
        item.setIdEvento("evento003");
        item.setCantidad(3);
        item.setNombreLocalidad("Platea");
       // carritoServicio.agregarItem("6701eefb2c4a9b1234567892", item);///carrito 3

        // Traer el carrito
        InformacionCarritoDTO carritoTraido = carritoServicio.traerCarrito("6701eedf2c4a9b1234567890");

        // Verificar que el carrito tiene los items correctos
       // assertNotNull(carritoTraido);
        assertEquals(3, carritoTraido.items().size());
        assertEquals("6701eea02f877bfc0e9397cf", carritoTraido.items().get(0).idEvento());
        //assertEquals(3, carritoTraido.getItems().get(0).getCantidad());
    }



}
