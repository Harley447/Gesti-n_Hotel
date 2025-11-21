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

    private void cargarProductos() {
        productosDisponibles = productoDAO.listarProductos();
    }

    private void actualizarVistaProductos() {
        contenedorProductos.getChildren().clear();

        if (productosDisponibles.isEmpty()) {
            Label lblVacio = new Label("No hay productos disponibles");
            lblVacio.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 16px;");
            contenedorProductos.getChildren().add(lblVacio);
            return;
        }

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(10));

        int col = 0, row = 0;
        int maxCols = 3;

        for (Producto p : productosDisponibles) {
            VBox productoCard = crearTarjetaProducto(p);
            grid.add(productoCard, col, row);
            col++;
            if (col >= maxCols) {
                col = 0;
                row++;
            }
        }

        contenedorProductos.getChildren().add(grid);
    }

    private VBox crearTarjetaProducto(Producto producto) {
    VBox card = new VBox(10);
    card.setAlignment(Pos.TOP_CENTER);
    card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15; "
                 + "-fx-border-color: #dfe6e9; -fx-border-radius: 10;");
    card.setPrefWidth(200);
    card.setMinHeight(300);
    card.setMaxHeight(300);

    StackPane imagenContainer = crearContenedorImagen(producto);

    Label lblNombre = new Label(producto.getNombre());
    lblNombre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
    lblNombre.setWrapText(true);
    lblNombre.setAlignment(Pos.CENTER);
    lblNombre.setMaxWidth(160);

    Label lblPrecio = new Label(String.format("$%,.0f", producto.getPrecio()));
    lblPrecio.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

    Label lblStock = new Label("Stock: " + producto.getCantidad());
    lblStock.setStyle("-fx-text-fill: #7f8c8d;");

    Button btnAgregar = new Button("Agregar al Carrito");
    btnAgregar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
    btnAgregar.setPrefWidth(150);

    btnAgregar.setOnAction(e -> agregarAlCarrito(producto));

    if (producto.getCantidad() <= 0) {
        btnAgregar.setDisable(true);
        btnAgregar.setText("Sin Stock");
    }

    card.getChildren().setAll(imagenContainer, lblNombre, lblPrecio, lblStock, btnAgregar);

    return card;
}


   private StackPane crearContenedorImagen(Producto p) {
    StackPane container = new StackPane();
    container.setPrefSize(140, 140);
    container.setMinSize(140, 140);
    container.setMaxSize(140, 140);
    container.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");

    crearPlaceholderImagen(container);

    if (p.tieneImagen()) cargarImagenEnSegundoPlano(p, container);

    return container;
}


    private void crearPlaceholderImagen(StackPane c) {
        Rectangle r = new Rectangle(120, 120);
        r.setFill(Color.web("#ecf0f1"));
        r.setStroke(Color.web("#bdc3c7"));
        r.setArcWidth(8);
        r.setArcHeight(8);

        Label l = new Label("Cargando...");
        l.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

        c.getChildren().addAll(r, l);
    }

    private void cargarImagenEnSegundoPlano(Producto p, StackPane container) {
        String url = p.getImagenUrl();

        if (cacheImagenes.containsKey(url)) {
            actualizarImagenEnUI(container, cacheImagenes.get(url));
            return;
        }

        if (imagenesCargando.contains(url)) return;
        imagenesCargando.add(url);

        new Thread(() -> {
            try {
                Image img = new Image(url, 120, 120, true, true, true);
                if (!img.isError()) {
                    cacheImagenes.put(url, img);
                    javafx.application.Platform.runLater(() -> actualizarImagenEnUI(container, img));
                }
            } finally {
                imagenesCargando.remove(url);
            }
        }).start();
    }

   private void actualizarImagenEnUI(StackPane c, Image img) {
    c.getChildren().clear();
    ImageView iv = new ImageView(img);
    iv.setFitWidth(140);
    iv.setFitHeight(140);
    iv.setPreserveRatio(true);

    Rectangle clip = new Rectangle(140, 140);
    clip.setArcWidth(8);
    clip.setArcHeight(8);
    iv.setClip(clip);

    c.getChildren().add(iv);
}


    private void agregarAlCarrito(Producto producto) {
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

    private void actualizarVistaCarrito() {
        contenedorCarrito.getChildren().clear();

        if (itemsCarrito.isEmpty()) {
            contenedorCarrito.getChildren().add(new Label("El carrito está vacío"));
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
        Label lblDetalles = new Label("Cantidad: " + item.cantidad + " x $" + item.producto.getPrecio());
        Label lblSubtotal = new Label("Subtotal: $" + item.getSubtotal());

        info.getChildren().addAll(lblNombre, lblDetalles, lblSubtotal);

        HBox controles = new HBox(5);
        controles.setAlignment(Pos.CENTER_RIGHT);

        Button btnMenos = new Button("-");
        btnMenos.setOnAction(e -> modificarCantidad(item, -1));

        Button btnMas = new Button("+");
        btnMas.setOnAction(e -> modificarCantidad(item, 1));

        Button btnEliminar = new Button("×");
        btnEliminar.setOnAction(e -> eliminarDelCarrito(item));

        controles.getChildren().addAll(btnMenos, btnMas, btnEliminar);

        h.getChildren().addAll(info, controles);
        return h;
    }

    private void modificarCantidad(ItemCarrito item, int cambio) {
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

    private void eliminarDelCarrito(ItemCarrito item) {
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
            productosDisponibles = productoDAO.listarProductos();
        } else {
            for (Producto p : productoDAO.listarProductos()) {
                if ((a && "Vitrina".equalsIgnoreCase(p.getUbicacion())) ||
                    (b && "Nevera".equalsIgnoreCase(p.getUbicacion())) ||
                    (c && "Heladera".equalsIgnoreCase(p.getUbicacion()))) {

                    filtrados.add(p);
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

    private double calcularTotal() {
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
