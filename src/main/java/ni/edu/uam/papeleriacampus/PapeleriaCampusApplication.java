package ni.edu.uam.papeleriacampus;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PapeleriaCampusApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PapeleriaCampusApplication.class.getResource("papeleria-campus-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 700);
        stage.setTitle("Control y Registro");
        stage.setScene(scene);
        stage.show();
    }
}
