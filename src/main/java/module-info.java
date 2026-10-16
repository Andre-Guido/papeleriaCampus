module ni.edu.uam.papeleriacampus {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens ni.edu.uam.papeleriacampus to javafx.fxml;
    exports ni.edu.uam.papeleriacampus;
    opens ni.edu.uam.papeleriacampus.connection to javafx.fxml;
    exports ni.edu.uam.papeleriacampus.connection to javafx.fxml;
    opens ni.edu.uam.papeleriacampus.controller to javafx.fxml;
    exports ni.edu.uam.papeleriacampus.controller to javafx.fxml;
    opens ni.edu.uam.papeleriacampus.models to javafx.fxml;
    exports ni.edu.uam.papeleriacampus.models to javafx.fxml;
}