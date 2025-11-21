package com.mycompany.gestion_hotel.ui;
import com.mycompany.gestion_hotel.dao.IngresosDAO;
import com.mycompany.gestion_hotel.modelo.Ingresos;
import com.mycompany.gestion_hotel.dao.ProductoDAO;
import com.mycompany.gestion_hotel.modelo.Producto;
import com.mycompany.gestion_hotel.servicio.WompiService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javafx.event.ActionEvent;

public class VentasController implements Initializable 
{
    
    private WompiService wompi = new WompiService();

    @FXML private ImageView imgQR;
    private IngresosDAO ingresosDAO;
    @FXML private VBox contenedorProductos;
    @FXML private VBox contenedorCarrito;
    @FXML private Label lblTotal;
    @FXML private RadioButton rbEfectivo;
    @FXML private RadioButton rbTransferencia;
    @FXML private Button btnFinalizarVenta;
    @FXML private CheckBox chkVitrina;
    @FXML private CheckBox chkNevera;
    @FXML private CheckBox chkHeladera;
   
    private final ToggleGroup metodoPago = new ToggleGroup();
    private ProductoDAO productoDAO;
    private List<Producto> productosDisponibles;
    
    // Caché para imágenes
    private Map<String, Image> cacheImagenes = new ConcurrentHashMap<>();
    private Set<String> imagenesCargando = Collections.synchronizedSet(new HashSet<>());
    
    // Clase interna para representar items del carrito
    private class ItemCarrito {
        Producto producto;
        int cantidad;
        
        public ItemCarrito(Producto producto, int cantidad) {
            this.producto = producto;
            this.cantidad = cantidad;
        }
        
        public double getSubtotal() {
            return producto.getPrecio() * cantidad;
        }
    }
    
    private List<ItemCarrito> itemsCarrito;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("VentasController inicializado");
        ingresosDAO = new IngresosDAO();
        // Inicializar componentes
        productoDAO = new ProductoDAO();
        productosDisponibles = new ArrayList<>();
        itemsCarrito = new ArrayList<>();
        
        lblTotal.setText("$0");
        rbEfectivo.setToggleGroup(metodoPago);
        rbTransferencia.setToggleGroup(metodoPago);
        
