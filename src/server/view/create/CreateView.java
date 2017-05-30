package server.view.create;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CreateView extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Create.fxml"));

        Scene scene = new Scene(root, 300, 275);

        stage.setTitle("SLChat");
        stage.setScene(scene);
        stage.show();
    }

//    public static void main(String[] args) {
//        launch(args);
//    }
}
