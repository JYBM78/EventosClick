package proyecto.modelo.vo;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Localidad {

    private double precio;
    private String nombre;
    private int entradasVendidas;
    private int capacidadMaxima;
    private List<Silla> sillas;


    public Localidad(double precio, String nombre, int capacidadMaxima) {
        this.precio = precio;
        this.nombre = nombre;
        this.capacidadMaxima = capacidadMaxima;
        this.sillas =  new ArrayList<>();
    }
    public int getCapacidadDisponible(){
        return capacidadMaxima - entradasVendidas;
    }
}