        // Cargar productos (sin imágenes inicialmente)
        cargarProductos();
        actualizarVistaProductos();
    }

    private void cargarProductos() {
        productosDisponibles = productoDAO.listarProductos();
        System.out.println("Productos cargados: " + productosDisponibles.size());
    }

    private void actualizarVistaProductos() {
        contenedorProductos.getChildren().clear();
        
        if (productosDisponibles.isEmpty()) {
            Label lblVacio = new Label("No hay productos disponibles");
            lblVacio.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 16px;");
            contenedorProductos.getChildren().add(lblVacio);
            return;
        }

        GridPane gridProductos = new GridPane();
        gridProductos.setHgap(15);
        gridProductos.setVgap(15);
        gridProductos.setPadding(new Insets(10));
        
        int col = 0;
        int row = 0;
        int maxCols = 3;

        for (Producto producto : productosDisponibles) {
            VBox productoCard = crearTarjetaProducto(producto);
            gridProductos.add(productoCard, col, row);
            
            col++;
            if (col >= maxCols) {
                col = 0;
                row++;
            }
        }

        contenedorProductos.getChildren().add(gridProductos);
    }

    private VBox crearTarjetaProducto(Producto producto) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15; " +
                     "-fx-border-color: #dfe6e9; -fx-border-radius: 10; -fx-border-width: 1;");
        card.setPrefSize(200, 280);
        card.setMaxSize(200, 280);

        StackPane imagenContainer = crearContenedorImagen(producto);

        Label lblNombre = new Label(producto.getNombre());
        lblNombre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2d3436;");
        lblNombre.setWrapText(true);
        lblNombre.setMaxWidth(150);

        Label lblPrecio = new Label(String.format("$%,.0f", producto.getPrecio()));
        lblPrecio.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        Label lblStock = new Label("Stock: " + producto.getCantidad());
        lblStock.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        Button btnAgregar = new Button("Agregar al Carrito");
        btnAgregar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; " +
                           "-fx-background-radius: 5; -fx-padding: 8 15;");
        btnAgregar.setOnAction(e -> agregarAlCarrito(producto));

        if (producto.getCantidad() <= 0) {
            btnAgregar.setDisable(true);
            btnAgregar.setText("Sin Stock");
            btnAgregar.setStyle("-fx-background-color: #bdc3c7; -fx-text-fill: #7f8c8d;");
        }

        card.getChildren().addAll(imagenContainer, lblNombre, lblPrecio, lblStock, btnAgregar);
        return card;
    }

    private StackPane crearContenedorImagen(Producto producto) {
        StackPane imagenContainer = new StackPane();
        imagenContainer.setPrefSize(120, 120);
        imagenContainer.setMaxSize(120, 120);
        imagenContainer.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");

        crearPlaceholderImagen(imagenContainer);
        
        if (producto.tieneImagen()) {
            cargarImagenEnSegundoPlano(producto, imagenContainer);
        }
        
        return imagenContainer;
    }

    private void cargarImagenEnSegundoPlano(Producto producto, StackPane container) {
        String imageUrl = producto.getImagenUrl();
        
        if (cacheImagenes.containsKey(imageUrl)) {
            actualizarImagenEnUI(container, cacheImagenes.get(imageUrl));
            return;
        }
        
        if (imagenesCargando.contains(imageUrl)) return;
        
        imagenesCargando.add(imageUrl);
        
        new Thread(() -> {
            try {
                Image imagen = new Image(imageUrl, 120, 120, true, true, true);
                if (!imagen.isError()) {
                    cacheImagenes.put(imageUrl, imagen);
                    javafx.application.Platform.runLater(() -> actualizarImagenEnUI(container, imagen));
                }
            } catch (Exception ignored) {
            } finally {
                imagenesCargando.remove(imageUrl);
            }
        }).start();
    }

    private void actualizarImagenEnUI(StackPane container, Image imagen) {
        container.getChildren().clear();
        
        ImageView imagenView = new ImageView(imagen);
        imagenView.setFitWidth(120);
        imagenView.setFitHeight(120);
        imagenView.setPreserveRatio(true);
        
        Rectangle clip = new Rectangle(120, 120);
        clip.setArcWidth(8);
        clip.setArcHeight(8);
        imagenView.setClip(clip);
        
        container.getChildren().add(imagenView);
    }

    private void crearPlaceholderImagen(StackPane container) {
        Rectangle placeholder = new Rectangle(120, 120);
        placeholder.setFill(Color.web("#ecf0f1"));
        placeholder.setStroke(Color.web("#bdc3c7"));
        placeholder.setStrokeWidth(1);
        placeholder.setArcWidth(8);
        placeholder.setArcHeight(8);
        
        Label lblNoImagen = new Label("Cargando...");
        lblNoImagen.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");
        
        container.getChildren().addAll(placeholder, lblNoImagen);
    }

    private void agregarAlCarrito(Producto producto) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Agregar al Carrito");
        dialog.setHeaderText("Seleccionar cantidad para: " + producto.getNombre());
        dialog.setContentText("Cantidad:");

        Optional<String> resultado = dialog.showAndWait();
        if (resultado.isPresent()) {
            try {
                int cantidad = Integer.parseInt(resultado.get());
                
                if (cantidad <= 0) {
                    mostrarAlerta("Error", "La cantidad debe ser mayor a 0");
                    return;
                }
                
                if (cantidad > producto.getCantidad()) {
                    mostrarAlerta("Stock Insuficiente", "Stock disponible: " + producto.getCantidad());
                    return;
                }

                ItemCarrito itemExistente = null;
                for (ItemCarrito item : itemsCarrito) {
                    if (item.producto.getIdProducto() == producto.getIdProducto()) {
                        itemExistente = item;
                        break;
                    }
                }

                if (itemExistente != null) {
                    itemExistente.cantidad += cantidad;
                } else {
                    itemsCarrito.add(new ItemCarrito(producto, cantidad));
                }

                actualizarVistaCarrito();
                mostrarAlerta("Éxito", "Producto agregado al carrito");

            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "Ingrese un número válido");
            }
        }
    }

    private void actualizarVistaCarrito() {
        contenedorCarrito.getChildren().clear();
        
        if (itemsCarrito.isEmpty()) {
            Label lblVacio = new Label("El carrito está vacío");
            lblVacio.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
            contenedorCarrito.getChildren().add(lblVacio);
            lblTotal.setText("$0");
            return;
        }

        double total = 0;

        for (ItemCarrito item : itemsCarrito) {
            HBox itemCarrito = crearItemCarrito(item);
            contenedorCarrito.getChildren().add(itemCarrito);
            total += item.getSubtotal();
        }

        lblTotal.setText(String.format("$%,.0f", total));
    }

    private HBox crearItemCarrito(ItemCarrito item) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 10;");
        hbox.setPrefWidth(280);

        VBox info = new VBox(5);
        info.setPrefWidth(180);
        
        Label lblNombre = new Label(item.producto.getNombre());
        lblNombre.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        Label lblDetalles = new Label(String.format("Cantidad: %d x $%,.0f", 
            item.cantidad, item.producto.getPrecio()));
        lblDetalles.setStyle("-fx-font-size: 11px;");

        Label lblSubtotal = new Label(String.format("Subtotal: $%,.0f", item.getSubtotal()));
        lblSubtotal.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        
        info.getChildren().addAll(lblNombre, lblDetalles, lblSubtotal);

        HBox controles = new HBox(5);
        controles.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnMenos = new Button("-");
        btnMenos.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        btnMenos.setOnAction(e -> modificarCantidad(item, -1));
        
        Button btnMas = new Button("+");
        btnMas.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnMas.setOnAction(e -> modificarCantidad(item, 1));
        
        Button btnEliminar = new Button("×");
        btnEliminar.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        btnEliminar.setOnAction(e -> eliminarDelCarrito(item));
        
        controles.getChildren().addAll(btnMenos, btnMas, btnEliminar);

        hbox.getChildren().addAll(info, controles);
        return hbox;
    }

    private void modificarCantidad(ItemCarrito item, int cambio) {
        int nuevaCantidad = item.cantidad + cambio;
        
        if (nuevaCantidad <= 0) {
            eliminarDelCarrito(item);
            return;
        }
        
        if (nuevaCantidad > item.producto.getCantidad()) {
            mostrarAlerta("Stock Insuficiente", "Stock disponible: " + item.producto.getCantidad());
            return;
        }
        
        item.cantidad = nuevaCantidad;
        actualizarVistaCarrito();
    }

    private void eliminarDelCarrito(ItemCarrito item) {
        itemsCarrito.remove(item);
        actualizarVistaCarrito();
        mostrarAlerta("Eliminado", "Producto eliminado del carrito");
    }

    @FXML
    private void aplicarFiltro(ActionEvent event) {
        List<Producto> productosFiltrados = new ArrayList<>();
        
        boolean filtroVitrina = chkVitrina.isSelected();
        boolean filtroNevera = chkNevera.isSelected();
        boolean filtroHeladera = chkHeladera.isSelected();
        
        if (!filtroVitrina && !filtroNevera && !filtroHeladera) {
            productosDisponibles = productoDAO.listarProductos();
        } else {
            for (Producto producto : productoDAO.listarProductos()) {
                if ((filtroVitrina && "Vitrina".equalsIgnoreCase(producto.getUbicacion())) ||
                    (filtroNevera && "Nevera".equalsIgnoreCase(producto.getUbicacion())) ||
                    (filtroHeladera && "Heladera".equalsIgnoreCase(producto.getUbicacion()))) {
                    productosFiltrados.add(producto);
                }
            }
            productosDisponibles = productosFiltrados;
        }
        
        actualizarVistaProductos();
    }

    
