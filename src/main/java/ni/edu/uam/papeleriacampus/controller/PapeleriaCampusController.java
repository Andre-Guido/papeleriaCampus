package ni.edu.uam.papeleriacampus.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ni.edu.uam.papeleriacampus.dao.ProductoDAO;
import ni.edu.uam.papeleriacampus.models.Producto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class PapeleriaCampusController {
    @FXML
    private TextField txtCodigo;
    @FXML
    private TextField txtNombre;
    @FXML
    private ComboBox<String> cmbCategoria;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtStock;
    @FXML
    private Button btnLimpiarFormulario;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btnEliminar;
    @FXML
    private TextField txtCodigoFiltro;
    @FXML
    private TextField txtNombreFiltro;
    @FXML
    private ComboBox<String> cmbCategoriaFiltro;
    @FXML
    private TextField txtPrecioFiltro;
    @FXML
    private CheckBox chkExistencia;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnLimpiarFiltros;
    @FXML
    private Button btnRecargar;
    @FXML
    private TableView<Producto>  tblProductos;
    @FXML
    private TableColumn<Producto,Integer> colId;
    @FXML
    private TableColumn<Producto,String> colCodigo;
    @FXML
    private TableColumn<Producto,String> colNombre;
    @FXML
    private TableColumn<Producto,String> colCategoria;
    @FXML
    private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML
    private TableColumn<Producto,Integer> colStock;

    private static final List<String> CATEGORIAS = List.of("Papelería", "Tecnología", "Accesorios");

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();

    private final ProductoDAO productoDAO = new ProductoDAO();

    @FXML
    private void initialize() {
        configurarCmbCategoria();
        configurarCmbCategoriaFiltro();
        configurarTableView();
        cargarProductos();
    }

    @FXML
    private void limpiarFormulario() {
        txtCodigo.clear();
        txtNombre.clear();
        cmbCategoria.setValue(null);
        txtPrecio.clear();
        txtStock.clear();
        tblProductos.getSelectionModel().clearSelection();
    }

    @FXML
    private void guardarProducto(){
        if (!validarFormulario()) {
            return;
        }
        Producto producto =
                obtenerProductoFormulario(0);
        try {
            productoDAO.insertar(producto);
            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Registro guardado",
                    "El producto fue registrado correctamente."
            );
            limpiarFormulario();
            limpiarFiltros();
            cargarProductos();
        } catch (SQLException e) {
            registrarError("Guardar producto", e);
            if ("23505".equals(e.getSQLState())) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Código duplicado",
                        "El código " + producto.getCodigo() +
                                " ya pertenece a otro producto."
                );
                txtCodigo.requestFocus();
            } else {
                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Error de base de datos",
                        "No se pudo guardar el producto.\n" + mensajeError(e)
                );
            }
        }
    }

    @FXML
    private void actualizarProducto(){
        Producto productoSeleccionado =
                tblProductos.getSelectionModel()
                        .getSelectedItem();
        if (productoSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Producto no seleccionado",
                    "Seleccione un producto de la tabla antes de actualizar."
            );
            return;
        }
        if (!validarFormulario()) {
            return;
        }
        Producto producto =
                obtenerProductoFormulario(
                        productoSeleccionado.getIdProducto()
                );
        try {
            int filasAfectadas =
                    productoDAO.actualizar(producto);
            if (filasAfectadas > 0) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Registro actualizado",
                        "El producto fue actualizado correctamente."
                );
                limpiarFormulario();
                limpiarFiltros();
                cargarProductos();
            } else {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Producto no encontrado",
                        "El producto ya no existe en la base de datos, por lo que no se pudo actualizar.\n"
                                + "La lista de productos se recargará."
                );
                limpiarFormulario();
                recargarRegistros();
            }
        } catch (SQLException e) {
            registrarError("Actualizar producto", e);
            if ("23505".equals(e.getSQLState())) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Código duplicado",
                        "El código " + producto.getCodigo() +
                                " ya pertenece a otro producto."
                );
                txtCodigo.requestFocus();
            } else {
                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Error de base de datos",
                        "No se pudo actualizar el producto.\n" + mensajeError(e)
                );
            }
        }
    }

    @FXML
    private void eliminarProducto(){
        Producto productoSeleccionado =
                tblProductos.getSelectionModel()
                        .getSelectedItem();
        if (productoSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Producto no seleccionado",
                    "Seleccione un producto de la tabla antes de eliminarlo."
            );
            return;
        }
        Alert confirmacion =
                new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle(
                "Confirmar eliminación"
        );
        confirmacion.setHeaderText(
                "Eliminar producto"
        );
        confirmacion.setContentText(
                "¿Desea eliminar el producto "
                        + productoSeleccionado.getNombre()
                        + "? Esta acción no se puede deshacer."
        );
        Optional<ButtonType> respuesta =
                confirmacion.showAndWait();
        if (
                respuesta.isEmpty()
                        ||
                        respuesta.get() != ButtonType.OK
        ) {
            return;
        }
        try {
            int filasAfectadas =
                    productoDAO.eliminar(
                            productoSeleccionado.getIdProducto()
                    );
            if (filasAfectadas > 0) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Producto eliminado",
                        "El producto fue eliminado correctamente."
                );
                limpiarFormulario();
                limpiarFiltros();
                cargarProductos();
            } else {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Producto no encontrado",
                        "El producto ya no existe en la base de datos, por lo que no se pudo eliminar.\n"
                                + "La lista de productos se recargará."
                );
                limpiarFormulario();
                recargarRegistros();
            }
        } catch (SQLException e) {
            registrarError("Eliminar producto", e);
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error de base de datos",
                    "No se pudo eliminar el producto.\n" + mensajeError(e)
            );
        }
    }

    @FXML
    private void buscarProducto(){
        BigDecimal precio = null;
        String textoPrecio =
                txtPrecioFiltro.getText().trim();
        if (!textoPrecio.isBlank()) {
            try {
                precio =
                        new BigDecimal(textoPrecio);
            } catch (NumberFormatException e) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Precio incorrecto",
                        "El precio debe ser numérico."
                );
                txtPrecioFiltro.requestFocus();
                return;
            }
            if (
                    precio.compareTo(
                            BigDecimal.ZERO
                    ) < 0
            ) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Precio incorrecto",
                        "El precio no puede ser negativo."
                );
                txtPrecioFiltro.requestFocus();
                return;
            }
        }
        listaProductos.clear();
        try {
            listaProductos.addAll(
                    productoDAO.buscar(
                            txtCodigoFiltro.getText().trim(),
                            txtNombreFiltro.getText().trim(),
                            cmbCategoriaFiltro.getValue(),
                            precio,
                            chkExistencia.isSelected()
                    )
            );
            if (listaProductos.isEmpty()) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Sin resultados",
                        "No existen productos que coincidan con los filtros aplicados."
                );
            }
        } catch (SQLException e) {
            registrarError("Buscar productos", e);
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error de base de datos",
                    "No se pudo realizar la búsqueda.\n" + mensajeError(e)
            );
        }
    }

    @FXML
    private void filtrarExistencia(){
        buscarProducto();
    }

    @FXML
    private void limpiarFiltros(){
        txtCodigoFiltro.clear();
        txtNombreFiltro.clear();
        cmbCategoriaFiltro.setValue(null);
        txtPrecioFiltro.clear();
        chkExistencia.setSelected(false);
        cargarProductos();
    }

    @FXML
    private void recargarRegistros(){
        limpiarFiltros();
        cargarProductos();
    }

    private void cargarProductos(){
        listaProductos.clear();
        try {
            listaProductos.addAll(
                    productoDAO.listar()
            );
        } catch (SQLException e) {
            registrarError("Cargar productos", e);
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error de base de datos",
                    "No se pudieron cargar los productos.\n" + mensajeError(e)
            );
        }
    }

    private void configurarCmbCategoria(){
        cmbCategoria.getItems().addAll(CATEGORIAS);
    }

    private void configurarCmbCategoriaFiltro(){
        cmbCategoriaFiltro.getItems().addAll(CATEGORIAS);
    }

    private void configurarTableView(){
        colId.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getIdProducto()
                ).asObject()
        );
        colCodigo.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCodigo()
                ));
        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getNombre()
                )
        );
        colCategoria.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCategoria()
                )
        );
        colPrecio.setCellValueFactory(data ->
                new SimpleObjectProperty<>(
                        data.getValue().getPrecio()
                )
        );
        colStock.setCellValueFactory( data ->
                new SimpleIntegerProperty(
                        data.getValue().getStock()
                ).asObject()
        );
        tblProductos.setItems(listaProductos);
        tblProductos.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, seleccionado) -> {
                    if (seleccionado != null) {
                        txtCodigo.setText(seleccionado.getCodigo());
                        txtNombre.setText(seleccionado.getNombre());
                        cmbCategoria.setValue(seleccionado.getCategoria());
                        txtPrecio.setText(seleccionado.getPrecio().toPlainString());
                        txtStock.setText(String.valueOf(seleccionado.getStock()));
                    }
                }
        );
    }

    private boolean validarFormulario(){
        if (!campoTieneDatos(txtCodigo, "Ingrese el código del producto.")) {
            return false;
        }
        if (txtCodigo.getText().trim().length() > 10) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Código incorrecto",
                    "El código no puede tener más de 10 caracteres."
            );
            txtCodigo.requestFocus();
            return false;
        }
        if (!campoTieneDatos(txtNombre, "Ingrese el nombre del producto.")) {
            return false;
        }
        int largoNombre = txtNombre.getText().trim().length();
        if (largoNombre < 3 || largoNombre > 80) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Nombre incorrecto",
                    "El nombre debe tener entre 3 y 80 caracteres."
            );
            txtNombre.requestFocus();
            return false;
        }
        if (cmbCategoria.getValue() == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Campo vacío",
                    "Seleccione una categoría."
            );
            cmbCategoria.requestFocus();
            return false;
        }
        if (!campoTieneDatos(txtPrecio, "Ingrese el precio del producto.")) {
            return false;
        }
        BigDecimal precio;
        try {
            precio = new BigDecimal(txtPrecio.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Tipo de dato incorrecto",
                    "El precio debe ser numérico."
            );
            txtPrecio.requestFocus();
            return false;
        }
        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Valor incorrecto",
                    "El precio debe ser mayor que 0."
            );
            txtPrecio.requestFocus();
            return false;
        }
        if (precio.stripTrailingZeros().scale() > 2) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Valor incorrecto",
                    "El precio no puede tener más de 2 decimales."
            );
            txtPrecio.requestFocus();
            return false;
        }
        if (precio.compareTo(new BigDecimal("99999999.99")) > 0) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Valor incorrecto",
                    "El precio no puede ser mayor que 99999999.99."
            );
            txtPrecio.requestFocus();
            return false;
        }
        if (!campoTieneDatos(txtStock, "Ingrese el stock del producto.")) {
            return false;
        }
        BigInteger stock;
        try {
            stock = new BigInteger(txtStock.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Tipo de dato incorrecto",
                    "El stock debe ser un número entero."
            );
            txtStock.requestFocus();
            return false;
        }
        if (stock.signum() < 0) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Valor negativo",
                    "El stock no puede ser negativo."
            );
            txtStock.requestFocus();
            return false;
        }
        if (stock.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Valor incorrecto",
                    "El stock no puede ser mayor que 2147483647."
            );
            txtStock.requestFocus();
            return false;
        }
        return true;
    }

    private Producto obtenerProductoFormulario(int idProducto){
        return new Producto(
                idProducto,
                txtCodigo.getText().trim().toUpperCase(),
                txtNombre.getText().trim(),
                cmbCategoria.getValue(),
                new BigDecimal(txtPrecio.getText().trim()),
                Integer.parseInt(txtStock.getText().trim())
        );
    }

    private boolean campoTieneDatos(TextField campo, String mensaje){
        if (campo.getText().isBlank()) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Campo vacío",
                    mensaje
            );
            campo.requestFocus();
            return false;
        }
        return true;
    }

    private void registrarError(String operacion, SQLException e){
        System.err.println(
                "[" + LocalDateTime.now().format(FORMATO_FECHA) + "] ERROR BD | "
                        + operacion
                        + " | SQLState: " + e.getSQLState()
                        + " | " + e.getMessage()
        );
    }

    private String mensajeError(SQLException e){
        String estado = e.getSQLState();
        if (
                estado == null
                        || estado.startsWith("08")
                        || estado.startsWith("28")
                        || estado.equals("3D000")
        ) {
            return "No se pudo conectar con la base de datos. Intente de nuevo más tarde.";
        }
        if (estado.startsWith("22") || estado.startsWith("23")) {
            return "Los datos no cumplen las reglas de la base de datos.";
        }
        return "Ocurrió un error inesperado. Intente de nuevo.";
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje){
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
