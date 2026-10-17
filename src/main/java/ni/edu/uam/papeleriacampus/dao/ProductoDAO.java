package ni.edu.uam.papeleriacampus.dao;

import ni.edu.uam.papeleriacampus.connection.DatabaseConnection;
import ni.edu.uam.papeleriacampus.models.Producto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public List<Producto> listar() throws SQLException {
        String sql = """
                SELECT
                    p.id_producto,
                    p.codigo,
                    p.nombre,
                    p.categoria,
                    p.precio,
                    p.stock
                FROM catalogos.productos p
                ORDER BY p.id_producto ASC
                """;
        return consultarProductos(
                sql,
                List.of()
        );
    }

    public List<Producto> buscar(String codigo, String nombre, String categoria,
                                 BigDecimal precio, boolean soloConExistencias) throws SQLException {
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
        if (!codigo.isBlank()) {
            sql.append("""
                    AND p.codigo ILIKE ?
                    """);
            parametros.add(
                    "%" + codigo + "%"
            );
        }
        if (!nombre.isBlank()) {
            sql.append("""
                    AND p.nombre ILIKE ?
                    """);
            parametros.add(
                    "%" + nombre + "%"
            );
        }
        if (categoria != null) {
            sql.append("""
                    AND p.categoria = ?
                    """);
            parametros.add(categoria);
        }
        if (precio != null) {
            sql.append("""
                    AND p.precio = ?
                    """);
            parametros.add(precio);
        }
        if (soloConExistencias) {
            sql.append("""
                    AND p.stock > 0
                    """);
        }
        sql.append("""
                ORDER BY p.id_producto ASC
                """);
        return consultarProductos(
                sql.toString(),
                parametros
        );
    }

    public void insertar(Producto producto) throws SQLException {
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
                    producto.getCodigo()
            );
            ps.setString(
                    2,
                    producto.getNombre()
            );
            ps.setString(
                    3,
                    producto.getCategoria()
            );
            ps.setBigDecimal(
                    4,
                    producto.getPrecio()
            );
            ps.setInt(
                    5,
                    producto.getStock()
            );
            ps.executeUpdate();
        }
    }

    public int actualizar(Producto producto) throws SQLException {
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
                    producto.getCodigo()
            );
            ps.setString(
                    2,
                    producto.getNombre()
            );
            ps.setString(
                    3,
                    producto.getCategoria()
            );
            ps.setBigDecimal(
                    4,
                    producto.getPrecio()
            );
            ps.setInt(
                    5,
                    producto.getStock()
            );
            ps.setInt(
                    6,
                    producto.getIdProducto()
            );
            return ps.executeUpdate();
        }
    }

    public int eliminar(int idProducto) throws SQLException {
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
                    idProducto
            );
            return ps.executeUpdate();
        }
    }

    private List<Producto> consultarProductos(String sql, List<Object> parametros)
            throws SQLException {
        List<Producto> productos = new ArrayList<>();
        try (
                Connection conexion =
                        DatabaseConnection.getConnection();
                PreparedStatement ps =
                        conexion.prepareStatement(sql)
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
                    productos.add(
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
        }
        return productos;
    }
}
