package ni.edu.uam.papeleriacampus.connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final String CONFIG_FILE = "db.properties";

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(CONFIG_FILE)) {
            props.load(in);
        } catch (IOException e) {
            throw new SQLException("No se pudo leer " + CONFIG_FILE
                    + ". Créalo en la carpeta principal del proyecto a partir de db.example.properties.", e);
        }
        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password"));
    }
}
