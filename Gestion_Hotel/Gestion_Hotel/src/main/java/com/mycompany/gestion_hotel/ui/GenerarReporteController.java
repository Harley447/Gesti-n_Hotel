package com.mycompany.gestion_hotel.ui;

import com.mycompany.gestion_hotel.conexion.ConexionBD;
import com.mycompany.gestion_hotel.dao.DetalleReporteDAO;
import com.mycompany.gestion_hotel.dao.ReporteDAO;
import com.mycompany.gestion_hotel.modelo.DetalleReporte;
import com.mycompany.gestion_hotel.modelo.Reporte;
import com.mycompany.gestion_hotel.modelo.Transacciones;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class GenerarReporteController implements Initializable {

    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private Button btnConsultar;
    @FXML private Button btnGuardar;

    @FXML private TableView<Transacciones> tvTransacciones;
    @FXML private TableColumn<Transacciones, Integer> colId;
    @FXML private TableColumn<Transacciones, Date> colFecha;
    @FXML private TableColumn<Transacciones, Double> colMonto;
    @FXML private TableColumn<Transacciones, String> colDescripcion;
    @FXML private TableColumn<Transacciones, String> colTipo;

    @FXML private Label lblIngresos;
    @FXML private Label lblEgresos;
    @FXML private Label lblBalance;

    private ObservableList<Transacciones> lista = FXCollections.observableArrayList();

    // DAOs
    private final ReporteDAO reporteDAO = new ReporteDAO();
    private final DetalleReporteDAO detalleDAO = new DetalleReporteDAO();

    // Si quieres usar el id del usuario que genera el reporte:
    // asigna dinámicamente con un setter si el controller recibe el usuario.
    // Por ahora ponemos administrador = 1
    private final Integer ID_GENERADO_POR = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColumnas();
        tvTransacciones.setItems(lista);
        lblIngresos.setText("$ 0.00");
        lblEgresos.setText("$ 0.00");
        lblBalance.setText("$ 0.00");
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getIdTransaccion()).asObject());
        colFecha.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getFecha()));
        colMonto.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getMonto()).asObject());
        colDescripcion.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDescripcion()));
        colTipo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTipo()));
    }

    @FXML
    public void consultarTransacciones() {
        LocalDate ldInicio = dpFechaInicio.getValue();
        LocalDate ldFin = dpFechaFin.getValue();

        if (ldInicio == null || ldFin == null) {
            mostrarAlerta("Debe seleccionar fecha inicio y fecha fin.");
            return;
        }

        if (ldFin.isBefore(ldInicio)) {
            mostrarAlerta("La fecha fin no puede ser anterior a la fecha inicio.");
            return;
        }

        Date sqlInicio = Date.valueOf(ldInicio);
        Date sqlFin = Date.valueOf(ldFin);

        lista.clear();

        String sql = "SELECT idTransaccion, fecha, monto, descripcion, tipo, registradoPor " +
                     "FROM Transacciones " +
                     "WHERE fecha BETWEEN '" + sqlInicio + "' AND '" + sqlFin + "' " +
                     "ORDER BY fecha ASC";

        try {
            ConexionBD conexion = new ConexionBD();
            ResultSet rs = conexion.consultarBD(sql);

            double totalIngresos = 0.0;
            double totalEgresos = 0.0;

            boolean tieneIngresos = false;
            boolean tieneEgresos = false;

            while (rs != null && rs.next()) {
                Transacciones t = new Transacciones(
                        rs.getInt("idTransaccion"),
                        rs.getDate("fecha"),
                        rs.getDouble("monto"),
                        rs.getString("descripcion"),
                        rs.getString("tipo"),
                        rs.getObject("registradoPor") != null ? rs.getInt("registradoPor") : null
                );

                lista.add(t);

                if ("Ingreso".equalsIgnoreCase(t.getTipo())) {
                    totalIngresos += t.getMonto();
                    tieneIngresos = true;
                } else if ("Egreso".equalsIgnoreCase(t.getTipo())) {
                    totalEgresos += t.getMonto();
                    tieneEgresos = true;
                }
            }

            if (rs != null) rs.close();
            conexion.closeConnection();

            lblIngresos.setText(String.format("$ %.2f", totalIngresos));
            lblEgresos.setText(String.format("$ %.2f", totalEgresos));
            lblBalance.setText(String.format("$ %.2f", (totalIngresos - totalEgresos)));

            // Determinar tipo de reporte según la opción C (automático)
            String tipoReporte;
            if (tieneIngresos && !tieneEgresos) tipoReporte = "Ingresos";
            else if (!tieneIngresos && tieneEgresos) tipoReporte = "Egresos";
            else tipoReporte = "Completo";

            // Guardamos el tipo calculado en la propiedad userData del botón para usar al guardar
            btnGuardar.setUserData(tipoReporte);

            if (lista.isEmpty()) {
                mostrarAlerta("No se encontraron transacciones en el rango seleccionado.");
            }

        } catch (SQLException ex) {
            mostrarAlerta("Error al consultar transacciones: " + ex.getMessage());
        }
    }

    @FXML
    public void guardarReporte() {
        if (lista.isEmpty()) {
            mostrarAlerta("No hay transacciones para guardar en el reporte.");
            return;
        }

        // Obtener fechas
        LocalDate ldInicio = dpFechaInicio.getValue();
        LocalDate ldFin = dpFechaFin.getValue();
        Date sqlInicio = Date.valueOf(ldInicio);
        Date sqlFin = Date.valueOf(ldFin);

        // Tipo calculado al consultar
        String tipoReporte = (String) btnGuardar.getUserData();
        if (tipoReporte == null) tipoReporte = "Completo";

        // Crear objeto Reporte y guardarlo, obteniendo el id generado
        Reporte reporte = new Reporte();
        reporte.setTipo(tipoReporte);
        reporte.setFechaInicio(sqlInicio);
        reporte.setFechaFin(sqlFin);
        reporte.setGeneradoPor(ID_GENERADO_POR);

        // Usamos método que inserta y devuelve id (ver nota en DAO)
        int idReporte = reporteDAO.insertarReporteYObtenerID(reporte);
        if (idReporte <= 0) {
            mostrarAlerta("Error al guardar el reporte.");
            return;
        }

        // Guardar cada detalle
        boolean todoOk = true;
        for (Transacciones t : lista) {
            DetalleReporte detalle = new DetalleReporte(idReporte, t.getIdTransaccion());
            boolean ok = detalleDAO.insertarDetalle(detalle);
            if (!ok) {
                todoOk = false;
            }
        }

        if (todoOk) {
            mostrarInfo("Reporte guardado correctamente (id=" + idReporte + ").");
        } else {
            mostrarAlerta("El reporte se guardó pero hubo errores al insertar algunos detalles.");
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}
