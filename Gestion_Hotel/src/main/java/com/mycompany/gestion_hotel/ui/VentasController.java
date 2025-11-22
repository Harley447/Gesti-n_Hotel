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
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VentasController implements Initializable {

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

    private Map<String, Image> cacheImagenes = new ConcurrentHashMap<>();
    private Set<String> imagenesCargando = Collections.synchronizedSet(new HashSet<>());

    public class ItemCarrito {
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

    public List<ItemCarrito> itemsCarrito;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ingresosDAO = new IngresosDAO();
        productoDAO = new ProductoDAO();
        productosDisponibles = new ArrayList<>();
        itemsCarrito = new ArrayList<>();

        lblTotal.setText("$0");
        rbEfectivo.setToggleGroup(metodoPago);
        rbTransferencia.setToggleGroup(metodoPago);

        cargarProductos();
        actualizarVistaProductos();
    }

    public void cargarProductos() {
        // Obtener todos los productos y filtrar los que no son de bodega
        List<Producto> todosProductos = productoDAO.listarProductos();
        productosDisponibles = new ArrayList<>();
        
        for (Producto producto : todosProductos) {
            // Excluir productos con ubicación "Bodega" (case insensitive)
            if (producto.getUbicacion() != null && 
                !producto.getUbicacion().equalsIgnoreCase("Bodega")) {
                productosDisponibles.add(producto);
            }
        }
        
        System.out.println("Productos cargados: " + productosDisponibles.size() + 
                          " (excluyendo bodega)");
    }

    public void actualizarVistaProductos() {
        contenedorProductos.getChildren().clear();
        contenedorProductos.setSpacing(15);
        contenedorProductos.setPadding(new Insets(10));

        if (productosDisponibles.isEmpty()) {
            Label lblVacio = new Label("No hay productos disponibles");
            lblVacio.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 16px;");
            contenedorProductos.getChildren().add(lblVacio);
            return;
        }

        // Usar FlowPane en lugar de GridPane para mejor flexibilidad
        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(15);
        flowPane.setVgap(15);
        flowPane.setPadding(new Insets(10));
        flowPane.setPrefWrapLength(800); // Ancho preferido antes de hacer wrap

        for (Producto p : productosDisponibles) {
            VBox productoCard = crearTarjetaProducto(p);
            flowPane.getChildren().add(productoCard);
        }

        contenedorProductos.getChildren().add(flowPane);
    }

    private VBox crearTarjetaProducto(Producto producto) {
        VBox card = new VBox(8); // Reducir spacing
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 12; "
                     + "-fx-border-color: #dfe6e9; -fx-border-radius: 10; -fx-border-width: 1;");
        card.setPrefWidth(180);
        card.setMaxWidth(180);

        StackPane imagenContainer = crearContenedorImagen(producto);

        // NOMBRE DEL PRODUCTO - Asegurar que sea visible
        Label lblNombre = new Label(producto.getNombre());
        lblNombre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        lblNombre.setWrapText(true);
        lblNombre.setAlignment(Pos.CENTER);
        lblNombre.setMaxWidth(150);
        lblNombre.setMinHeight(40);
        lblNombre.setPrefHeight(Region.USE_COMPUTED_SIZE);

        Label lblPrecio = new Label(String.format("$%,.0f", producto.getPrecio()));
        lblPrecio.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        Label lblStock = new Label("Stock: " + producto.getCantidad());
        lblStock.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");
        
        // Mostrar ubicación para debug
        Label lblUbicacion = new Label("Ubic: " + producto.getUbicacion());
        lblUbicacion.setStyle("-fx-text-fill: #3498db; -fx-font-size: 10px;");

        Button btnAgregar = new Button("Agregar al Carrito");
        btnAgregar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAgregar.setPrefWidth(140);
        btnAgregar.setMinHeight(35);

        btnAgregar.setOnAction(e -> agregarAlCarrito(producto));

        if (producto.getCantidad() <= 0) {
            btnAgregar.setDisable(true);
            btnAgregar.setText("Sin Stock");
            btnAgregar.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        }

        VBox.setMargin(imagenContainer, new Insets(0, 0, 5, 0));
        VBox.setMargin(lblNombre, new Insets(5, 0, 5, 0));
        VBox.setMargin(btnAgregar, new Insets(10, 0, 0, 0));

        card.getChildren().addAll(imagenContainer, lblNombre, lblPrecio, lblStock, lblUbicacion, btnAgregar);

        return card;
    }

    private StackPane crearContenedorImagen(Producto p) {
        StackPane container = new StackPane();
        container.setPrefSize(120, 120);
        container.setMinSize(120, 120);
        container.setMaxSize(120, 120);
        container.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");

        crearPlaceholderImagen(container);

        if (p.tieneImagen()) cargarImagenEnSegundoPlano(p, container);

        return container;
    }

    private void crearPlaceholderImagen(StackPane c) {
        Rectangle r = new Rectangle(100, 100);
        r.setFill(Color.web("#ecf0f1"));
        r.setStroke(Color.web("#bdc3c7"));
        r.setArcWidth(8);
        r.setArcHeight(8);

        Label l = new Label("Sin imagen");
        l.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");

        c.getChildren().addAll(r, l);
    }

    private void cargarImagenEnSegundoPlano(Producto p, StackPane container) {
        String url = p.getImagenUrl();
        
        if (url == null || url.trim().isEmpty()) {
            System.out.println("URL de imagen vacía para: " + p.getNombre());
            return;
        }

        if (cacheImagenes.containsKey(url)) {
            actualizarImagenEnUI(container, cacheImagenes.get(url));
            return;
        }

        if (imagenesCargando.contains(url)) return;
        imagenesCargando.add(url);

        new Thread(() -> {
            try {
                System.out.println("Cargando imagen para: " + p.getNombre() + " - URL: " + url);
                Image img = new Image(url, 100, 100, true, true, true);
                
                if (!img.isError()) {
                    cacheImagenes.put(url, img);
                    javafx.application.Platform.runLater(() -> {
                        actualizarImagenEnUI(container, img);
                        System.out.println("✓ Imagen cargada: " + p.getNombre());
                    });
                } else {
                    System.out.println("✗ Error cargando imagen: " + url);
                    javafx.application.Platform.runLater(() -> {
                        container.getChildren().clear();
                        Rectangle errorRect = new Rectangle(100, 100);
                        errorRect.setFill(Color.web("#ff6b6b"));
                        Label errorLabel = new Label("Error");
                        errorLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px;");
                        container.getChildren().addAll(errorRect, errorLabel);
                    });
                }
            } catch (Exception e) {
                System.out.println("Excepción cargando imagen: " + e.getMessage());
            } finally {
                imagenesCargando.remove(url);
            }
        }).start();
    }

    private void actualizarImagenEnUI(StackPane c, Image img) {
        c.getChildren().clear();
        ImageView iv = new ImageView(img);
        iv.setFitWidth(120);
        iv.setFitHeight(120);
        iv.setPreserveRatio(true);

        Rectangle clip = new Rectangle(120, 120);
        clip.setArcWidth(8);
        clip.setArcHeight(8);
        iv.setClip(clip);

        c.getChildren().add(iv);
    }

    public void agregarAlCarrito(Producto producto) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setHeaderText("Cantidad para: " + producto.getNombre());
        Optional<String> r = dialog.showAndWait();

        if (r.isPresent()) {
            try {
                int cantidad = Integer.parseInt(r.get());
                if (cantidad <= 0) {
                    mostrarAlerta("Error", "La cantidad debe ser mayor a 0");
                    return;
                }

                int totalEnCarrito = itemsCarrito.stream()
                        .filter(i -> i.producto.getIdProducto() == producto.getIdProducto())
                        .mapToInt(i -> i.cantidad).sum();

                if (cantidad + totalEnCarrito > producto.getCantidad()) {
                    mostrarAlerta("Stock Insuficiente", "Stock disponible: " + producto.getCantidad());
                    return;
                }

                ItemCarrito existente = itemsCarrito.stream()
                        .filter(i -> i.producto.getIdProducto() == producto.getIdProducto())
                        .findFirst().orElse(null);

                if (existente != null) {
                    existente.cantidad += cantidad;
                } else {
                    itemsCarrito.add(new ItemCarrito(producto, cantidad));
                }

                actualizarVistaCarrito();

            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "Ingrese un número válido");
            }
        }
    }

    public void actualizarVistaCarrito() {
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
            contenedorCarrito.getChildren().add(crearItemCarrito(item));
            total += item.getSubtotal();
        }

        lblTotal.setText(String.format("$%,.0f", total));
    }

    private HBox crearItemCarrito(ItemCarrito item) {
        HBox h = new HBox(10);
        h.setAlignment(Pos.CENTER_LEFT);
        h.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 10;");

        VBox info = new VBox(5);
        info.setPrefWidth(180);

        Label lblNombre = new Label(item.producto.getNombre());
        lblNombre.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label lblDetalles = new Label("Cantidad: " + item.cantidad + " x $" + item.producto.getPrecio());
        lblDetalles.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        Label lblSubtotal = new Label("Subtotal: $" + item.getSubtotal());
        lblSubtotal.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");

        info.getChildren().addAll(lblNombre, lblDetalles, lblSubtotal);

        HBox controles = new HBox(5);
        controles.setAlignment(Pos.CENTER_RIGHT);

        Button btnMenos = new Button("-");
        btnMenos.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-min-width: 30;");
        btnMenos.setOnAction(e -> modificarCantidad(item, -1));

        Button btnMas = new Button("+");
        btnMas.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-min-width: 30;");
        btnMas.setOnAction(e -> modificarCantidad(item, 1));

        Button btnEliminar = new Button("×");
        btnEliminar.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-min-width: 30;");
        btnEliminar.setOnAction(e -> eliminarDelCarrito(item));

        controles.getChildren().addAll(btnMenos, btnMas, btnEliminar);

        h.getChildren().addAll(info, controles);
        return h;
    }

    public void modificarCantidad(ItemCarrito item, int cambio) {
        int nuevaCantidad = item.cantidad + cambio;

        if (nuevaCantidad <= 0) {
            eliminarDelCarrito(item);
            return;
        }

        int stockDisponible = item.producto.getCantidad();

        if (nuevaCantidad > stockDisponible) {
            mostrarAlerta("Stock insuficiente", "Stock disponible: " + stockDisponible);
            return;
        }

        item.cantidad = nuevaCantidad;
        actualizarVistaCarrito();
    }

    public void eliminarDelCarrito(ItemCarrito item) {
        itemsCarrito.remove(item);
        actualizarVistaCarrito();
    }

    @FXML
    private void aplicarFiltro(ActionEvent e) {
        List<Producto> filtrados = new ArrayList<>();

        boolean a = chkVitrina.isSelected();
        boolean b = chkNevera.isSelected();
        boolean c = chkHeladera.isSelected();

        if (!a && !b && !c) {
            // Al no tener filtros activos, mostrar todos los productos excepto bodega
            cargarProductos();
        } else {
            // Aplicar filtros sobre todos los productos (incluyendo verificación de bodega)
            List<Producto> todosProductos = productoDAO.listarProductos();
            for (Producto p : todosProductos) {
                // Excluir productos de bodega primero
                if (p.getUbicacion() != null && !p.getUbicacion().equalsIgnoreCase("Bodega")) {
                    if ((a && "Vitrina".equalsIgnoreCase(p.getUbicacion())) ||
                        (b && "Nevera".equalsIgnoreCase(p.getUbicacion())) ||
                        (c && "Heladera".equalsIgnoreCase(p.getUbicacion()))) {

                        filtrados.add(p);
                    }
                }
            }
            productosDisponibles = filtrados;
        }

        actualizarVistaProductos();
    }

    @FXML
    private void finalizarVenta(ActionEvent e) {
        if (itemsCarrito.isEmpty()) {
            mostrarAlerta("Error", "No hay productos en el carrito");
            return;
        }

        String metodo = rbEfectivo.isSelected() ? "Efectivo" :
                        rbTransferencia.isSelected() ? "Transferencia" : "";

        if (metodo.isEmpty()) {
            mostrarAlerta("Error", "Seleccione un método de pago");
            return;
        }

        double total = calcularTotal();

        if (metodo.equals("Transferencia")) {
            String tx = wompi.crearTransaccionBancolombiaQR(total);
            if (tx == null) {
                mostrarAlerta("Error", "No se pudo iniciar el pago");
                return;
            }
        }

        if (registrarIngreso(metodo, total)) {
            descontarStock();
            mostrarAlerta("Venta Exitosa", "Total: $" + total);
            limpiarCarritoSoloUI();
        }
    }

    private void descontarStock() {
        for (ItemCarrito item : itemsCarrito) {
            int nuevaCantidad = item.producto.getCantidad() - item.cantidad;
            if (nuevaCantidad < 0) nuevaCantidad = 0;

            item.producto.setCantidad(nuevaCantidad);
            productoDAO.actualizarStock(item.producto.getIdProducto(), nuevaCantidad);
        }
    }

    public double calcularTotal() {
        return itemsCarrito.stream().mapToDouble(ItemCarrito::getSubtotal).sum();
    }

    private boolean registrarIngreso(String metodoPago, double monto) {
        try {
            Ingresos ingreso = new Ingresos();
            ingreso.setMetodoPago(metodoPago);
            ingreso.setConcepto("Cafetería");
            ingreso.setMonto(monto);
            return ingresosDAO.insertarIngreso(ingreso);
        } catch (Exception e) {
            return false;
        }
    }

    private void limpiarCarritoSoloUI() {
        itemsCarrito.clear();
        actualizarVistaCarrito();
        imgQR.setImage(null);
        cargarProductos();  
        actualizarVistaProductos();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}