package com.mycompany.gestion_hotel.ui;

import com.mycompany.gestion_hotel.modelo.Producto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class VentasControllerTest {

    private VentasController controller;
    private Constructor<?> itemCarritoConstructor;

    @BeforeEach
    public void setUp() throws Exception {
        controller = new VentasController();
        
        // Encontrar la clase interna ItemCarrito
        Class<?>[] declaredClasses = VentasController.class.getDeclaredClasses();
        for (Class<?> c : declaredClasses) {
            if (c.getSimpleName().equals("ItemCarrito")) {
                // El constructor necesita 3 parámetros: VentasController, Producto, int
                itemCarritoConstructor = c.getDeclaredConstructor(
                    VentasController.class, Producto.class, int.class
                );
                itemCarritoConstructor.setAccessible(true);
                break;
            }
        }
        
        assertNotNull(itemCarritoConstructor, "Constructor de ItemCarrito no encontrado");
        
        // Inicializar lista vacía
        Field itemsCarritoField = VentasController.class.getDeclaredField("itemsCarrito");
        itemsCarritoField.setAccessible(true);
        itemsCarritoField.set(controller, new ArrayList<>());
    }

    private Object crearItemCarrito(Producto producto, int cantidad) throws Exception {
        // Pasar la instancia del controller como primer parámetro
        return itemCarritoConstructor.newInstance(controller, producto, cantidad);
    }

    @Test
    public void testCalcularTotal_CarritoVacio() throws Exception {
        double total = controller.calcularTotal();
        assertEquals(0, total, 0.001, "El total debería ser 0 para carrito vacío");
    }

    @Test
    public void testCalcularTotal_ConProductos() throws Exception {
        // Crear productos de prueba
        Producto gaseosa = new Producto(1, "Gaseosa", "Bebida", 50, 5000, "Nevera", 10);
        Producto empanada = new Producto(2, "Empanada", "Comida", 40, 3000, "Mostrador", 11);

        // Crear items del carrito
        List<Object> items = new ArrayList<>();
        items.add(crearItemCarrito(gaseosa, 2)); // 2 gaseosas a $5000
        items.add(crearItemCarrito(empanada, 3)); // 3 empanadas a $3000

        // Establecer la lista en el controlador
        Field itemsCarritoField = VentasController.class.getDeclaredField("itemsCarrito");
        itemsCarritoField.setAccessible(true);
        itemsCarritoField.set(controller, items);

        // Calcular y verificar total
        double total = controller.calcularTotal();
        double expectedTotal = (2 * 5000) + (3 * 3000); // 10000 + 9000 = 19000
        assertEquals(expectedTotal, total, 0.001, "El cálculo del total es incorrecto");
    }

    @Test
    public void testCalcularTotal_ConDecimales() throws Exception {
        Producto productoDecimal = new Producto(3, "Café", "Bebida", 20, 2500.75, "Vitrina", 12);
        
        List<Object> items = new ArrayList<>();
        items.add(crearItemCarrito(productoDecimal, 4)); // 4 cafés a $2500.75

        Field itemsCarritoField = VentasController.class.getDeclaredField("itemsCarrito");
        itemsCarritoField.setAccessible(true);
        itemsCarritoField.set(controller, items);

        double total = controller.calcularTotal();
        double expectedTotal = 4 * 2500.75; // 10003.0
        assertEquals(expectedTotal, total, 0.001, "El cálculo con decimales es incorrecto");
    }

    @Test
    public void testCalcularTotal_MultiplesItemsMismoProducto() throws Exception {
        Producto producto = new Producto(1, "Agua", "Bebida", 100, 2000, "Nevera", 1);
        
        List<Object> items = new ArrayList<>();
        items.add(crearItemCarrito(producto, 1));
        items.add(crearItemCarrito(producto, 2)); // Mismo producto, cantidad adicional
        items.add(crearItemCarrito(producto, 1)); // Mismo producto, otra cantidad

        Field itemsCarritoField = VentasController.class.getDeclaredField("itemsCarrito");
        itemsCarritoField.setAccessible(true);
        itemsCarritoField.set(controller, items);

        double total = controller.calcularTotal();
        double expectedTotal = (1 + 2 + 1) * 2000; // 4 * 2000 = 8000
        assertEquals(expectedTotal, total, 0.001, "El cálculo con múltiples items del mismo producto es incorrecto");
    }
}