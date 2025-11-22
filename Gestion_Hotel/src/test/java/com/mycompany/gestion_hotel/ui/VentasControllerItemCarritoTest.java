package com.mycompany.gestion_hotel.ui;

import com.mycompany.gestion_hotel.modelo.Producto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class VentasControllerItemCarritoTest {

    @Test
    public void testItemCarritoGetSubtotal() throws Exception {
        VentasController controller = new VentasController();
        Producto producto = new Producto(1, "Test", "Categoria", 10, 1500.50, "Ubicacion", 1);
        
        // Encontrar clase ItemCarrito
        Class<?> itemCarritoClass = null;
        for (Class<?> c : VentasController.class.getDeclaredClasses()) {
            if (c.getSimpleName().equals("ItemCarrito")) {
                itemCarritoClass = c;
                break;
            }
        }
        assertNotNull(itemCarritoClass, "Clase ItemCarrito no encontrada");
        
        // Crear instancia de ItemCarrito
        Constructor<?> constructor = itemCarritoClass.getDeclaredConstructor(
            VentasController.class, Producto.class, int.class
        );
        constructor.setAccessible(true);
        Object itemCarrito = constructor.newInstance(controller, producto, 3);
        
        // Obtener y ejecutar método getSubtotal
        Method getSubtotalMethod = itemCarritoClass.getDeclaredMethod("getSubtotal");
        getSubtotalMethod.setAccessible(true);
        double subtotal = (Double) getSubtotalMethod.invoke(itemCarrito);
        
        // Verificar cálculo
        double expectedSubtotal = 3 * 1500.50; // 4501.50
        assertEquals(expectedSubtotal, subtotal, 0.001, 
            "El método getSubtotal no calcula correctamente");
    }
    
    @Test
    public void testItemCarritoCampos() throws Exception {
        VentasController controller = new VentasController();
        Producto producto = new Producto(1, "Test", "Cat", 5, 1000, "Loc", 1);
        
        Class<?> itemCarritoClass = null;
        for (Class<?> c : VentasController.class.getDeclaredClasses()) {
            if (c.getSimpleName().equals("ItemCarrito")) {
                itemCarritoClass = c;
                break;
            }
        }
        
        Constructor<?> constructor = itemCarritoClass.getDeclaredConstructor(
            VentasController.class, Producto.class, int.class
        );
        constructor.setAccessible(true);
        Object itemCarrito = constructor.newInstance(controller, producto, 5);
        
        // Verificar campos
        Field productoField = itemCarritoClass.getDeclaredField("producto");
        productoField.setAccessible(true);
        Producto productoEnItem = (Producto) productoField.get(itemCarrito);
        assertEquals(producto, productoEnItem, "El producto no se asignó correctamente");
        
        Field cantidadField = itemCarritoClass.getDeclaredField("cantidad");
        cantidadField.setAccessible(true);
        int cantidadEnItem = (Integer) cantidadField.get(itemCarrito);
        assertEquals(5, cantidadEnItem, "La cantidad no se asignó correctamente");
    }
}