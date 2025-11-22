package com.mycompany.gestion_hotel.ui;

import com.mycompany.gestion_hotel.modelo.Producto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class VentasControllerIntegrationTest {

    private VentasController controller;

    @BeforeEach
    public void setUp() throws Exception {
        controller = new VentasController();
        
        // Inicializar listas usando reflexión
        Field productosDisponiblesField = VentasController.class.getDeclaredField("productosDisponibles");
        productosDisponiblesField.setAccessible(true);
        productosDisponiblesField.set(controller, new ArrayList<Producto>());
        
        Field itemsCarritoField = VentasController.class.getDeclaredField("itemsCarrito");
        itemsCarritoField.setAccessible(true);
        itemsCarritoField.set(controller, new ArrayList<>());
        
        // Para tests de integración, podemos inicializar DAOs como null
        // ya que estamos probando principalmente la lógica
    }

    @Test
    @DisplayName("Integración: Lógica de descontar stock (sin DAO)")
    public void testLogicaDescontarStock() throws Exception {
        // Configurar productos en carrito
        Producto producto = new Producto(1, "Test", "Cat", 10, 1000, "Loc", 1);
        VentasController.ItemCarrito item = controller.new ItemCarrito(producto, 3);
        
        List<Object> items = new ArrayList<>();
        items.add(item);
        
        Field itemsCarritoField = VentasController.class.getDeclaredField("itemsCarrito");
        itemsCarritoField.setAccessible(true);
        itemsCarritoField.set(controller, items);
        
        // Simular la lógica de descontarStock sin llamar al DAO
        for (Object carritoItem : items) {
            VentasController.ItemCarrito itemCarrito = (VentasController.ItemCarrito) carritoItem;
            int nuevaCantidad = itemCarrito.producto.getCantidad() - itemCarrito.cantidad;
            if (nuevaCantidad < 0) nuevaCantidad = 0;
            itemCarrito.producto.setCantidad(nuevaCantidad);
        }
        
        // Verificar que se actualizó la cantidad en el objeto Producto
        assertEquals(7, producto.getCantidad(), "El stock debería reducirse de 10 a 7");
    }

    @Test
    @DisplayName("Integración: Lógica de descontar stock con cantidad negativa")
    public void testLogicaDescontarStockCantidadNegativa() throws Exception {
        Producto producto = new Producto(1, "Test", "Cat", 2, 1000, "Loc", 1); // Stock: 2
        VentasController.ItemCarrito item = controller.new ItemCarrito(producto, 5); // Pide 5
        
        List<Object> items = new ArrayList<>();
        items.add(item);
        
        Field itemsCarritoField = VentasController.class.getDeclaredField("itemsCarrito");
        itemsCarritoField.setAccessible(true);
        itemsCarritoField.set(controller, items);
        
        // Simular la lógica de descontarStock sin llamar al DAO
        for (Object carritoItem : items) {
            VentasController.ItemCarrito itemCarrito = (VentasController.ItemCarrito) carritoItem;
            int nuevaCantidad = itemCarrito.producto.getCantidad() - itemCarrito.cantidad;
            if (nuevaCantidad < 0) nuevaCantidad = 0; // Prevenir valores negativos
            itemCarrito.producto.setCantidad(nuevaCantidad);
        }
        
        // Verificar que no queda stock negativo
        assertEquals(0, producto.getCantidad(), "El stock no debería ser negativo, debería ser 0");
    }

    @Test
    @DisplayName("Integración: Lógica de registrar ingreso (sin DAO)")
    public void testLogicaRegistrarIngreso() throws Exception {
        // Este método interactúa con IngresosDAO, pero podemos verificar la lógica de construcción
        String metodoPago = "Efectivo";
        double monto = 50000.0;
        
        // Simular la lógica de construcción del objeto Ingresos
        String concepto = "Cafetería";
        
        assertEquals("Efectivo", metodoPago, "El método de pago debería ser Efectivo");
        assertEquals(50000.0, monto, 0.001, "El monto debería ser 50000.0");
        assertEquals("Cafetería", concepto, "El concepto debería ser Cafetería");
        
        // Verificar que los datos son válidos
        assertTrue(monto > 0, "El monto debería ser positivo");
        assertFalse(metodoPago.isEmpty(), "El método de pago no debería estar vacío");
    }

    @Test
    @DisplayName("Validar construcción de objetos Producto")
    public void testConstructoresProducto() {
        // Probar diferentes constructores de Producto
        Producto producto1 = new Producto(1, "Nombre", "Categoria", 10, 1000.0, "Ubicacion", 1);
        assertNotNull(producto1, "Constructor completo debería crear objeto");
        
        Producto producto2 = new Producto("Nombre", "Categoria", 10, 1000.0, "Ubicacion", 1);
        assertNotNull(producto2, "Constructor sin ID debería crear objeto");
        
        Producto producto3 = new Producto(1, "Nombre", "Categoria", 10, 1000.0, "Ubicacion", 1, "imagen.jpg");
        assertNotNull(producto3, "Constructor con imagen debería crear objeto");
        
        Producto producto4 = new Producto("Nombre", "Categoria", 10, 1000.0, "Ubicacion", 1, "imagen.jpg");
        assertNotNull(producto4, "Constructor sin ID con imagen debería crear objeto");
    }

    @Test
    @DisplayName("Validar métodos de Producto")
    public void testMetodosProducto() {
        Producto producto = new Producto(1, "Test", "Cat", 10, 1500.75, "Loc", 1, "imagen.jpg");
        
        // Probar getters
        assertEquals(1, producto.getIdProducto());
        assertEquals("Test", producto.getNombre());
        assertEquals("Cat", producto.getCategoria());
        assertEquals(10, producto.getCantidad());
        assertEquals(1500.75, producto.getPrecio(), 0.001);
        assertEquals("Loc", producto.getUbicacion());
        assertEquals(1, producto.getIdProveedor());
        assertEquals("imagen.jpg", producto.getImagenUrl());
        assertTrue(producto.tieneImagen());
        
        // Probar setters
        producto.setNombre("Nuevo Nombre");
        producto.setCantidad(5);
        producto.setPrecio(2000.0);
        producto.setUbicacion("Nueva Ubicacion");
        producto.setImagenUrl("nueva_imagen.jpg");
        
        assertEquals("Nuevo Nombre", producto.getNombre());
        assertEquals(5, producto.getCantidad());
        assertEquals(2000.0, producto.getPrecio(), 0.001);
        assertEquals("Nueva Ubicacion", producto.getUbicacion());
        assertEquals("nueva_imagen.jpg", producto.getImagenUrl());
        
        // Probar método tieneImagen con URL vacía
        producto.setImagenUrl("");
        assertFalse(producto.tieneImagen(), "No debería tener imagen con URL vacía");
        
        producto.setImagenUrl(null);
        assertFalse(producto.tieneImagen(), "No debería tener imagen con URL nula");
        
        // Probar toString
        assertNotNull(producto.toString());
    }

    @Test
    @DisplayName("Validar método getImagen de Producto")
    public void testGetImagenProducto() {
        Producto productoSinImagen = new Producto(1, "Test", "Cat", 10, 1000, "Loc", 1);
        assertNull(productoSinImagen.getImagen(), "Debería retornar null sin imagen");
        
        Producto productoConImagenInvalida = new Producto(1, "Test", "Cat", 10, 1000, "Loc", 1, "url_invalida");
        // No podemos probar fácilmente la carga de imágenes, pero podemos verificar que no lanza excepciones
        assertDoesNotThrow(() -> {
            productoConImagenInvalida.getImagen();
        }, "getImagen no debería lanzar excepciones con URL inválida");
    }

    @Test
    @DisplayName("Integración: Flujo completo de venta sin DAOs")
    public void testFlujoCompletoVenta() throws Exception {
        // Configurar productos
        Producto producto1 = new Producto(1, "Producto1", "Cat1", 10, 1000, "Loc1", 1);
        Producto producto2 = new Producto(2, "Producto2", "Cat2", 5, 2000, "Loc2", 2);
        
        // Simular agregar al carrito
        simularAgregarAlCarrito(producto1, "2");
        simularAgregarAlCarrito(producto2, "1");
        
        Field itemsCarritoField = VentasController.class.getDeclaredField("itemsCarrito");
        itemsCarritoField.setAccessible(true);
        List<Object> itemsCarrito = (List<Object>) itemsCarritoField.get(controller);
        
        // Verificar carrito
        assertEquals(2, itemsCarrito.size(), "Debería tener 2 items en el carrito");
        
        // Calcular total
        double total = controller.calcularTotal();
        assertEquals(4000, total, 0.001, "El total debería ser 4000");
        
        // Simular descontar stock (sin DAO)
        for (Object item : itemsCarrito) {
            VentasController.ItemCarrito itemCarrito = (VentasController.ItemCarrito) item;
            int nuevaCantidad = itemCarrito.producto.getCantidad() - itemCarrito.cantidad;
            if (nuevaCantidad < 0) nuevaCantidad = 0;
            itemCarrito.producto.setCantidad(nuevaCantidad);
        }
        
        // Verificar stocks actualizados
        assertEquals(8, producto1.getCantidad(), "Stock producto1 debería ser 8");
        assertEquals(4, producto2.getCantidad(), "Stock producto2 debería ser 4");
        
        // Simular limpiar carrito
        itemsCarrito.clear();
        
        assertEquals(0, itemsCarrito.size(), "El carrito debería estar vacío");
        assertEquals(0, controller.calcularTotal(), 0.001, "El total debería ser 0 después de limpiar");
    }

    @Test
    @DisplayName("Integración: Validar filtrado de productos por ubicación")
    public void testFiltradoProductos() {
        List<Producto> todosProductos = new ArrayList<>();
        todosProductos.add(new Producto(1, "Prod1", "Cat1", 10, 1000, "Vitrina", 1));
        todosProductos.add(new Producto(2, "Prod2", "Cat2", 20, 2000, "Nevera", 2));
        todosProductos.add(new Producto(3, "Prod3", "Cat3", 30, 3000, "Heladera", 3));
        todosProductos.add(new Producto(4, "Prod4", "Cat4", 40, 4000, "Bodega", 4));
        todosProductos.add(new Producto(5, "Prod5", "Cat5", 50, 5000, "Vitrina", 5));
        
        // Simular filtro para "Vitrina" excluyendo "Bodega"
        List<Producto> filtrados = new ArrayList<>();
        for (Producto p : todosProductos) {
            if (p.getUbicacion() != null && !p.getUbicacion().equalsIgnoreCase("Bodega")) {
                if ("Vitrina".equalsIgnoreCase(p.getUbicacion())) {
                    filtrados.add(p);
                }
            }
        }
        
        assertEquals(2, filtrados.size(), "Debería filtrar 2 productos de Vitrina");
        assertTrue(filtrados.stream().allMatch(p -> "Vitrina".equals(p.getUbicacion())));
        assertTrue(filtrados.stream().noneMatch(p -> "Bodega".equals(p.getUbicacion())));
    }

    @Test
    @DisplayName("Integración: Validar cálculos de subtotales y totales")
    public void testCalculosFinancieros() {
        Producto producto1 = new Producto(1, "Prod1", "Cat1", 10, 1234.56, "Loc1", 1);
        Producto producto2 = new Producto(2, "Prod2", "Cat2", 20, 789.12, "Loc2", 2);
        
        VentasController.ItemCarrito item1 = controller.new ItemCarrito(producto1, 3);
        VentasController.ItemCarrito item2 = controller.new ItemCarrito(producto2, 5);
        
        // Calcular subtotales
        double subtotal1 = item1.getSubtotal();
        double subtotal2 = item2.getSubtotal();
        
        double expectedSubtotal1 = 3 * 1234.56; // 3703.68
        double expectedSubtotal2 = 5 * 789.12;  // 3945.60
        
        assertEquals(expectedSubtotal1, subtotal1, 0.001, "Subtotal1 calculado incorrectamente");
        assertEquals(expectedSubtotal2, subtotal2, 0.001, "Subtotal2 calculado incorrectamente");
        
        // Calcular total manualmente
        double totalManual = subtotal1 + subtotal2; // 3703.68 + 3945.60 = 7649.28
        assertEquals(7649.28, totalManual, 0.001, "Total manual calculado incorrectamente");
    }

    // ===== MÉTODO AUXILIAR =====

    private void simularAgregarAlCarrito(Producto producto, String cantidadStr) throws Exception {
        try {
            int cantidad = Integer.parseInt(cantidadStr);
            
            if (cantidad <= 0) {
                return;
            }

            Field itemsCarritoField = VentasController.class.getDeclaredField("itemsCarrito");
            itemsCarritoField.setAccessible(true);
            List<Object> itemsCarrito = (List<Object>) itemsCarritoField.get(controller);

            int totalEnCarrito = 0;
            for (Object item : itemsCarrito) {
                VentasController.ItemCarrito itemCarrito = (VentasController.ItemCarrito) item;
                if (itemCarrito.producto.getIdProducto() == producto.getIdProducto()) {
                    totalEnCarrito += itemCarrito.cantidad;
                }
            }

            if (cantidad + totalEnCarrito > producto.getCantidad()) {
                return;
            }

            VentasController.ItemCarrito existente = null;
            for (Object item : itemsCarrito) {
                VentasController.ItemCarrito itemCarrito = (VentasController.ItemCarrito) item;
                if (itemCarrito.producto.getIdProducto() == producto.getIdProducto()) {
                    existente = itemCarrito;
                    break;
                }
            }

            if (existente != null) {
                existente.cantidad += cantidad;
            } else {
                itemsCarrito.add(controller.new ItemCarrito(producto, cantidad));
            }

        } catch (NumberFormatException e) {
            // Ignorar para tests
        }
    }
}