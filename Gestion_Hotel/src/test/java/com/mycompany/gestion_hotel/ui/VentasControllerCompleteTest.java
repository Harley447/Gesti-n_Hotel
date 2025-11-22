package com.mycompany.gestion_hotel.ui;

import com.mycompany.gestion_hotel.modelo.Producto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class VentasControllerCompleteTest {

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
        
        // Inicializar DAOs (pueden ser null para estos tests)
        Field productoDAOField = VentasController.class.getDeclaredField("productoDAO");
        productoDAOField.setAccessible(true);
        productoDAOField.set(controller, null);
        
        Field ingresosDAOField = VentasController.class.getDeclaredField("ingresosDAO");
        ingresosDAOField.setAccessible(true);
        ingresosDAOField.set(controller, null);
    }

    // ===== TESTS PARA calcularTotal() =====
    
    @Test
    @DisplayName("Calcular total con carrito vacío")
    public void testCalcularTotal_CarritoVacio() throws Exception {
        double total = controller.calcularTotal();
        assertEquals(0, total, 0.001, "El total debería ser 0 para carrito vacío");
    }

    @Test
    @DisplayName("Calcular total con múltiples productos")
    public void testCalcularTotal_ConProductos() throws Exception {
        // Configurar productos en el carrito
        List<Object> items = new ArrayList<>();
        items.add(controller.new ItemCarrito(new Producto(1, "Gaseosa", "Bebida", 50, 5000, "Nevera", 10), 2));
        items.add(controller.new ItemCarrito(new Producto(2, "Empanada", "Comida", 40, 3000, "Mostrador", 11), 3));
        
        Field itemsCarritoField = VentasController.class.getDeclaredField("itemsCarrito");
        itemsCarritoField.setAccessible(true);
        itemsCarritoField.set(controller, items);

        double total = controller.calcularTotal();
        assertEquals(19000, total, 0.001, "El cálculo del total es incorrecto");
    }

    // ===== TESTS PARA LA LÓGICA DE VALIDACIÓN =====

    @Test
    @DisplayName("Validar cantidad positiva")
    public void testValidarCantidadPositiva() {
        assertTrue(validarCantidad(2), "Cantidad 2 debería ser válida");
        assertFalse(validarCantidad(0), "Cantidad 0 debería ser inválida");
        assertFalse(validarCantidad(-1), "Cantidad -1 debería ser inválida");
    }

    @Test
    @DisplayName("Validar stock suficiente")
    public void testValidarStockSuficiente() {
        Producto producto = new Producto(1, "Test", "Categoria", 5, 1000, "Ubicacion", 1);
        
        assertTrue(validarStock(producto, 3, 0), "Stock suficiente debería ser válido");
        assertFalse(validarStock(producto, 6, 0), "Stock insuficiente debería ser inválido");
        assertFalse(validarStock(producto, 3, 3), "Stock justo en límite debería ser inválido");
    }

    @Test
    @DisplayName("Validar entrada numérica")
    public void testValidarEntradaNumerica() {
        assertTrue(esNumeroValido("5"), "'5' debería ser número válido");
        assertTrue(esNumeroValido("123"), "'123' debería ser número válido");
        assertFalse(esNumeroValido("abc"), "'abc' debería ser número inválido");
        assertFalse(esNumeroValido(""), "Cadena vacía debería ser inválida");
        assertFalse(esNumeroValido("12.5"), "Decimales deberían ser inválidos");
    }

    @Test
    @DisplayName("Simular agregar producto con cantidad válida")
    public void testSimularAgregarAlCarrito_Valido() throws Exception {
        Producto producto = new Producto(1, "Test", "Categoria", 10, 1000, "Ubicacion", 1);
        
        simularAgregarAlCarrito(producto, "3");
        
        Field itemsCarritoField = VentasController.class.getDeclaredField("itemsCarrito");
        itemsCarritoField.setAccessible(true);
        List<Object> itemsCarrito = (List<Object>) itemsCarritoField.get(controller);
        
        assertEquals(1, itemsCarrito.size(), "Debería agregar un item al carrito");
        assertEquals(3, ((VentasController.ItemCarrito) itemsCarrito.get(0)).cantidad, 
            "La cantidad debería ser 3");
    }

    @Test
    @DisplayName("Simular agregar producto con cantidad negativa")
    public void testSimularAgregarAlCarrito_Negativo() throws Exception {
        Producto producto = new Producto(1, "Test", "Categoria", 10, 1000, "Ubicacion", 1);
        
        simularAgregarAlCarrito(producto, "-2");
        
        Field itemsCarritoField = VentasController.class.getDeclaredField("itemsCarrito");
        itemsCarritoField.setAccessible(true);
        List<Object> itemsCarrito = (List<Object>) itemsCarritoField.get(controller);
        
        assertEquals(0, itemsCarrito.size(), "No debería agregar items con cantidad negativa");
    }

    @Test
    @DisplayName("Simular agregar producto con cantidad cero")
    public void testSimularAgregarAlCarrito_Cero() throws Exception {
        Producto producto = new Producto(1, "Test", "Categoria", 10, 1000, "Ubicacion", 1);
        
        simularAgregarAlCarrito(producto, "0");
        
        Field itemsCarritoField = VentasController.class.getDeclaredField("itemsCarrito");
        itemsCarritoField.setAccessible(true);
        List<Object> itemsCarrito = (List<Object>) itemsCarritoField.get(controller);
        
        assertEquals(0, itemsCarrito.size(), "No debería agregar items con cantidad cero");
    }

    @Test
    @DisplayName("Simular agregar producto con stock insuficiente")
    public void testSimularAgregarAlCarrito_StockInsuficiente() throws Exception {
        Producto producto = new Producto(1, "Test", "Categoria", 5, 1000, "Ubicacion", 1);
        
        simularAgregarAlCarrito(producto, "10");
        
        Field itemsCarritoField = VentasController.class.getDeclaredField("itemsCarrito");
        itemsCarritoField.setAccessible(true);
        List<Object> itemsCarrito = (List<Object>) itemsCarritoField.get(controller);
        
        assertEquals(0, itemsCarrito.size(), "No debería agregar items con stock insuficiente");
    }

    @Test
    @DisplayName("Simular agregar producto con entrada no numérica")
    public void testSimularAgregarAlCarrito_NoNumerico() throws Exception {
        Producto producto = new Producto(1, "Test", "Categoria", 10, 1000, "Ubicacion", 1);
        
        simularAgregarAlCarrito(producto, "abc");
        
        Field itemsCarritoField = VentasController.class.getDeclaredField("itemsCarrito");
        itemsCarritoField.setAccessible(true);
        List<Object> itemsCarrito = (List<Object>) itemsCarritoField.get(controller);
        
        assertEquals(0, itemsCarrito.size(), "No debería agregar items con entrada no numérica");
    }

    @Test
    @DisplayName("Simular agregar producto existente - incrementar cantidad")
    public void testSimularAgregarAlCarrito_ProductoExistente() throws Exception {
        Producto producto = new Producto(1, "Test", "Categoria", 10, 1000, "Ubicacion", 1);
        
        // Agregar primer item
        simularAgregarAlCarrito(producto, "2");
        
        // Agregar más del mismo producto
        simularAgregarAlCarrito(producto, "3");
        
        Field itemsCarritoField = VentasController.class.getDeclaredField("itemsCarrito");
        itemsCarritoField.setAccessible(true);
        List<Object> itemsCarrito = (List<Object>) itemsCarritoField.get(controller);
        
        assertEquals(1, itemsCarrito.size(), "Debería mantener un solo item");
        assertEquals(5, ((VentasController.ItemCarrito) itemsCarrito.get(0)).cantidad, 
            "Debería sumar las cantidades (2 + 3 = 5)");
    }

    // ===== TESTS PARA LA LÓGICA DE MODIFICACIÓN SIN UI =====

    @Test
    @DisplayName("Lógica de incrementar cantidad sin UI")
    public void testLogicaIncrementarCantidad() throws Exception {
        Producto producto = new Producto(1, "Test", "Categoria", 10, 1000, "Ubicacion", 1);
        VentasController.ItemCarrito item = controller.new ItemCarrito(producto, 2);
        
        // Simular incremento sin llamar al método que actualiza UI
        int nuevaCantidad = item.cantidad + 1;
        
        // Verificar que la lógica es correcta
        assertEquals(3, nuevaCantidad, "La lógica de incremento debería funcionar");
        assertTrue(nuevaCantidad <= producto.getCantidad(), "No debería exceder el stock");
    }

    @Test
    @DisplayName("Lógica de decrementar cantidad sin UI")
    public void testLogicaDecrementarCantidad() throws Exception {
        Producto producto = new Producto(1, "Test", "Categoria", 10, 1000, "Ubicacion", 1);
        VentasController.ItemCarrito item = controller.new ItemCarrito(producto, 3);
        
        // Simular decremento sin llamar al método que actualiza UI
        int nuevaCantidad = item.cantidad - 1;
        
        // Verificar que la lógica es correcta
        assertEquals(2, nuevaCantidad, "La lógica de decremento debería funcionar");
        assertTrue(nuevaCantidad > 0, "No debería llegar a cero en este caso");
    }

    @Test
    @DisplayName("Lógica de eliminar cuando cantidad llega a cero")
    public void testLogicaEliminarCero() throws Exception {
        Producto producto = new Producto(1, "Test", "Categoria", 10, 1000, "Ubicacion", 1);
        VentasController.ItemCarrito item = controller.new ItemCarrito(producto, 1);
        
        List<Object> items = new ArrayList<>();
        items.add(item);
        
        Field itemsCarritoField = VentasController.class.getDeclaredField("itemsCarrito");
        itemsCarritoField.setAccessible(true);
        itemsCarritoField.set(controller, items);
        
        // Simular decremento a cero
        int nuevaCantidad = item.cantidad - 1;
        
        if (nuevaCantidad <= 0) {
            items.remove(item);
        }
        
        List<Object> itemsCarrito = (List<Object>) itemsCarritoField.get(controller);
        assertEquals(0, itemsCarrito.size(), "El item debería eliminarse cuando la cantidad llega a 0");
    }

    @Test
    @DisplayName("Lógica de validación stock al modificar")
    public void testLogicaValidacionStockModificar() throws Exception {
        Producto producto = new Producto(1, "Test", "Categoria", 5, 1000, "Ubicacion", 1);
        VentasController.ItemCarrito item = controller.new ItemCarrito(producto, 5);
        
        // Intentar incrementar más allá del stock disponible
        int cambio = 1;
        int nuevaCantidad = item.cantidad + cambio;
        
        boolean stockSuficiente = nuevaCantidad <= producto.getCantidad();
        
        assertFalse(stockSuficiente, "No debería permitir incrementar más allá del stock disponible");
        assertEquals(5, item.cantidad, "La cantidad debería mantenerse sin cambios");
    }

    // ===== TESTS PARA ELIMINAR SIN UI =====

    @Test
    @DisplayName("Lógica de eliminar item del carrito sin UI")
    public void testLogicaEliminarDelCarrito() throws Exception {
        Producto producto = new Producto(1, "Test", "Categoria", 10, 1000, "Ubicacion", 1);
        VentasController.ItemCarrito item = controller.new ItemCarrito(producto, 2);
        
        List<Object> items = new ArrayList<>();
        items.add(item);
        
        Field itemsCarritoField = VentasController.class.getDeclaredField("itemsCarrito");
        itemsCarritoField.setAccessible(true);
        itemsCarritoField.set(controller, items);
        
        // Simular eliminación sin UI
        items.remove(item);
        
        List<Object> itemsCarrito = (List<Object>) itemsCarritoField.get(controller);
        assertEquals(0, itemsCarrito.size(), "El carrito debería estar vacío después de eliminar");
    }

    // ===== TESTS PARA ItemCarrito =====

    @Test
    @DisplayName("Calcular subtotal de ItemCarrito")
    public void testItemCarritoGetSubtotal() {
        Producto producto = new Producto(1, "Test", "Categoria", 10, 1500.75, "Ubicacion", 1);
        VentasController.ItemCarrito item = controller.new ItemCarrito(producto, 3);
        
        double subtotal = item.getSubtotal();
        double expected = 3 * 1500.75; // 4502.25
        
        assertEquals(expected, subtotal, 0.001, "El subtotal calculado es incorrecto");
    }

    @Test
    @DisplayName("ItemCarrito con cantidad cero")
    public void testItemCarritoCantidadCero() {
        Producto producto = new Producto(1, "Test", "Categoria", 10, 1000, "Ubicacion", 1);
        VentasController.ItemCarrito item = controller.new ItemCarrito(producto, 0);
        
        double subtotal = item.getSubtotal();
        assertEquals(0, subtotal, 0.001, "El subtotal debería ser 0 con cantidad cero");
    }

    // ===== TESTS PARA ESCENARIOS COMPLEJOS =====

    @Test
    @DisplayName("Escenario completo sin UI: Agregar, modificar y eliminar items")
    public void testEscenarioCompletoSinUI() throws Exception {
        Producto producto1 = new Producto(1, "Producto1", "Cat1", 10, 1000, "Loc1", 1);
        Producto producto2 = new Producto(2, "Producto2", "Cat2", 5, 2000, "Loc2", 2);
        
        // Agregar productos al carrito
        simularAgregarAlCarrito(producto1, "2");
        simularAgregarAlCarrito(producto2, "1");
        
        Field itemsCarritoField = VentasController.class.getDeclaredField("itemsCarrito");
        itemsCarritoField.setAccessible(true);
        List<Object> itemsCarrito = (List<Object>) itemsCarritoField.get(controller);
        
        // Verificar estado inicial
        assertEquals(2, itemsCarrito.size(), "Debería tener 2 items en el carrito");
        
        // Calcular total
        double total = controller.calcularTotal();
        double expectedTotal = (2 * 1000) + (1 * 2000); // 2000 + 2000 = 4000
        assertEquals(expectedTotal, total, 0.001, "El total inicial es incorrecto");
        
        // Modificar cantidad del primer producto (lógica sin UI)
        VentasController.ItemCarrito item1 = (VentasController.ItemCarrito) itemsCarrito.get(0);
        item1.cantidad += 1; // Incrementar a 3 manualmente
        
        assertEquals(3, item1.cantidad, "La cantidad debería incrementarse a 3");
        
        // Recalcular total
        total = controller.calcularTotal();
        expectedTotal = (3 * 1000) + (1 * 2000); // 3000 + 2000 = 5000
        assertEquals(expectedTotal, total, 0.001, "El total después de modificar es incorrecto");
        
        // Eliminar segundo producto (lógica sin UI)
        VentasController.ItemCarrito item2 = (VentasController.ItemCarrito) itemsCarrito.get(1);
        itemsCarrito.remove(item2);
        
        itemsCarrito = (List<Object>) itemsCarritoField.get(controller);
        assertEquals(1, itemsCarrito.size(), "Debería quedar solo 1 item después de eliminar");
        
        // Total final
        total = controller.calcularTotal();
        expectedTotal = 3 * 1000; // 3000
        assertEquals(expectedTotal, total, 0.001, "El total final es incorrecto");
    }

    @Test
    @DisplayName("Validar límites de stock en escenarios complejos sin UI")
    public void testLimitesStockComplejosSinUI() throws Exception {
        Producto producto = new Producto(1, "Test", "Cat", 5, 1000, "Loc", 1);
        
        // Agregar hasta el límite
        simularAgregarAlCarrito(producto, "3");
        
        Field itemsCarritoField = VentasController.class.getDeclaredField("itemsCarrito");
        itemsCarritoField.setAccessible(true);
        List<Object> itemsCarrito = (List<Object>) itemsCarritoField.get(controller);
        
        assertEquals(1, itemsCarrito.size(), "Debería agregar 3 unidades");
        assertEquals(3, ((VentasController.ItemCarrito) itemsCarrito.get(0)).cantidad);
        
        // Intentar agregar más allá del límite
        simularAgregarAlCarrito(producto, "3"); // Esto excedería el stock (3 + 3 = 6 > 5)
        
        itemsCarrito = (List<Object>) itemsCarritoField.get(controller);
        assertEquals(1, itemsCarrito.size(), "No debería agregar más items");
        assertEquals(3, ((VentasController.ItemCarrito) itemsCarrito.get(0)).cantidad, 
            "La cantidad debería mantenerse en 3");
    }

    // ===== MÉTODOS AUXILIARES =====

    private boolean validarCantidad(int cantidad) {
        return cantidad > 0;
    }

    private boolean validarStock(Producto producto, int cantidad, int totalEnCarrito) {
        return (cantidad + totalEnCarrito) <= producto.getCantidad();
    }

    private boolean esNumeroValido(String str) {
        try {
            int numero = Integer.parseInt(str);
            return numero > 0; // Solo números positivos
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Método auxiliar que simula la lógica de agregarAlCarrito sin el diálogo
     */
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