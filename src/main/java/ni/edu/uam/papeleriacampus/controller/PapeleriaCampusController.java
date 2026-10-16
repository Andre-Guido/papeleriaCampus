package ni.edu.uam.papeleriacampus.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ni.edu.uam.papeleriacampus.connection.DatabaseConnection;
import ni.edu.uam.papeleriacampus.models.Producto;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
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
    private Button getBtnLimpiarFiltros;
    @FXML
    private Button btnRecargarRegistros;
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

    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cmbCategoria.getItems().addAll("Papelería", "Tecnología", "Accesorios");
        cmbCategoriaFiltro.getItems().addAll("Papelería", "Tecnología", "Accesorios");
        configurarTableView();
    }

    @FXML
    private void limpiarFormulario() {
        txtCodigo.clear();
        txtNombre.clear();
        cmbCategoria.setValue(null);
        txtPrecio.clear();
        txtStock.clear();
    }

    @FXML
    private void guardarProducto(){
        if (!validarFormulario()) {
            return;
        }
        String codigo =
                txtCodigo.getText().trim().toUpperCase();
        String sql = """
                INSERT INTO catalogos.productos
                (
                    codigo,
                    nombre,
                    categoria,
                    precio,
                    stock
                )
                VALUES (?, ?, ?, ?, ?)
                """;
        try (
                Connection conexion =
                        DatabaseConnection.getConnection();
                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {
            ps.setString(
                    1,
                    codigo
            );
            ps.setString(
                    2,
                    txtNombre.getText().trim()
            );
            ps.setString(
                    3,
                    cmbCategoria.getValue()
            );
            ps.setBigDecimal(
                    4,
                    new BigDecimal(
                            txtPrecio.getText().trim()
                    )
            );
            ps.setInt(
                    5,
                    Integer.parseInt(
                            txtStock.getText().trim()
                    )
            );
            ps.executeUpdate();
            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Registro guardado",
                    "El producto fue registrado correctamente."
            );
            limpiarFormulario();
            limpiarFiltros();
            buscarProducto();
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Código duplicado",
                        "El código " + codigo +
                                " ya pertenece a otro producto."
                );
                txtCodigo.requestFocus();
            } else {
                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Error de base de datos",
                        "No se pudo guardar el producto.\n" + e.getMessage()
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
        String codigo =
                txtCodigo.getText().trim().toUpperCase();
        String sql = """
                UPDATE catalogos.productos
                SET
                    codigo = ?,
                    nombre = ?,
                    categoria = ?,
                    precio = ?,
                    stock = ?
                WHERE id_producto = ?
                """;
        try (
                Connection conexion =
                        DatabaseConnection.getConnection();
                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {
            ps.setString(
                    1,
                    codigo
            );
            ps.setString(
                    2,
                    txtNombre.getText().trim()
            );
            ps.setString(
                    3,
                    cmbCategoria.getValue()
            );
            ps.setBigDecimal(
                    4,
                    new BigDecimal(
                            txtPrecio.getText().trim()
                    )
            );
            ps.setInt(
                    5,
                    Integer.parseInt(
                            txtStock.getText().trim()
                    )
            );
            ps.setInt(
                    6,
                    productoSeleccionado.getIdProducto()
            );
            int filasAfectadas =
                    ps.executeUpdate();
            if (filasAfectadas > 0) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Registro actualizado",
                        "El producto fue actualizado correctamente."
                );
                limpiarFormulario();
                limpiarFiltros();
                buscarProducto();
            }
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Código duplicado",
                        "El código " + codigo +
                                " ya pertenece a otro producto."
                );
                txtCodigo.requestFocus();
            } else {
                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Error de base de datos",
                        "No se pudo actualizar el producto.\n" + e.getMessage()
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
        String sql = """
                DELETE FROM catalogos.productos
                WHERE id_producto = ?
                """;
        try (
                Connection conexion =
                        DatabaseConnection.getConnection();
                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {
            ps.setInt(
                    1,
                    productoSeleccionado.getIdProducto()
            );
            int filasAfectadas =
                    ps.executeUpdate();
            if (filasAfectadas > 0) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Producto eliminado",
                        "El producto fue eliminado correctamente."
                );
                limpiarFormulario();
                limpiarFiltros();
                buscarProducto();
            }
        } catch (SQLException e) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error de base de datos",
                    "No se pudo eliminar el producto.\n" + e.getMessage()
            );
        }
    }

    @FXML
    private void buscarProducto(){
        listaProductos.clear();
        StringBuilder sql = new StringBuilder("""
                SELECT
                    p.id_producto,
                    p.codigo,
                    p.nombre,
                    p.categoria,
                    p.precio,
                    p.stock
                FROM catalogos.productos p
                WHERE 1 = 1
                """);
        List<Object> parametros =
                new ArrayList<>();
        String codigo =
                txtCodigoFiltro.getText().trim();
        if (!codigo.isBlank()) {
            sql.append("""
                    AND p.codigo ILIKE ?
                    """);
            parametros.add(
                    "%" + codigo + "%"
            );
        }
        String nombre =
                txtNombreFiltro.getText().trim();
        if (!nombre.isBlank()) {
            sql.append("""
                    AND p.nombre ILIKE ?
                    """);
            parametros.add(
                    "%" + nombre + "%"
            );
        }
        String categoria =
                cmbCategoriaFiltro.getValue();
        if (categoria != null) {
            sql.append("""
                    AND p.categoria = ?
                    """);
            parametros.add(categoria);
        }
        String textoPrecio =
                txtPrecioFiltro.getText().trim();
        if (!textoPrecio.isBlank()) {
            BigDecimal precio;
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
            sql.append("""
                    AND p.precio = ?
                    """);
            parametros.add(precio);
        }
        if (chkExistencia.isSelected()) {
            sql.append("""
                    AND p.stock > 0
                    """);
        }
        sql.append("""
                ORDER BY p.id_producto ASC
                """);
        try (
                Connection conexion =
                        DatabaseConnection.getConnection();
                PreparedStatement ps =
                        conexion.prepareStatement(
                                sql.toString()
                        )
        ) {
            for (
                    int i = 0;
                    i < parametros.size();
                    i++
            ) {
                ps.setObject(
                        i + 1,
                        parametros.get(i)
                );
            }
            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {
                while (rs.next()) {
                    listaProductos.add(
                            new Producto(
                                    rs.getInt("id_producto"),
                                    rs.getString("codigo"),
                                    rs.getString("nombre"),
                                    rs.getString("categoria"),
                                    rs.getBigDecimal("precio"),
                                    rs.getInt("stock")
                            )
                    );
                }
            }
            tblProductos.setItems(
                    listaProductos
            );
            if (listaProductos.isEmpty()) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Sin resultados",
                        "No existen productos que coincidan con los filtros aplicados."
                );
            }
        } catch (SQLException e) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error de base de datos",
                    "No se pudo realizar la búsqueda.\n" + e.getMessage()
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
    }

    @FXML
    private void recargarRegistros(){
        tblProductos.refresh();
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
        return true;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje){
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