@FXML
private void finalizarVenta(ActionEvent event) {
    // Validar que hay productos en el carrito
    if (itemsCarrito.isEmpty()) {
        mostrarAlerta("Error", "No hay productos en el carrito");
        return;
    }

    String metodo = "";
    if (rbEfectivo.isSelected()) {
        metodo = "Efectivo";
    } else if (rbTransferencia.isSelected()) {
        metodo = "Transferencia";
    } else {
        mostrarAlerta("Error", "Seleccione un método de pago");
        return;
    }

    double total = calcularTotal();

    if (metodo.equalsIgnoreCase("Efectivo")) {
        // Registrar ingreso en efectivo
        boolean ingresoRegistrado = registrarIngreso(metodo, total);

        if (ingresoRegistrado) {
            descontarStock(); // ✅ Descontar stock en BD
            mostrarAlerta("Venta Exitosa", "Venta en efectivo registrada\nTotal: $" + total);
            limpiarCarrito();
        } else {
            mostrarAlerta("Error", "No se pudo registrar la venta");
        }
    } else if (metodo.equalsIgnoreCase("Transferencia")) {
        // En Sandbox simulamos la transacción como aprobada directamente
        String transactionId = wompi.crearTransaccionBancolombiaQR(total);

        if (transactionId != null) {
            // Guardar como si estuviera aprobada
            boolean ingresoRegistrado = registrarIngreso(metodo, total);

            if (ingresoRegistrado) {
                descontarStock(); // ✅ Descontar stock en BD
                mostrarAlerta("Pago Aprobado (Simulado)", "Pago confirmado\nTotal: $" + total);
                limpiarCarrito();
            } else {
                mostrarAlerta("Error", "No se pudo registrar la venta");
            }
        } else {
            mostrarAlerta("Error", "No se pudo iniciar el pago con Wompi Bancolombia QR");
        }
    }
}


    
    private void descontarStock() {
    ProductoDAO productoDAO = new ProductoDAO();

    for (ItemCarrito item : itemsCarrito) {
        Producto producto = item.producto;          // ✅ Producto del carrito
        int cantidadComprada = item.cantidad;       // ✅ Cantidad comprada

        int nuevaCantidad = producto.getCantidad() - cantidadComprada;
        if (nuevaCantidad < 0) nuevaCantidad = 0;

        // Actualizar en el objeto
        producto.setCantidad(nuevaCantidad);

        // Actualizar en BD
        boolean ok = productoDAO.actualizarStock(producto.getIdProducto(), nuevaCantidad);
        if (!ok) {
            System.err.println("❌ No se pudo actualizar stock para producto: " + producto.getNombre());
        }
    }
}





    
    private void mostrarQR(String urlQR) 
    {
        try 
        {
            Image image = new Image(urlQR);
            imgQR.setImage(image);
        } 
        catch (Exception e)    
        {
           e.printStackTrace();
        }
    }

    
    private double calcularTotal() {
        double total = 0;
        for (ItemCarrito item : itemsCarrito) {
            total += item.getSubtotal();
        }
        return total;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private boolean registrarIngreso(String metodoPago, double monto) {
    try {
        Ingresos ingreso = new Ingresos();
        ingreso.setMetodoPago(metodoPago);
        ingreso.setConcepto("Cafetería");
        ingreso.setMonto(monto);

        return ingresosDAO.insertarIngreso(ingreso);
        
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

private void actualizarStockProductos() {
    try {
        for (ItemCarrito item : itemsCarrito) {
            Producto producto = item.producto;
            int nuevaCantidad = producto.getCantidad() - item.cantidad;
            productoDAO.actualizarStock(producto.getIdProducto(), nuevaCantidad);
        }
    } catch (Exception e) {
        System.out.println("Error al actualizar stock: " + e.getMessage());
    }
}

private void limpiarCarrito() {
    actualizarStockProductos();
    itemsCarrito.clear();
    actualizarVistaCarrito();
    imgQR.setImage(null);
}
}